(defproject ideabox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [ring "1.9.3"]
                 [compojure "1.6.2"]
                 [hiccup "1.0.5"]
                 [buddy/buddy-auth "3.0.1"]
                 [buddy/buddy-hashers "1.8.1"]
                 [bouncer "1.0.1"]
                 [environ "1.2.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.2.21"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.1.0"]
            [lein-codox "0.10.7"]]
  :codox {:source-paths ["src"]}
  :profiles {;; see: https://devcenter.heroku.com/articles/deploying-clojure
             :uberjar {:aot :all}
             :production {:env {:production true}}}
  :uberjar-name "ideabox-standalone.jar"
  :min-lein-version "2.0.0"
  :main ideabox.core
  :ring {:handler ideabox.core/app
         :init ideabox.core/on-startup})
