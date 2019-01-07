(ns ideabox.auth.handler
  (:require [ring.util.response :refer [redirect
                                        response]]
            [compojure.response :refer [render]]
            [buddy.hashers :as hashers]
            [buddy.auth :refer [authenticated?]]
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
        password (get-in req [:params :password])]
    (if-let [user (store/find-user-by-email db email)]
      (if (hashers/check password (:password user))
        (-> (redirect (home-url (:id user)))
            (assoc :session (assoc (:session req)
                                   :identity (:email user)))))
      (render (view/login-page {:email email
                                :password password})
              req))))

(defn handle-logout [req]
  (-> (redirect (view/login-url))
      (assoc :session {})))

(defn handle-unauthorized [request metadata]
  (cond
    ;; If request is authenticated, raise 403 instead
    ;; of 401 (because user is authenticated but permission
    ;; denied is raised).
    (authenticated? request)
    (-> (render (error-page) request)
        (assoc :status 403))
    ;; In other cases, redirect the user to login page.
    :else
    (let [current-url (:uri request)]
      (redirect (format "/auth/login?next=%s" current-url)))))
