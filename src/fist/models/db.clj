(ns fist.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [fist.models.schema :as schema]))

(defdb db schema/db-spec)

(declare players teams matches)

(defentity players
  (has-many matches))

(defentity teams
  (has-many matches))

(defentity matches
  (belongs-to players)
  (belongs-to teams))

(defn create-player [player]
  (insert players
    (values player)))

(defn create-team [team]
  (insert teams
    (values team)))

(defn create-match [match]
  (insert matches
    (values match)))

(defn get-player [id]
  (first (select players
                 (where {:id id})
                 (limit 1))))
