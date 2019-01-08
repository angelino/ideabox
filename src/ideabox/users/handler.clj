(ns ideabox.users.handler
  (:require [ring.util.response :refer [redirect
                                        response
                                        bad-request]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [ideabox.shared.url :refer :all]
            [ideabox.users.store :as store]
            [ideabox.users.view :as view]))

(defn validate-user [user]
  (first
   (b/validate user
               :username [[v/required]
                          [v/string]
                          [v/min-count 5 :message "username is less than 5"]
                          [v/max-count 255 :message "title is longer than 64"]]
               :email [[v/required]
                       [v/email]]
               :password [[v/required]
                          [v/string]
                          [v/min-count 6 :message "password is less than 6"]]
               :password-confirmation [[v/required]
                                       [v/string]
                                       [v/min-count 6 :message "passoword confirmation is less than 6"]])))

(defn handle-new-user [req]
  (-> {}
      view/new-user-page
      response))

(defn handle-create-user [req]
  (let [db (:ideabox/db req)
        {:keys [:email :username :password :password-confirmation] :as user} (get-in req [:params :user])
        errors (validate-user user)]
    (if errors
      (-> (assoc user :errors errors)
          view/new-user-page
          bad-request)
      (if-not (= password password-confirmation)
        (-> {:username username
             :email email
             :errors [[:password "The password and the confirmation does not match"]]}
            view/new-user-page
            bad-request)
        (if-let [user (store/find-user-by-email db email)]
          (-> {:email email
               :username username
               :errors [[:email "User already exists"]]}
              view/new-user-page
              bad-request)
          (do
            (store/create-user! db user)
            (let [user-id (:id (store/find-user-by-email db email))]
              (redirect (login-url)))))))))
