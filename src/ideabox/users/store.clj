(ns ideabox.users.store
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]))

(defn create-user! [db {:keys [username email password]}]
  (let [id (java.util.UUID/randomUUID)
        password-hash (hashers/derive password)]
    (jdbc/execute! db
                   ["INSERT INTO users (id, username, email, password, created_at, updated_at)
                       VALUES(?, ?, ?, ?, NOW(), NOW())" id username, email, password-hash])))

(defn find-user-by-email [db email]
  (first (jdbc/query db
                     ["SELECT * FROM users WHERE email = ?" email])))

(comment

  (def phash (hashers/derive "mysecret"))
  (hashers/check "mysecret" phash)

  (create-user! user/db-dev
                {:username "Lucas"
                 :email "lucas.angelino@gmail.com"
                 :password "xpto"})

  (jdbc/query user/db-dev
              "SELECT * FROM ideas")

  (jdbc/execute! user/db-dev
                 ["UPDATE ideas SET user_id = (SELECT TOP 1 id FROM users)"])

  (jdbc/query user/db-dev
              "SELECT * FROM users"))


