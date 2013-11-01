(ns fist.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [fist.models.schema :as schema]
            [taoensso.timbre :as timbre]
            [clj-time.coerce :as time-coerce]))

(defdb db schema/db-spec)

(declare players teams matches)

(defentity players)

(defentity teams)

(defentity matches
  (belongs-to players)
  (belongs-to teams))

(defn create-match [match]
  (insert matches
    (values match)))

(defn get-home-matches [player-id start-date end-date]
  (select matches
    (where {:home_player_id player-id})
    (where {:occured_at [>= (time-coerce/to-sql-time start-date)]})
    (where {:occured_at [<= (time-coerce/to-sql-time end-date)]})))

(defn get-away-matches [player-id start-date end-date]
  (select matches
    (where {:away_player_id player-id})
    (where {:occured_at [>= (time-coerce/to-sql-time start-date)]})
    (where {:occured_at [<= (time-coerce/to-sql-time end-date)]})))

(defn get-player [id]
  (first (select players
           (where {:id id})
           (limit 1))))

(defn get-players []
  (select players
    (order :name)))

(defn home-matches-reducer [stats {home_score :home_score away_score :away_score}]
  (merge-with +
    stats
    (cond
      (> home_score away_score) {:w 1 :d 0 :l 0}
      (= home_score away_score) {:w 0 :d 1 :l 0}
      (< home_score away_score) {:w 0 :d 0 :l 1}
      :else {:w 0 :d 0 :l 0})))

(defn away-matches-reducer [stats {home_score :home_score away_score :away_score}]
  (merge-with +
    stats
    (cond
      (> home_score away_score) {:w 0 :d 0 :l 1}
      (= home_score away_score) {:w 0 :d 1 :l 0}
      (< home_score away_score) {:w 1 :d 0 :l 0}
      :else {:w 0 :d 0 :l 0})))

(defn get-stats [player-id start-date end-date]
  (let [stats (merge-with
                +
                (reduce home-matches-reducer {:w 0 :d 0 :l 0} (get-home-matches player-id start-date end-date))
                (reduce away-matches-reducer {:w 0 :d 0 :l 0} (get-away-matches player-id start-date end-date)))
        m (reduce + (vals  stats))
        p (+ (* (:w stats) 3) (:d stats))
        s (if (= m 0) 0 (float(/ p m)))]
    (assoc stats :m m :p p :s s)))

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



