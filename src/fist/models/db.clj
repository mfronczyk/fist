(ns fist.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [fist.models.schema :as schema]
            [taoensso.timbre :as timbre]
            [clj-time.coerce :as time-coerce]
            [clj-time.core :as time]))

(defdb db schema/db-spec)

(declare players teams matches)

(defentity players)

(defentity teams)

(defentity matches
  (belongs-to players)
  (belongs-to teams))

(defn create-match [match]
  (insert matches
    (values (update-in match [:occured_at] time-coerce/to-sql-time))))

(defn get-matches [player-id start-date end-date]
  (select matches
    (where (or (= :home_player_id player-id)
             (= :away_player_id player-id)))
    (where {:occured_at [>= (time-coerce/to-sql-time start-date)]})
    (where {:occured_at [<= (time-coerce/to-sql-time end-date)]})
    (order :occured_at)))

(defn normalize-match [match player-id]
  (let [home-prefix (if (= (:home_player_id match) player-id) "player" "opponent")
        away-prefix (if (= (:home_player_id match) player-id) "opponent" "player")]
    {(keyword (str home-prefix "-player-id")) (:home_player_id match)
     (keyword (str home-prefix "-team-id")) (:home_team_id match)
     (keyword (str home-prefix "-score")) (:home_score match)
     (keyword (str away-prefix "-player-id")) (:away_player_id match)
     (keyword (str away-prefix "-team-id")) (:away_team_id match)
     (keyword (str away-prefix "-score")) (:away_score match)
     :occured-at (:occured_at match)}))

(defn get-normalized-matches [player-id start-date end-date]
  (map #(normalize-match % player-id) (get-matches player-id start-date end-date)))

(defn get-player [id]
  (first (select players
           (where {:id id})
           (limit 1))))

(defn get-players []
  (select players
    (order :name)))

(defn match-reducer [stats {player-score :player-score opponent-score :opponent-score :as match}]
  (conj stats
    (assoc
      (merge-with +
        (last stats)
        (cond
          (> player-score opponent-score) {:w 1 :d 0 :l 0}
          (= player-score opponent-score) {:w 0 :d 1 :l 0}
          :else {:w 0 :d 0 :l 1}))
      :match match)))

(defn get-stats [player-id start-date end-date]
  (map
    #(let [m (+ (:w %) (:d %) (:l %))]
        (assoc %
          :m m
          :s (float (/ (+ (* (:w %) 3) (:d %)) m))))
    (reduce match-reducer [] (get-normalized-matches player-id start-date end-date))))

(defn get-all-stats []
  (filter #(> (count (:stats %)) 0)
    (map
      #(identity {
         :name (:name %)
         :stats (get-stats
                  (:id %)
                  (time/first-day-of-the-month (time/now))
                  (time/last-day-of-the-month (time/now)))})
      (get-players))))

(defn get-ranking [stats]
  (sort #(compare (:s (:stats %2)) (:s (:stats %1))) (map #(update-in % [:stats] last) stats)))

(defn create-team [team]
  (insert teams
    (values team)))

(defn get-team-by-name [name]
  (first (select teams
           (where {:name name})
           (limit 1))))

(defn get-teams []
  (select teams
    (order :name)))
