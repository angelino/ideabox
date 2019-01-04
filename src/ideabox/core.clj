(ns ideabox.core
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.nested-params :refer [wrap-nested-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [ideabox.shared.store :refer [init-database]]
            [ideabox.shared.handler :refer :all]
            [ideabox.ideas.handler :refer :all]
            [ideabox.users.handler :refer :all]))

;; (s/def ::id uuid?)
;; (s/def ::title (s/and string? #(<= (count %) 255))
;; (s/def ::description (s/and string? #(<= (count %) 4000))

;; (s/def ::idea (s/keys :req [::title ::description]
;;                       :opt [::id]))

;; Routes

(defroutes app-routes
  (context "/users/:user-id/ideas" [user-id]
           (GET "/" [] handle-index-idea)
           (GET "/new" [] handle-new-idea)
           (POST "/" [] handle-create-idea)
           (GET "/:id/edit" [] handle-edit-idea)
           (PUT "/:id" [] handle-update-idea)
           (DELETE "/:id" [] handle-delete-idea)
           (POST "/:id/archive" [] handle-archive-idea)
           (POST "/:id/like" [] handle-like-idea)
           (DELETE "/:id/like" [] handle-unlike-idea))
  (GET "/users/:user-id/archive" [] handle-index-archive)
  ;;(GET "/login" [] handle-user-login)
  (GET "/users/new" [] handle-new-user)
  (POST "/users" [] handle-create-user)
  (not-found handle-not-found))

;; Wrappers

(def sim-methods {"DELETE" :delete "PUT" :put})

(defn wrap-sim-methods [handler]
  (fn [req]
    (if-let [method (and (:post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (handler (assoc req :request-method method))
      (handler req))))

(def db (or (env :jdbc-database-url) ;; see: https://devcenter.heroku.com/articles/connecting-to-relational-databases-on-heroku-with-java#using-the-jdbc_database_url
            {:connection-uri (env :database-connection-uri)
             :user (env :database-user)
             :password (env :database-password)}))

(defn wrap-database [handler]
  (fn [req]
    (handler (assoc req :ideabox/db db))))

;; App config

(def app
  (-> app-routes
      wrap-database
      wrap-sim-methods
      wrap-keyword-params
      wrap-nested-params
      wrap-params
      (wrap-resource "public")))

(defn on-startup []
  (try
    (println "Initializing the database...")
    (init-database db)
    (println "DONE.")
    (catch Exception e
      (println "Not possible initialize the database")
      (println e))))

(defn -main [& [port]]
  (on-startup)
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))
