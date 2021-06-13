(ns ideabox.config
  (:require [environ.core :refer [env]]))

(def db (or (env :jdbc-database-url) ;; see: https://devcenter.heroku.com/articles/connecting-to-relational-databases-on-heroku-with-java#using-the-jdbc_database_url
            {:connection-uri (env :database-connection-uri)
             :user (env :database-user)
             :password (env :database-password)}))

(def server {:port (Integer. (or (env :port) 5000))})
