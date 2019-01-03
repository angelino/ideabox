(ns ideabox.users.store
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]))

(defn create-user! [db {:keys [username email password]}]
  (let [id (java.util.UUID/randomUUID)
        password-hash (hashers/derive password)]
    (jdbc/execute! db
                   ["INSERT INTO users (id, username, email, password, created_at, updated_at)
                       VALUES(?, ?, ?, ?, NOW(), NOW())" id username, email, password-hash])))

(comment

  (def phash (hashers/derive "mysecret"))
  (hashers/check "mysecret" phash)

  (def db-spec {:connection-uri "jdbc:h2:~/test"
                :user "sa"
                :password ""})

  (create-user! db-spec {:username "Lucas"
                         :email "lucas.angelino@gmail.com"
                         :password "xpto"})

  (jdbc/query db-spec "SELECT * FROM users"))
