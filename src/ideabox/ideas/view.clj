(ns ideabox.ideas.view
  (:require
   [clojure.string :as s]
   [hiccup.page :as page]
   [ideabox.shared.url :as urls]
   [ideabox.shared.view :as views]))

(defn remove-button [idea]
  [:form {:action (urls/idea-url idea)
          :method "POST"}
   [:input {:type :hidden
            :name "_method"
            :value "DELETE"}]
   [:button.button.is-danger.is-small
    [:span.icon [:i.fas.fa-trash]]
    [:span "Delete"]]])

(defn edit-button [idea]
  [:a.button.is-light.is-small
   {:href (urls/edit-idea-url idea)}
   [:span.icon [:i.fas.fa-edit]]
   [:span "Edit"]])

(defn like-button [idea]
  [:form {:action (urls/like-idea-url idea)
          :method "POST"}
   [:button.button.is-light.is-small
    [:span.icon [:i.fas.fa-thumbs-up]]
    [:span "+"]]])

(defn unlike-button [idea]
  [:form {:action (urls/like-idea-url idea)
          :method "POST"}
   [:input {:type :hidden
            :name "_method"
            :value "DELETE"}]
   [:button.button.is-light.is-small
    [:span.icon [:i.fas.fa-thumbs-down]]
    [:span "-"]]])

(defn archive-button [idea]
  [:form {:action (urls/archive-idea-url idea)
          :method "POST"}
   [:button.button.is-light.is-small
    [:span.icon [:i.fas.fa-archive]]
    [:span "Archive"]]])

(defn unarchive-button [idea]
  [:form {:action (urls/unarchive-idea-url idea)
          :method "POST"}
   [:button.button.is-light.is-small
    [:span.icon [:i.fas.fa-archive]]
    [:span "Unarchive"]]])

(defn idea-form [{:keys [user-id id] :as idea}]
  [:form {:action (if id
                    (urls/idea-url idea)
                    (urls/ideas-url user-id))
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
   [:div.field
    [:input.input
     {:type :text
      :name "idea[tags]"
      :placeholder "Tags separated by comma"
      :value (s/join ", " (:tags idea))}]]
   [:div
    [:input.button.is-primary.is-medium
     {:type :submit
      :value (if id "Edit" "Create")}]
    [:a.button.is-light.is-medium
     {:href (urls/ideas-url user-id)}
     "Cancel"]]])

(defn tags [idea]
  [:div.tags
   (for [tag (seq (:tags idea))]
     [:a.tag.is-link
      {:href (urls/tagged-ideas-url (:user-id idea) tag)}
      [:span tag]])])

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
       (like-button idea)]
      [:div.level-item
       (unlike-button idea)]
      [:div.level-item
       [:span.tag.is-small (:rank idea)]]
      [:div.level-item
       (edit-button idea)]
      [:div.level-item
       (archive-button idea)]
      [:div.level-item
       (remove-button idea)]]]]
   [:div.card-content
    (for [line (s/split (:description idea) #"\n")]
      [:p.content line])
    (tags idea)]])

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
       (unarchive-button idea)]
      [:div.level-item
       (remove-button idea)]]]]
   [:div.card-content
    (for [line (s/split (:description idea) #"\n")]
      [:p.content line])
    [:div.tags
     (for [tag (seq (:tags idea))]
       [:span.tag tag])]]])

(defn index-page [user-id ideas]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (views/page-head)
   [:body
    (views/nav-bar user-id)
    [:section.section
     [:div.container
      [:div.level
       [:div.level-left
        [:div.level-item
         [:h3.title.is-3 "Existing Ideas"]]]
       [:div.level-right
        [:div.level-item
         [:a.button.is-primary {:href (urls/new-idea-url user-id)} "Create a new Idea"]]]]
      [:div.columns.is-multiline
       (for [idea ideas]
         [:div.column.is-12
          (idea-card idea)])]]]]))

(defn index-archive-page [user-id ideas]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (views/page-head)
   [:body
    (views/nav-bar user-id)
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
   (views/page-head)
   [:body
    (views/nav-bar (:user-id idea))
    [:section.section
     [:div.container
      [:h1.title.is-1 "A fresh new idea?"]
      (views/error-panel (:errors idea))
      (idea-form idea)]]]))

(defn edit-page [idea]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (views/page-head)
   [:body
    (views/nav-bar (:user-id idea))
    [:section.section
     [:div.container
      [:h1.title.is-1 "Edit your idea"]
      (views/error-panel (:errors idea))
      (idea-form idea)]]]))

