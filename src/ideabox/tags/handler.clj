(ns ideabox.tags.handler
  (:require [ring.util.response :refer [response]]
            [ideabox.tags.store :as store]
            [ideabox.tags.view :as view]))

(defn handle-index-tag [req]
  (let [db (:ideabox/db req)
        user-id (java.util.UUID/fromString (get-in req [:params :user-id]))]
    (->> (store/read-tags db user-id)
         (view/index-page user-id)
         (response))))
