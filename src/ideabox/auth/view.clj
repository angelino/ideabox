(ns ideabox.auth.view
  (:require [hiccup.page :as page]
            [ideabox.shared.view :refer :all]))

(defn login-url []
  "/auth/login")

(defn sessions-url []
  "/auth/sessions")

(defn signup-url []
  "/auth/signup")

(defn login-form [{:keys [:email :password] :as credentials}]
  [:form {:action (sessions-url)
          :method "POST"}
   [:div.field
    [:input.input
     {:type :email
      :name "email"
      :placeholder "Your e-mail"
      :value email}]]
   [:div.field
    [:input.input
     {:type :password
      :name "password"
      :placeholder "Your password"
      :value password}]]
   [:div
    [:input.button.is-primary.is-medium
     {:type :submit
      :value "Login"}]
    [:a.button.is-light.is-medium
     {:href (login-url)}
     "Cancel"]]
   [:div
    [:a.button.is-white.is-medium
     {:href (signup-url)}
     "Signup"]]])

(defn login-page [credentials]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar nil)
    [:section.section
     [:div.container
      [:h1.title.is-1 "Login"]
      (error-panel (:errors credentials))
      (login-form credentials)]]]))
