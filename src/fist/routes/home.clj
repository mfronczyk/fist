(ns fist.routes.home
  (:use compojure.core)
  (:require [fist.views.layout :as layout]
            [fist.models.db :as db]
            [taoensso.timbre :as timbre]
            [clj-time.core :as time]
            [formative.core :as f]
            [formative.parse :as fp]
            [korma.db :as korma-db]
            [noir.response :as response]))

(def match-form
  {:fields [{:name :home_player_id :type :select :datatype :int
             :options (fn [] (map #(vector (:id %) (:name %)) (db/get-players)))}
            {:name :home_team_name :type :text :datatype :str}
            {:name :home_score :type :select :datatype :int :options (range 10)}
            {:name :away_player_id :type :select :datatype :int
             :options (fn [] (map #(vector (:id %) (:name %)) (db/get-players)))}
            {:name :away_team_name :type :text :datatype :str}
            {:name :away_score :type :select :datatype :int :options (range 10)}]
   :validations [[:required [:home_player_id :home_team_name :home_score :away_player_id :away_team_name :away_score]]]})

(defn home-page []
  (layout/render
    "home.html" {:players (db/get-players)
                 :scores (range 10)
                 :teams (db/get-teams)}))

(defn create-match [params]
  (korma-db/transaction
    (let [match (fp/parse-params match-form params)
          team1 (db/get-team-by-name (:home_team_name match))
          home-team-id (if-not team1 (:id (db/create-team {:name (:home_team_name match)})) (:id team1))
          team2 (db/get-team-by-name (:away_team_name match))
          away-team-id (if-not team2 (:id (db/create-team {:name (:away_team_name match)})) (:id team2))]

      (db/create-match {:home_player_id (:home_player_id match)
                        :home_team_id home-team-id
                        :home_score (:home_score match)
                        :away_player_id (:away_player_id match)
                        :away_team_id away-team-id
                        :away_score (:away_score match)
                        :occured_at (time/now)})))
  (response/redirect "/stats"))


(defn stats-page []
  (let [stats (db/get-all-stats)]
    (layout/render
      "stats.html" {:ranking (db/get-ranking stats)})))

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/" {params :params} (create-match params))
  (GET "/stats" [] (stats-page)))

