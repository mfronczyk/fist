;; migrations/20131015133352459-init.clj

(defn up []
  ["CREATE TABLE players (
      id serial PRIMARY KEY,
      name varchar(255) UNIQUE)"
   "CREATE TABLE teams (
      id serial PRIMARY KEY,
      name varchar(255) UNIQUE)"
   "CREATE TABLE matches (
      id serial PRIMARY KEY,
      home_player_id integer,
      home_team_id integer,
      home_score integer,
      away_player_id integer,
      away_team_id integer,
      away_score integer,
      occured_at timestamp without time zone)"])

(defn down []
  ["DROP TABLE matches"
   "DROP TABLE teams"
   "DROP TABLE players"])