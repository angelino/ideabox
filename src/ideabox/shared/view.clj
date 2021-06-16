(ns ideabox.shared.view
  (:require [hiccup.page :as page]
            [ideabox.shared.url :refer :all]))

(defn page-head []
  [:head
   [:title "IdeaBox"]
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (page/include-css "/css/bulma.css")
   [:script {:defer true
             :src "https://use.fontawesome.com/releases/v5.3.1/js/all.js"}]
   [:script {:defer true
             :src "/js/navbar-burger.js"}]])

(defn nav-bar [user-id]
  [:nav.navbar
   [:div.container
    [:div.navbar-brand
     [:div.navbar-item
      [:span.icon.is-medium
       [:i.fas.fa-3x.fa-lightbulb]]
      [:div.navbar-item
       [:h1.title.is-1 "IdeaBox"]]]
     [:a.navbar-burger
      {:role "button"
       :aria-label "menu"
       :aria-expanded "false"
       :data-target "menu"}
      [:span {:aria-hidden "true"}]
      [:span {:aria-hidden "true"}]
      [:span {:aria-hidden "true"}]]]
    (if user-id
      [:div#menu.navbar-menu
       [:div.navbar-start
        [:a.navbar-item {:href (home-user-url user-id)} "Home"]
        [:a.navbar-item {:href (tags-url user-id)} "Tags"]
        [:a.navbar-item {:href (archive-url user-id)} "Archive"]]

       [:div.navbar-end
        [:a.navbar-item {:href (logout-url)} "Logout"]]]
      [:div#menu.navbar-menu
       [:div.navbar-start
        [:a.navbar-item {:href (home-url)} "Home"]]
       [:div.navbar-end
        [:a.navbar-item {:href (login-url)} "Login"]]])]])

(defn error-panel [errors]
  (when errors
    [:div.message.is-danger
     [:div.message-header
      [:p "Fix the mistakes before save"]]
     [:div.message-body
      (for [[field message] errors]
        [:p (apply str message)])]]))

(defn home-page [user]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar (:id user))
    [:section.hero.is-dark.is-fullheight
     [:div.hero-body
      [:div.container.has-text-centered
       [:h1.title "IdeaBox"]
       [:h2.subtitle "A place to save all your ideas"]]]]]))

(defn error-page [request]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    [:section.section
     [:div.container
      [:h1.title.is-1 "An Error Occured"]
      [:h3.title.is-3 "Request Params"]
      [:table.table.is-bordered.is-fullwidth
       [:tr
        [:th "Request Verb"]
        [:td (name (:request-method request))]]
       [:tr
        [:th "Request Path"]
        [:td (:uri request)]]
       [:tr
        [:th {:colspan 2} "Parameters"]
        (for [[k v] (:params request)]
          [:tr
           [:td (str k)]
           [:td (str v)]])]]]]]))
