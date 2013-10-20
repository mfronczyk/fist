(ns fist.routes.home
  (:use compojure.core)
  (:require [fist.views.layout :as layout]
            [fist.util :as util]
            [fist.models.db :as db]
            [taoensso.timbre :as timbre]
            [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]
            [formative.core :as f]
            [formative.parse :as fp]
            [hiccup.page :as page]))

(def match-form
  {:fields [{:name :home_player_id :label "Player" :type :select :datatype :int :class "form-control" :id "homePlayer"
             :options (fn [] (map #(vector (:id %) (:name %)) (db/get-players))) :placeholder ""}
            {:name :home_team_name :label "Team" :type :text :datatype :str :class "form-control" :id "homeTeam"}
            {:name :home_score :label "Score" :type :select :datatype :int :class "form-control" :id "homeScore"
             :options (range 10) :placeholder ""}
            {:name :away_player_id :label "Player" :type :select :datatype :int :class "form-control" :id "awayPlayer"
             :options (fn [] (map #(vector (:id %) (:name %)) (db/get-players))) :placeholder ""}
            {:name :away_team_name :label "Team" :type :text :datatype :str :class "form-control" :id "awayTeam"}
            {:name :away_score :label "Score" :type :select :datatype :int :class "form-control" :id "awayScore"
             :options (range 10) :placeholder ""}]})

(defn home-page []
  (layout/render
    "home.html" {:home-player-select (util/render-form-field :home_player_id match-form)
                 :home-team-input (util/render-form-field :home_team_name match-form)
                 :home-score-select (util/render-form-field :home_score match-form)
                 :away-player-select (util/render-form-field :away_player_id match-form)
                 :away-team-input (util/render-form-field :away_team_name match-form)
                 :away-score-select (util/render-form-field :away_score match-form)}))

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

