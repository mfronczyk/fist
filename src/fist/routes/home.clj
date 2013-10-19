(ns fist.routes.home
  (:use compojure.core)
  (:require [fist.views.layout :as layout]
            [fist.util :as util]
            [fist.models.db :as db]
            [taoensso.timbre :as timbre]
            [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn create-match [match]
  (let [home-team (db/get-team-by-name (:home_team_name match))
        away-team (db/get-team-by-name (:away_team_name match))]
    (db/create-match {:home_player_id (Integer/parseInt (:home_player_id match))
                      :home_team_id (:id home-team)
                      :home_score (Integer/parseInt (:home_score match))
                      :away_player_id (Integer/parseInt (:away_player_id match))
                      :away_team_id (:id away-team)
                      :away_score (Integer/parseInt (:away_score match))
                      :occured_at (time-coerce/to-sql-time (time/now))})))


(defn stats-page []
  (layout/render "stats.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/" {match :params} (create-match match))
  (GET "/stats" [] (stats-page)))

