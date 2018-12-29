(ns ideabox.handler
  (:require [ring.util.response :refer [redirect
                                        response]]
            [ideabox.store :as store]
            [ideabox.view :refer [error-page
                                  index-page
                                  edit-page
                                  new-page]]))

(defn handle-new-idea [req]
  (-> {} ;; fresh new idea
      (new-page)
      (response)))

(defn handle-create-idea [req]
  (let [db (:ideabox/db req)
        idea (get-in req [:params :idea])]
    (store/create-idea! db idea)
    (redirect "/")))

(defn handle-update-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))
        idea (get-in req [:params :idea])]
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

(defn handle-like-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/like-idea! db id)
    (redirect "/")))

(defn handle-not-found [req]
  (error-page req))
