(ns ideabox.ideas.view
  (:require [hiccup.page :as page]
            [ideabox.shared.view :refer :all]))

(defn remove-button [id]
  [:form {:action (str "/" id)
          :method "POST"}
   [:input {:type :hidden
            :name "_method"
            :value "DELETE"}]
   [:button.button.is-danger.is-small
    [:span.icon [:i.fas.fa-trash]]
    [:span "Delete"]]])

(defn edit-button [id]
  [:a.button.is-light.is-small
   {:href (str "/" id "/edit")}
   [:span.icon [:i.fas.fa-edit]]
   [:span "Edit"]])

(defn like-button [id]
  [:form {:action (str "/" id "/like")
          :method "POST"}
   [:button.button.is-light.is-small
    [:span.icon [:i.fas.fa-thumbs-up]]
    [:span "+"]]])

(defn unlike-button [id]
  [:form {:action (str "/" id "/like")
          :method "POST"}
   [:input {:type :hidden
            :name "_method"
            :value "DELETE"}]
   [:button.button.is-light.is-small
    [:span.icon [:i.fas.fa-thumbs-down]]
    [:span "-"]]])

(defn archive-button [id]
  [:form {:action (str "/" id "/archive")
          :method "POST"}
   [:button.button.is-light.is-small
    [:span.icon [:i.fas.fa-archive]]
    [:span "Archive"]]])

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

(defn idea-card [idea]
  [:div.card
   [:header.card-header
    [:div.level
     [:div.level-left
      [:div.level-item
       [:h4.card-header-title
        (:title idea)]]]
     [:div.level-right
      [:div.level-item
       (like-button (:id idea))]
      [:div.level-item
       (unlike-button (:id idea))]
      [:div.level-item
       [:span.tag.is-small (:rank idea)]]
      [:div.level-item
       (edit-button (:id idea))]
      [:div.level-item
       (archive-button (:id idea))]
      [:div.level-item
       (remove-button (:id idea))]]]]
   [:div.card-content
    (for [line (clojure.string/split (:description idea) #"\n")]
      [:p.content line])]])

(defn archived-idea-card [idea]
  [:div.card
   [:header.card-header
    [:div.level
     [:div.level-left
      [:div.level-item
       [:h4.card-header-title
        (:title idea)]]
      [:div.level-item
       [:span.tag.is-small (:rank idea)]]
      [:div.level-item
       (remove-button (:id idea))]]]]
   [:div.card-content
    (for [line (clojure.string/split (:description idea) #"\n")]
      [:p.content line])]])

(defn index-page [ideas]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar)
    [:section.section
     [:div.container
      [:div.level
       [:div.level-left
        [:div.level-item
         [:h3.title.is-3 "Existing Ideas"]]]
       [:div.level-right
        [:div.level-item
         [:a.button.is-primary {:href "/new"} "Create a new Idea"]]]]
      [:div.columns.is-multiline
       (for [idea ideas]
         [:div.column.is-12
          (idea-card idea)])]]]]))

(defn index-archive-page [ideas]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar)
    [:section.section
     [:div.container
      [:div.level
       [:div.level-left
        [:div.level-item
         [:h3.title.is-3 "Archived Ideas"]]]]
      [:div.columns.is-multiline
       (for [idea ideas]
         [:div.column.is-12
          (archived-idea-card idea)])]]]]))

(defn new-page [idea]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar)
    [:section.section
     [:div.container
      [:h1.title.is-1 "A new fresh idea?"]
      (error-panel (:errors idea))
      (idea-form idea)]]]))

(defn edit-page [idea]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body
    (nav-bar)
    [:section.section
     [:div.container
      [:h1.title.is-1 "Edit your idea"]
      (error-panel (:errors idea))
      (idea-form idea)]]]))

