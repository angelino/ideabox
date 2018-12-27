(ns ideabox.core
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [redirect
                                        response]]
            [ideabox.views :refer [index-page
                                   edit-page
                                   error-page]]))

;; (s/def ::title string?)
;; (s/def ::description string?)

;; (s/def ::idea (s/keys :req [::title ::description]))

;; Models/Store

(defn params->idea [params]
  {:title (get params "idea-title")
   :description (get params "idea-description")})

(defonce database (atom {}))

(defn remove-idea! [id]
  (swap! database dissoc id))

(defn save-idea! [idea]
  ;; FIXME: Change to use a database or file (edn)
  (let [id (or (:id idea)
               (java.util.UUID/randomUUID))
        entity (assoc idea :id id)]
    (swap! database assoc id entity)))

(defn find-idea [id]
  (get @database id))

;; Handlers

(defn handle-create-idea [req]
  (if-let [idea (params->idea (get-in req [:params]))]
    (save-idea! idea))
  (redirect "/"))

(defn handle-update-idea [req]
  (let [id (java.util.UUID/fromString (get-in req [:params :id]))
        idea (params->idea (get-in req [:params]))]
    (save-idea! (assoc idea :id id))
    (redirect "/")))

(defn handle-delete-idea [req]
  (if-let [id (java.util.UUID/fromString (get-in req [:params :id]))]
    (remove-idea! id))
  (redirect "/"))

(defn handle-index-idea [req]
  (let [ideas (vals @database)]
    (response (index-page ideas))))

(defn handle-edit-idea [req]
  (let [id (java.util.UUID/fromString (get-in req [:params :id]))]
    (response (edit-page (find-idea id)))))

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

;; App config

(def app
  (-> app-routes
      wrap-sim-methods
      wrap-params
      (wrap-resource "public")))
