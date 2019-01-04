(ns ideabox.shared.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.java.io :as io]))

(defn init-database [db]
  (with-open [r (io/reader (io/resource "db/init.sql"))]
    (jdbc/execute! db (slurp r))))
