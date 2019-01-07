(ns ideabox.users.view
  (:require [hiccup.page :as page]
            [ideabox.shared.view :refer :all]))

(defn registrations-url []
  "/auth/registrations")

(defn user-registration-form [{id :id :as user}]
  [:form {:action (registrations-url)
          :method "POST"}
   [:div.field
    [:input.input
     {:type :text
      :name "user[username]"
      :placeholder "Your name"
      :value (:username user)}]]
   [:div.field
    [:input.input
     {:type :email
      :name "user[email]"
      :placeholder "Your best e-mail"
      :value (:email user)}]]
   [:div.field
    [:input.input
     {:type :password
      :name "user[password]"
      :placeholder "Type a strong password"
      :value (:password user)}]]
   [:div.field
    [:input.input
     {:type :password
      :name "user[password-confirmation]"
      :placeholder "Confirm the password"
      :value (:password-confirmation user)}]]
   [:div
    [:input.button.is-primary.is-medium
     {:type :submit
      :value "Create"}]
    [:a.button.is-light.is-medium
     {:href "/auth/login"}
     "Cancel"]]])

(defn new-user-page [user]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar nil)
    [:section.section
     [:div.container
      [:h1.title.is-1 "Create a new user"]
      (error-panel (:errors user))
      (user-registration-form user)]]]))

