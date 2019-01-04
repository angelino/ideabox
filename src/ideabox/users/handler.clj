(ns ideabox.users.handler
  (:require [ring.util.response :refer [redirect
                                        response]]
            [ideabox.users.store :as store]
            [ideabox.users.view :as view]))

(defn handle-new-user [req]
  (-> {}
      view/new-user-page
      response))

(defn handle-create-user [req]
  (let [db (:ideabox/db req)
        user (get-in req [:params :user])]
    (store/create-user! db user)
    (redirect "/")))
