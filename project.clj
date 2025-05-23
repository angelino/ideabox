(defproject ideabox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [com.taoensso/telemere "1.0.0-beta16"]
                 [com.taoensso/slf4j-telemere "1.0.0-beta16"]
                 [ring "1.12.2"]
                 [compojure "1.7.1"]
                 [hiccup "1.0.5"]
                 [buddy/buddy-auth "3.0.323"]
                 [buddy/buddy-hashers "2.0.167"]
                 [bouncer "1.0.1"]
                 [environ "1.2.0"]
                 [clj-commons/iapetos "0.1.14"]
                 [io.prometheus/simpleclient_hotspot "0.12.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [com.h2database/h2 "2.3.232"]
                 [org.postgresql/postgresql "42.7.3"]]
  :plugins [[lein-ring "0.12.6"]
            [lein-environ "1.1.0"]
            [lein-codox "0.10.8"]]
  :codox {:source-paths ["src"]}
  :profiles {:dev {:source-paths ["dev" "src"]}
             ;; see: https://devcenter.heroku.com/articles/deploying-clojure
             :uberjar {:aot :all}
             :production {:env {:production true}}}
  :uberjar-name "ideabox-standalone.jar"
  :min-lein-version "2.0.0"
  :main ideabox.core
  :ring {:handler ideabox.core/app
         :init ideabox.core/on-startup})
