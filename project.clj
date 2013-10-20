(defproject
  fist
  "0.1.0-SNAPSHOT"
  :dependencies
  [[org.clojure/clojure "1.5.1"]
   [lib-noir "0.7.1"]
   [compojure "1.1.5"
    :exclusions
    [org.clojure/tools.macro
     ring/ring-core]]
   [ring-server "0.3.0"]
   [selmer "0.4.3"]
   [com.taoensso/timbre "2.6.2"]
   [com.postspectacular/rotor "0.1.0"]
   [com.taoensso/tower "1.7.1"]
   [markdown-clj "0.9.33"]
   [postgresql/postgresql "9.1-901.jdbc4"]
   [korma "0.3.0-RC5"]
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [org.clojure/clojurescript "0.0-1896"
    :exclusions
    [org.clojure/tools.reader]]
   [domina "1.0.1"]
   [prismatic/dommy "0.1.1"]
   [cljs-ajax "0.2.0"]
   [clj-time "0.6.0"]
   [formative "0.8.7"]]
  :cljsbuild
  {:builds
   [{:source-paths ["src-cljs"],
     :compiler
     {:pretty-print false,
      :output-to "resources/public/js/site.js",
      :optimizations :advanced}}]}
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
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.2.0"]]}}
  :url
  "http://example.com/FIXME"
  :plugins
  [[lein-ring "0.8.7"] [lein-cljsbuild "0.3.3"] [clj-sql-up "0.2.0"]]
  :description
  "A simple web app to track FIFA matches statistics."
  :min-lein-version "2.0.0")