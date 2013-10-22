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
            [hiccup.page :as page]
            [korma.db :as korma-db]
            [noir.response :as response]))

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
             :options (range 10) :placeholder ""}]
   :validations [[:required [:home_player_id :home_team_name :home_score :away_player_id :away_team_name :away_score]]]})

(defn home-page []
  (layout/render
    "home.html" {:home-player-select (util/render-form-field :home_player_id match-form)
                 :home-team-input (util/render-form-field :home_team_name match-form)
                 :home-score-select (util/render-form-field :home_score match-form)
                 :away-player-select (util/render-form-field :away_player_id match-form)
                 :away-team-input (util/render-form-field :away_team_name match-form)
                 :away-score-select (util/render-form-field :away_score match-form)}))

(defn create-match [params]
  (korma-db/transaction
    (let [match (fp/parse-params match-form params)
          team1 (db/get-team-by-name (:home_team_name match))
          home-team-id (if-not team1 (:id (db/create-team {:name (:home_team_name match)})) (:id team1))
          team2 (db/get-team-by-name (:away_team_name match))
          away-team-id (if-not team2 (:id (db/create-team {:name (:away_team_name match)})) (:id team2))
          ]
      (db/create-match {:home_player_id (:home_player_id match)
                        :home_team_id home-team-id
                        :home_score (:home_score match)
                        :away_player_id (:away_player_id match)
                        :away_team_id away-team-id
                        :away_score (:away_score match)
                        :occured_at (time-coerce/to-sql-time (time/now))})))
  (response/redirect "/stats"))


(defn stats-page []
  (layout/render
    "stats.html" {:stats (sort #(compare (:s %2) (:s %1))
                           (filter #(> (:m %) 0)
                             (map #(assoc (db/get-stats (:id %)) :name (:name %)) (db/get-players))))}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/" {params :params} (create-match params))
  (GET "/stats" [] (stats-page)))

