(ns ideabox.ideas.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]))

(defn idea-row-mapper [row]
  {:id (:id row)
   :user-id (:user_id row)
   :title (:title row)
   :description (:description row)
   :rank (:rank row)
   :created-at (:created_at row)
   :updated-at (:updated-at row)})

(defn read-tags [db idea-id]
  (jdbc/query db
              ["SELECT * FROM tags t INNER JOIN ideas_tags i ON t.id = i.tag_id WHERE i.idea_id = ?" idea-id]
              {:row-fn :description}))

(defn find-tag [db tag]
  (first (jdbc/query db ["SELECT * FROM tags WHERE description = ?" tag])))

(defn read-ideas [db user-id]
  (doall
   (for [idea (jdbc/query db
                          ["SELECT * FROM ideas WHERE user_id = ? AND archived = FALSE ORDER BY rank DESC" user-id]
                          {:row-fn idea-row-mapper})]
     (assoc idea :tags (read-tags db (:id idea))))))

(defn read-tagged-ideas [db user-id tag]
  (if-let [tag (find-tag db tag)]
    (doall
     (for [idea (jdbc/query db
                            ["SELECT * FROM ideas INNER JOIN ideas_tags ON ideas.id = ideas_tags.idea_id WHERE user_id = ? AND ideas_tags.tag_id = ? AND archived = FALSE ORDER BY rank DESC"
                             user-id
                             (:id tag)]
                            {:row-fn idea-row-mapper})]
       (assoc idea :tags (read-tags db (:id idea)))))))

(defn read-archive [db user-id]
  (doall
   (for [archived-idea (jdbc/query db
                                   ["SELECT * FROM ideas WHERE user_id =? AND archived = TRUE ORDER BY updated_at DESC" user-id]
                                   {:row-fn idea-row-mapper})]
     (assoc archived-idea :tags (read-tags db (:id archived-idea))))))

(defn find-idea [db id]
  (if-let [idea (first (jdbc/query db
                                   ["SELECT * FROM ideas WHERE id = ?" id]
                                   {:row-fn idea-row-mapper}))]
    (assoc idea :tags (read-tags db id))))

(defn create-tag-when-not-exists! [db idea-id description]
  (if-let [tag (find-tag db description)]
    (do
      (jdbc/execute! db
                     ["INSERT INTO ideas_tags (tag_id, idea_id) VALUES (?, ?)"
                      (:id tag)
                      idea-id]))
    (let [tag-id (java.util.UUID/randomUUID)]
      (jdbc/execute! db
                     ["INSERT INTO tags (id, description) VALUES (?, ?)"
                      tag-id
                      description])
      (jdbc/execute! db
                     ["INSERT INTO ideas_tags (tag_id, idea_id) VALUES (?, ?)"
                      tag-id
                      idea-id]))))

(defn cleanup-tags [tags]
  (->> (str/split tags #",")
       (map str/trim)
       (remove str/blank?)))

(defn create-tags! [db idea-id tags]
  (jdbc/execute! db ["DELETE FROM ideas_tags WHERE idea_id = ?" idea-id])
  (doseq [tag (cleanup-tags tags)]
    (create-tag-when-not-exists! db idea-id tag)))

(defn create-idea! [db {:keys [user-id title description tags] :as idea}]
  (let [idea-id (java.util.UUID/randomUUID)]
    (jdbc/execute! db
                   ["INSERT INTO ideas (id, user_id, title, description) VALUES (?, ?, ?, ?)"
                    idea-id
                    user-id
                    title
                    description])
    (create-tags! db idea-id tags)))

(defn update-idea! [db {:keys [id title description tags] :as idea}]
  (jdbc/execute! db
                 ["UPDATE ideas SET title = ?, description = ?, updated_at = NOW() WHERE id = ?"
                  title
                  description
                  id])
  (create-tags! db id tags))

(defn like-idea! [db id]
  (let [{:keys [rank] :as idea} (find-idea db id)]
    (jdbc/execute! db
                   ["UPDATE ideas SET rank = ?, updated_at = NOW()
                        WHERE id = ?" (inc rank) id])))

(defn unlike-idea! [db id]
  (let [{:keys [rank] :as idea} (find-idea db id)]
    (jdbc/execute! db
                   ["UPDATE ideas SET rank = ?, updated_at = NOW()
                        WHERE id = ?" (dec rank) id])))

(defn archive-idea! [db id]
  (if (find-idea db id)
    (jdbc/execute! db
                   ["UPDATE ideas SET archived = TRUE, updated_at = NOW()
                       WHERE id = ?" id])))

(defn remove-idea! [db id]
  (jdbc/execute! db ["DELETE FROM ideas_tags WHERE idea_id = ?" id])
  #_(jdbc/execute! db ["DELETE FROM tags WHERE id = ?" id])
  (jdbc/execute! db ["DELETE FROM ideas WHERE id = ?" id]))

(comment

  (def user-id (:id (first (jdbc/query user/db-dev "SELECT * FROM users"))))

  (create-idea! user/db-dev
                {:user-id user-id
                 :title "Pizza day!"
                 :description "Pizza day will be the best day ever!!!!"
                 :tags "testando tags, outra tag"})

  (def ideas (read-ideas user/db-dev user-id))

  (def id (:id (first ideas)))

  (def idea (find-idea user/db-dev id))

  (update-idea! user/db-dev (-> idea
                                (assoc :description "Edited")
                                (update :tags #(clojure.string/join ", " %))))

  (find-idea user/db-dev id)

  (remove-idea! user/db-dev id)

  (assert (nil? (find-idea user/db-dev id)))
  (assert (empty? (read-ideas user/db-dev user-id))))

