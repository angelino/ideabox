(ns ideabox.auth.handler
  (:require [ring.util.response :refer [redirect
                                        response]]
            [compojure.response :refer [render]]
            [buddy.hashers :as hashers]
            [buddy.auth :refer [authenticated?]]
            [ideabox.shared.url :refer :all]
            [ideabox.shared.view :refer :all]
            [ideabox.auth.view :as view]
            [ideabox.users.store :as store]))

(defn handle-login [req]
  (-> {}
      view/login-page
      response))

(defn handle-create-session [req]
  (let [db (:ideabox/db req)
        email (get-in req [:params :email])
        password (get-in req [:params :password])
        session (:session req)]
    (if-let [user (store/find-user-by-email db email)]
      (if (hashers/check password (:password user))
        (-> (redirect (home-user-url (:id user)))
            (assoc :session (assoc session :identity (dissoc user :password))))
        (render (view/login-page {:email email
                                  :password password
                                  :errors [[:password "Invalid password"]]})
                req))
      (render (view/login-page {:email email
                                :password password
                                :errors [[:email "User not found"]]})
              req))))

(defn handle-logout [req]
  (-> (redirect (login-url))
      (assoc :session {})))
