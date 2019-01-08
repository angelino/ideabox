(ns ideabox.shared.handler
  (:require [clojure.pprint :as pp]
            [ring.util.response :refer [response
                                        bad-request]]
            [ideabox.shared.view :refer :all]))

(defn handle-not-found [req]
  (pp/pprint req)
  (bad-request (error-page req)))

(defn handle-home [req]
  (let [user (:identity req)]
    (response (home-page user))))
