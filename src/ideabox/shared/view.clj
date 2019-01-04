(ns ideabox.shared.view
  (:require [hiccup.page :as page]
            [clojure.pprint :as pp]))

(defn page-head []
  [:head
   [:title "IdeaBox"]
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (page/include-css "/css/bulma.css")
   [:script {:defer true
             :src "https://use.fontawesome.com/releases/v5.3.1/js/all.js"}]])

(defn nav-bar []
  [:nav.navbar
   [:div.container
    [:div.navbar-brand
     [:div.navbar-item
      [:span.icon.is-medium
       [:i.fas.fa-3x.fa-lightbulb]]
      [:div.navbar-item
       [:h1.title.is-1 "IdeaBox"]]]]
    [:div.navbar-menu
     [:div.navbar-start
      [:a.navbar-item {:href "/"} "Home"]
      [:a.navbar-item {:href "/archive"} "Archive"]]]]])

(defn error-panel [errors]
  (when errors
    [:div.message.is-danger
     [:div.message-header
      [:p "Fix the mistakes before save"]]
     [:div.message-body
      (for [[field message] errors]
        [:p (apply str message)])]]))

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
