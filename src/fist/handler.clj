(ns fist.handler
  (:require [compojure.core :refer [defroutes]]
            [fist.routes.home :refer [home-routes]]
            [noir.util.middleware :as middleware]
            [ring.middleware.params :as ring-params]
            [ring.middleware.keyword-params :as ring-keyword-params]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [com.postspectacular.rotor :as rotor]))

(defroutes
  app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info,
     :enabled? true,
     :async? false,
     :max-message-per-msecs nil,
     :fn rotor/append})
  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "fist.log", :max-size (* 512 1024), :backlog 10})
  (timbre/info "fist started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "fist is shutting down..."))

(def app
 (middleware/app-handler
   [home-routes app-routes]
   :middleware
   [ring-params/wrap-params ring-keyword-params/wrap-keyword-params]
   :access-rules
   []
   :formats
   [:json-kw :edn]))

(def war-handler (middleware/war-handler app))

