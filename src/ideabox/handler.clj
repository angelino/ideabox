(ns ideabox.handler
  (:require [ring.util.response :refer [redirect
                                        response]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [ideabox.store :as store]
            [ideabox.view :refer [error-page
                                  index-page
                                  edit-page
                                  new-page
                                  index-archive-page]]))

(defn validate-idea [idea]
  (first
   (b/validate idea
               :id [[v/string]
                    [v/max-count 32]]
               :title [[v/required]
                       [v/string]
                       [v/min-count 3 :message "title is less than 3"]
                       [v/max-count 255 :message "title is longer than 255"]]
               :description [[v/string]
                             [v/max-count 4000 :message "title is longer than 4000"]])))

(defn bad-request [body]
  {:status 400
   :headers {}
   :body body})

(defn handle-new-idea [req]
  (-> {} ;; fresh new idea
      (new-page)
      (response)))

(defn handle-create-idea [req]
  (let [db (:ideabox/db req)
        idea (get-in req [:params :idea])
        errors (validate-idea idea)]
    (if errors
      (bad-request (new-page (assoc idea :errors errors)))
      (do
        (store/create-idea! db idea)
        (redirect "/")))))

(defn handle-update-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))
        idea (get-in req [:params :idea])
        errors (validate-idea idea)]
    (if errors
      (bad-request (edit-page (assoc idea :id id :errors errors)))
      (do
        (store/update-idea! db (assoc idea :id id))
        (redirect "/")))))

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

(defn handle-like-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/like-idea! db id)
    (redirect "/")))

(defn handle-archive-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/archive-idea! db id)
    (redirect "/")))

(defn handle-index-archive [req]
  (let [db (:ideabox/db req)]
    (-> (store/read-archive db)
        (index-archive-page)
        (response))))

(defn handle-not-found [req]
  (error-page req))
