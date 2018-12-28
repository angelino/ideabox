(ns ideabox.view
  (:require [hiccup.page :as page]
            [clojure.pprint :as pp]))

(defn remove-button [id]
  [:form {:action (str "/" id)
          :method "POST"}
   [:input {:type :hidden
            :name "_method"
            :value "DELETE"}]
   [:button.button.is-light.is-small
    [:span.icon [:i.fas.fa-trash]]
    [:span "Delete"]]])

(defn edit-button [id]
  [:a.button.is-light.is-small
   {:href (str "/" id "/edit")}
   [:span.icon [:i.fas.fa-edit]]
   [:span "Edit"]])

(defn idea-form [{id :id :as idea}]
  [:form {:action (if id (str "/" id) "/")
          :method "POST"}
   [:div.field
    (when id
      [:input {:type :hidden :name "_method" :value "PUT"}])
    [:input.input
     {:type :text
      :name "idea[title]"
      :placeholder "Title"
      :value (:title idea)}]]
   [:div.field
    [:textarea.textarea
     {:name "idea[description]"
      :placeholder "Your idea..."}
     (:description idea)]]
   [:div
    [:input.button.is-primary.is-medium
     {:type :submit
      :value (if id "Edit" "Create")}]
    [:a.button.is-light.is-medium
     {:href "/"}
     "Cancel"]]])

(defn error-page [request]
  (pp/pprint request)
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   [:head
    [:title "IdeaBox Error"]
    (page/include-css "/css/bulma.css")]
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

(defn index-page [ideas]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   [:head
    [:title "IdeaBox"]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    (page/include-css "/css/bulma.css")
    [:script {:defer true
              :src "https://use.fontawesome.com/releases/v5.3.1/js/all.js"}]]
   [:body
    [:section.section
     [:div.container
      [:h1.title.is-1 "IdeaBox"]
      (idea-form {})]]
    [:section.section
     [:div.container
      [:h3.title.is-3 "Existing Ideas"]
      [:ul
       (for [idea ideas]
         [:li.content.is-small
          [:div.field.is-grouped
           [:div.control
            [:h4 (:title idea)]]
           [:div.control
            (edit-button (:id idea))]
           [:div.control
            (remove-button (:id idea))]]
          (for [line (clojure.string/split (:description idea) #"\n")]
            [:p line])])]]]]))

(defn edit-page [idea]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   [:head
    [:title "IdeaBox"]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    (page/include-css "/css/bulma.css")
    [:script {:defer true
              :src "https://use.fontawesome.com/releases/v5.3.1/js/all.js"}]]
   [:body
    [:section.section
     [:div.container
      [:h1.title.is-1 "Edit your idea"]
      (idea-form idea)]]]))

