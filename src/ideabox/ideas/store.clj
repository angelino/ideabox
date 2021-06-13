(ns ideabox.ideas.store
  (:require [clojure.java.jdbc :as jdbc]))

(defn idea-row-mapper [row]
  {:id (:id row)
   :user-id (:user_id row)
   :title (:title row)
   :description (:description row)
   :rank (:rank row)
   :created-at (:created_at row)
   :updated-at (:updated-at row)})

(defn read-ideas [db user-id]
  (jdbc/query db
              ["SELECT * FROM ideas WHERE user_id = ? AND archived = FALSE ORDER BY rank DESC" user-id]
              {:row-fn idea-row-mapper}))

(defn read-archive [db user-id]
  (jdbc/query db
              ["SELECT * FROM ideas WHERE user_id =? AND archived = TRUE ORDER BY updated_at DESC" user-id]
              {:row-fn idea-row-mapper}))

(defn find-idea [db id]
  (first (jdbc/query db
                     ["SELECT * FROM ideas WHERE id = ?" id]
                     {:row-fn idea-row-mapper})))

(defn create-idea! [db {:keys [user-id title description] :as idea}]
  (let [id (java.util.UUID/randomUUID)]
    (jdbc/execute! db
                   ["INSERT INTO ideas (id, user_id, title, description)
                       VALUES (?, ?, ?, ?)" id user-id title description])))

(defn update-idea! [db {:keys [id title description] :as idea}]
  (jdbc/execute! db
                 ["UPDATE ideas SET title = ?,
                                    description = ?,
                                    updated_at = NOW()
                     WHERE id = ?" title description id]))

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
  (jdbc/execute! db ["DELETE FROM ideas WHERE id = ?" id]))

(comment

  (create-idea! user/db-dev
                {:title "Pizza day!"
                 :description "Pizza day will be the best day ever!!!!"})

  (def ideas (read-ideas user/db-dev))

  (def id (:id (first ideas)))

  (def idea (find-idea user/db-dev id))

  (update-idea! user/db-dev (assoc idea :description "Edited"))

  (find-idea user/db-dev id)

  (remove-idea! user/db-dev id)

  (assert (nil? (find-idea user/db-dev id)))
  (assert (empty? (read-ideas user/db-dev))))

