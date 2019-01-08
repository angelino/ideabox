(ns ideabox.users.view
  (:require [hiccup.page :as page]
            [ideabox.shared.url :refer :all]
            [ideabox.shared.view :refer :all]))

(defn user-registration-form [{id :id :as user}]
  [:form {:action (registrations-url)
          :method "POST"}
   [:div.field
    [:labe.label
     {:for "user[username]"}
     "Name"]
    [:input.input
     {:type :text
      :name "user[username]"
      :value (:username user)}]]
   [:div.field
    [:labe.label
     {:for "user[email]"}
     "Email"]
    [:input.input
     {:type :email
      :name "user[email]"
      :value (:email user)}]]
   [:div.field
    [:labe.label
     {:for "user[password]"}
     "Password"]
    [:input.input
     {:type :password
      :name "user[password]"
      :value (:password user)}]]
   [:div.field
    [:labe.label
     {:for "user[password-confirmation]"}
     "Password Confirmation"]
    [:input.input
     {:type :password
      :name "user[password-confirmation]"
      :value (:password-confirmation user)}]]
   [:div.field
    [:input.button.is-primary.is-fullwidth
     {:type :submit
      :value "Sign up"}]]
   [:div.has-text-centered
    [:span.content.is-small "Already have an account?"]
    [:a.button.is-text.is-small
     {:href (login-url)}
     "Sign in"]]])

(defn new-user-page [user]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar nil)
    [:section.section
     [:div.container
      [:div.columns.is-centered
       [:div.column.is-half
        (error-panel (:errors user))
        (user-registration-form user)]]]]]))

