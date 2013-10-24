(defproject
  fist
  "0.1.0-SNAPSHOT"
  :dependencies
  [[org.clojure/clojure "1.5.1"]
   [com.taoensso/timbre "2.6.2"]
   [lib-noir "0.7.1"]
   [ring-server "0.3.0"]
   [compojure "1.1.5"
    :exclusions
    [org.clojure/tools.macro
     ring/ring-core]]
   [selmer "0.4.3"]
   [com.postspectacular/rotor "0.1.0"]
   [postgresql/postgresql "9.1-901.jdbc4"]
   [korma "0.3.0-RC5"]
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [clj-time "0.6.0"]
   [formative "0.8.7"]]
  :ring
  {:handler fist.handler/war-handler,
   :init fist.handler/init,
   :destroy fist.handler/destroy}
  :clj-sql-up
  {:database
   {:classname "org.postgresql.Driver"
    :subprotocol "postgresql"
    :subname "//localhost:5432/fist"
    :user "postgres"
    :password "postgres"}
   :deps [[postgresql/postgresql "9.1-901.jdbc4"]]}
  :profiles
  {:production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
    {:dependencies [[ring/ring-devel "1.2.0"]]}}
  :url
  "http://example.com/FIXME"
  :plugins
  [[lein-ring "0.8.7"] [clj-sql-up "0.2.0"]]
  :description
  "A simple web app to track FIFA matches statistics."
  :min-lein-version "2.0.0")