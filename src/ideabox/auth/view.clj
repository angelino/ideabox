(ns ideabox.auth.view
  (:require [hiccup.page :as page]
            [ideabox.shared.url :refer :all]
            [ideabox.shared.view :refer :all]))

(defn login-form [{:keys [:email :password] :as credentials}]
  [:form {:action (sessions-url)
          :method "POST"}
   [:div.field
    [:label.label
     {:for "email"}
     "Email"]
    [:input.input
     {:type :email
      :name "email"
      :value email}]]
   [:div.field
    [:label.label
     {:for "password"}
     "Password"]
    [:input.input
     {:type :password
      :name "password"
      :value password}]]
   [:div.field
    [:input.button.is-primary.is-fullwidth
     {:type :submit
      :value "Login"}]]
   [:div.has-text-centered
    [:span.content.is-small "Don't have an account yet?"]
    [:a.button.is-text.is-small
     {:href (signup-url)}
     "Sign up"]]])

(defn login-page [credentials]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar nil)
    [:section.section
     [:div.container
      [:div.columns.is-centered
       [:div.column.is-one-quarter
        (error-panel (:errors credentials))
        (login-form credentials)]]]]]))
