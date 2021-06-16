(ns ideabox.tags.store
  (:require [clojure.java.jdbc :as jdbc]))

(defn read-tags [db user-id]
  (jdbc/query db
              [(str "SELECT tags.description, count(ideas.id) as ideas_count FROM tags"
                    "  INNER JOIN ideas_tags ON tags.id = ideas_tags.tag_id"
                    "  INNER JOIN ideas ON ideas.id = ideas_tags.idea_id"
                    "  WHERE user_id = ?"
                    "  AND ideas.archived IS NOT TRUE"
                    " GROUP BY tags.description")
               user-id]
              {:row-fn (fn [record]
                         {:tag (:description record)
                          :ideas-count (:ideas_count record)})}))
