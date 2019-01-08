(ns ideabox.users.handler
  (:require [ring.util.response :refer [redirect
                                        response]]
            [ideabox.shared.url :refer :all]
            [ideabox.users.store :as store]
            [ideabox.users.view :as view]))

(defn handle-new-user [req]
  (-> {}
      view/new-user-page
      response))

(defn handle-create-user [req]
  (let [db (:ideabox/db req)
        {email :email :as user} (get-in req [:params :user])]
    (store/create-user! db user)
    (let [user-id (:id (store/find-user-by-email db email))]
      (redirect (login-url)))))
