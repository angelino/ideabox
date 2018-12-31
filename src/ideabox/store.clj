(ns ideabox.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.java.io :as io]))

(defn init-database [db]
  (with-open [r (io/reader (io/resource "db/init.sql"))]
    (jdbc/execute! db (slurp r))))

(defn read-ideas [db]
  (jdbc/query db "SELECT * FROM ideas WHERE archived = FALSE ORDER BY rank DESC"))

(defn read-archive [db]
  (jdbc/query db "SELECT * FROM ideas WHERE archived = TRUE ORDER BY updated_at DESC"))

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

(defn archive-idea! [db id]
  (if (find-idea db id)
    (jdbc/execute! db
                   ["UPDATE ideas SET archived = TRUE, updated_at = NOW()
                       WHERE id = ?" id])))

(defn remove-idea! [db id]
  (jdbc/execute! db ["DELETE FROM ideas WHERE id = ?" id]))

(comment
  (def db-spec {:connection-uri "jdbc:h2:~/test"
                :user "sa"
                :password ""})

  (init-database db-spec)

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

