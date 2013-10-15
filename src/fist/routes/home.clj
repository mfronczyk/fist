(ns fist.routes.home
  (:use compojure.core)
  (:require [fist.views.layout :as layout]
            [fist.util :as util]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn stats-page []
  (layout/render "stats.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/stats" [] (stats-page)))
