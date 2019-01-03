(ns ideabox.users.handler
  (:require [ring.util.response :refer [redirect
                                        response]]
            [ideabox.users.store :as store]))

(defn handle-create-user [req]
  (let [db (:ideabox/db req)
        user (get-in req [:params :user])]
    (store/create-user! db user)
    (redirect "/")))
