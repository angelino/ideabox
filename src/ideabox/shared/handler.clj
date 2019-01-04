(ns ideabox.shared.handler
  (:require [clojure.pprint :as pp]
            [ideabox.shared.view :refer :all]))

(defn bad-request [body]
  {:status 400
   :headers {}
   :body body})

(defn handle-not-found [req]
  (pp/pprint req)
  (bad-request (error-page req)))
