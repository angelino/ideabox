(ns ideabox.core
  (:require [buddy.auth :refer [authenticated?]]
            [buddy.auth.accessrules :refer [error
                                            success
                                            wrap-access-rules]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication
                                           wrap-authorization]]
            [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [iapetos.collector.jvm :as jvm]
            [iapetos.collector.ring :as ring]
            [iapetos.core :as prometheus]
            [ideabox.auth.handler :refer :all]
            [ideabox.config :as config]
            [ideabox.ideas.handler :refer :all]
            [ideabox.shared.handler :refer :all]
            [ideabox.shared.store :refer [init-database]]
            [ideabox.shared.url :refer [login-url]]
            [ideabox.tags.handler :refer :all]
            [ideabox.users.handler :refer :all]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.nested-params :refer [wrap-nested-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.util.response :refer [redirect]]))

;; Prometheus setup
;;
(defonce prometheus-registry
  (-> (prometheus/collector-registry)
      (jvm/initialize)
      (ring/initialize)))

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
    (POST "/:id/unarchive" [] handle-unarchive-idea)
    (POST "/:id/like" [] handle-like-idea)
    (DELETE "/:id/like" [] handle-unlike-idea))
  (GET "/users/:user-id/tags" [] handle-index-tag)
  (GET "/users/:user-id/archive" [] handle-index-archive)
  (GET "/auth/login" [] handle-login)
  (GET "/auth/logout" [] handle-logout)
  (GET "/auth/signup" [] handle-new-user)
  (POST "/auth/sessions" [] handle-create-session)
  (POST "/auth/registrations" [] handle-create-user)
  (GET "/" [] handle-home)
  (not-found handle-not-found))

;; Wrappers

(def sim-methods {"DELETE" :delete "PUT" :put})

(defn wrap-sim-methods [handler]
  (fn [req]
    (if-let [method (and (:post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (handler (assoc req :request-method method))
      (handler req))))

(defn wrap-database [handler]
  (fn [req]
    (handler (assoc req :ideabox/db config/db))))

;; App config

(defn unauthorized-handler
  [request metadata]
  (cond
    ;; If request is authenticated, raise 403 instead
    ;; of 401 (because user is authenticated but permission
    ;; denied is raised).
    (authenticated? request)
    {:status 403
     :headers {}
     :body "Permission denied"}
    ;; In other cases, redirect the user to login page.
    :else
    (let [current-url (:uri request)]
      (redirect (login-url)))))

(def backend (backends/session {:unauthorized-handler unauthorized-handler}))

(defn any-access [req]
  (success))

(defn authenticated-access [req]
  (if (authenticated? req)
    (success)
    (error "Only authenticated users allowed")))

(defn authorized-user [req]
  (let [current-user-id (get-in req [:identity :id])
        user-id (java.util.UUID/fromString (get-in req [:match-params :user-id]))]
    (if-not (= current-user-id user-id)
      (error)
      (success))))

(defn on-error [request value]
  {:status 403
   :headers {}
   :body (str "Not Authorized ;)" " " value)})

(def rules [{;:pattern #"^/auth$"
             :uri     "/auth/*"
             :handler any-access}
            {;:pattern #"^/metrics"
             :uri     "/metrics"
             :handler any-access}
            {;:pattern #"^/users/.*"
             :uri      "/users/:user-id/*"
             :handler  {:and [authenticated-access authorized-user]}
             :redirect (login-url)}])

(def app
  (-> app-routes
      wrap-database
      wrap-sim-methods
      (wrap-access-rules {:rules rules :on-error on-error})
      (wrap-authorization backend)
      (wrap-authentication backend)
      wrap-session
      wrap-keyword-params
      wrap-nested-params
      wrap-params
      (wrap-resource "public")
      ;; it exposes the metrics to Prometheus scraping process.
      (ring/wrap-metrics prometheus-registry {:path "/metrics"})))

(defn on-startup []
  (try
    (println "Initializing the database...")
    (init-database config/db)
    (println "DONE.")
    (catch Exception e
      (println "Not possible initialize the database")
      (println e))))

(defn -main [& [port]]
  (on-startup)
  (let [port (Integer. (or port (:port config/server)))]
    (println "Server running on port:" port)
    (jetty/run-jetty app {:port port :join? false})))
