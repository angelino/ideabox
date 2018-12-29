(ns ideabox.store
  (:require [clojure.java.jdbc :as jdbc]))

(defn create-table [db]
  (jdbc/execute! db
                 "CREATE TABLE IF NOT EXISTS ideas (
                   id UUID not null primary key,
                   title VARCHAR(255) not null,
                   description VARCHAR(4000) not null,
                   created_at TIMESTAMP not null default NOW(),
                   updated_at TIMESTAMP not null default NOW())")
  (jdbc/execute! db
                 "ALTER TABLE IF EXISTS ideas
                    ADD COLUMN IF NOT EXISTS rank SMALLINT default 0")
  (jdbc/execute! db
                 "CREATE INDEX IF NOT EXISTS ideas_rank ON ideas (rank)"))

(defn read-ideas [db]
  (jdbc/query db "SELECT * FROM ideas ORDER BY rank DESC"))

(defn find-idea [db id]
  (first (jdbc/query db ["SELECT * FROM ideas WHERE id = ?" id])))

(defn create-idea! [db {:keys [title description] :as idea}]
  (let [id (java.util.UUID/randomUUID)]
    (jdbc/execute! db
                   ["INSERT INTO ideas (id, title, description)
                       VALUES (?, ?, ?)" id title description])))

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

(defn remove-idea! [db id]
  (jdbc/execute! db ["DELETE FROM ideas WHERE id = ?" id]))

(comment
  (def db-spec {:connection-uri "jdbc:h2:~/test"
                :user "sa"
                :password ""})

  (create-table db-spec)

  (create-idea! db-spec
              {:title "Pizza day!"
               :description "Pizza day will be the best day ever!!!!"})

  (def ideas (read-ideas db-spec))

  (def id (:id (first ideas)))

  (def idea (find-idea db-spec id))

  (update-idea! db-spec (assoc idea :description "Edited"))

  (find-idea db-spec id)

  (remove-idea! db-spec id)

  (assert (nil? (find-idea db-spec id)))
  (assert (empty? (read-ideas db-spec))))

