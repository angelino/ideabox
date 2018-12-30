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
                 [org.postgresql/postgresql "42.2.5"]]
  :plugins [[lein-ring "0.12.4"]
            [lein-environ "1.1.0"]]
  :profiles {;;:dev {:dependencies [[com.h2database/h2 "1.4.197"]]}
             ;; see: https://devcenter.heroku.com/articles/deploying-clojure
             :uberjar {:aot :all}
             :production {:env {:production true}}}
  :uberjar-name "ideabox-standalone.jar"
  :min-lein-version "2.0.0"
  :main ideabox.core
  :ring {:handler ideabox.core/app})
