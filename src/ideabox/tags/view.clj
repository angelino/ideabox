(ns ideabox.tags.view
  (:require [hiccup.page :as page]
            [ideabox.shared.url :refer :all]
            [ideabox.shared.view :refer :all]))

(defn index-page [user-id tags]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar user-id)
    [:section.section
     [:div.container
      [:div.level
       [:div.level-left
        [:div.level-item
         [:h3.title.is-3 "Existing Tags"]]]]
      [:div.columns.is-multiline
       [:div.tags
        (for [tag tags]
          [:a.tag.is-link
           {:href (tagged-ideas-url user-id (:tag tag))}
           [:span (str (:tag tag) "(" (:ideas-count tag) ")")]])]]]]]))
