(ns ideabox.core
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.nested-params :refer [wrap-nested-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ideabox.handler :refer :all]))

;; (s/def ::id uuid?)
;; (s/def ::title (s/and string? #(<= (count %) 255))
;; (s/def ::description (s/and string? #(<= (count %) 4000))

;; (s/def ::idea (s/keys :req [::title ::description]
;;                       :opt [::id]))

;; Routes

(defroutes app-routes
  (GET "/" [] handle-index-idea)
  (GET "/:id/edit" [] handle-edit-idea)
  (POST "/" [] handle-create-idea)
  (PUT "/:id" [] handle-update-idea)
  (DELETE "/:id" [] handle-delete-idea)
  (not-found handle-not-found))

;; Wrappers

(def sim-methods {"DELETE" :delete "PUT" :put})

(defn wrap-sim-methods [handler]
  (fn [req]
    (if-let [method (and (:post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (handler (assoc req :request-method method))
      (handler req))))

(def db-spec {:connection-uri "jdbc:h2:~/test"
              :user "sa"
              :password ""})

(defn wrap-database [handler]
  (fn [req]
    (handler (assoc req :ideabox/db db-spec))))

;; App config

(def app
  (-> app-routes
      wrap-database
      wrap-sim-methods
      wrap-keyword-params
      wrap-nested-params
      wrap-params
      (wrap-resource "public")))
