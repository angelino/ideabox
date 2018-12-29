(defproject ideabox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring "1.7.1"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [bouncer "1.0.1"]
                 [environ "1.1.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [com.h2database/h2 "1.4.197"]]
  :plugins [[lein-ring "0.12.4"]
            [lein-environ "1.1.0"]]
  ;; see: https://devcenter.heroku.com/articles/deploying-clojure
  :profiles {:uberjar {:aot :all}}
  :ring {:handler ideabox.core/app})
