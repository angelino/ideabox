(ns ideabox.ideas.handler
  (:require
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [ideabox.ideas.store :as store]
   [ideabox.ideas.view :as view]
   [ideabox.shared.url :as urls]
   [ring.util.response :refer [bad-request redirect response]]))

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

(defn handle-new-idea [req]
  (let [user-id (java.util.UUID/fromString (get-in req [:params :user-id]))]
    (-> {:user-id user-id} ;; fresh new idea
        (view/new-page)
        (response))))

(defn handle-create-idea [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))
        idea (get-in req [:params :idea])]
    (if-let [errors (validate-idea idea)]
      (bad-request (view/new-page (-> idea
                                      (assoc :errors errors)
                                      (assoc :user-id user-id))))
      (do
        (store/create-idea! db (assoc idea :user-id user-id))
        (redirect (urls/ideas-url user-id))))))

(defn handle-update-idea [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))
        id (java.util.UUID/fromString (get-in req [:params :id]))
        idea (get-in req [:params :idea])]
    (if-let [errors (validate-idea idea)]
      (bad-request (view/edit-page (-> idea
                                       (assoc :id id)
                                       (assoc :errors errors)
                                       (assoc :user-id user-id))))
      (do
        (store/update-idea! db (assoc idea :id id))
        (redirect (urls/ideas-url user-id))))))

(defn handle-delete-idea [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/remove-idea! db id)
    (redirect (urls/ideas-url user-id))))

(defn handle-index-idea [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))
        tag (get-in req [:params :tag])]
    (->> (if tag
           (store/read-tagged-ideas db user-id tag)
           (store/read-ideas db user-id))
         (view/index-page user-id)
         (response))))

(defn handle-edit-idea [req]
  (let [db (:ideabox/db req)
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (-> (store/find-idea db id)
        (view/edit-page)
        (response))))

(defn handle-like-idea [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/like-idea! db id)
    (redirect (urls/ideas-url user-id))))

(defn handle-unlike-idea [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/unlike-idea! db id)
    (redirect (urls/ideas-url user-id))))

(defn handle-archive-idea [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/archive-idea! db id)
    (redirect (urls/ideas-url user-id))))

(defn handle-unarchive-idea [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))
        id (java.util.UUID/fromString (get-in req [:params :id]))]
    (store/unarchive-idea! db id)
    (redirect (urls/archive-url user-id))))

(defn handle-index-archive [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))]
    (->> (store/read-archive db user-id)
         (view/index-archive-page user-id)
         (response))))
