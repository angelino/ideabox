(ns ideabox.core
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [redirect
                                        response]]
            [ideabox.store :as store]
            [ideabox.views :refer [index-page
                                   edit-page
                                   error-page]]))

;; (s/def ::id uuid?)
;; (s/def ::title (s/and string? #(<= (count %) 255))
;; (s/def ::description (s/and string? #(<= (count %) 4000))

;; (s/def ::idea (s/keys :req [::title ::description]
;;                       :opt [::id]))

;; Models/Store

(defn params->idea [params]
  {:title (get params "idea-title")
   :description (get params "idea-description")})

;; Handlers

(defn handle-create-idea [req]
  (let [db (:ideabox/db req)
        idea (params->idea (get-in req [:params]))]
    (store/create-idea! db idea)
    (redirect "/")))

(defn handle-update-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))
        idea (params->idea (get-in req [:params]))]
    (store/update-idea! db (assoc idea :id id))
    (redirect "/")))

(defn handle-delete-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/remove-idea! db id)
    (redirect "/")))

(defn handle-index-idea [req]
  (let [db (:ideabox/db req)]
    (-> (store/read-ideas db)
        (index-page)
        (response))))

(defn handle-edit-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (-> (store/find-idea db id)
        (edit-page)
        (response))))

;; Routes

(defroutes app-routes
  (GET "/" [] handle-index-idea)
  (GET "/:id/edit" [] handle-edit-idea)
  (POST "/" [] handle-create-idea)
  (PUT "/:id" [] handle-update-idea)
  (DELETE "/:id" [] handle-delete-idea)
  (not-found error-page))

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
      wrap-params
      (wrap-resource "public")))
