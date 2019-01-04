(ns ideabox.users.view
  (:require [hiccup.page :as page]
            [ideabox.shared.view :refer :all]))

(defn new-user-page [user]
  (page/html5
   {:lang "en-US"
    :encoding "utf-8"}
   (page-head)
   [:body "Create a new user"]))

