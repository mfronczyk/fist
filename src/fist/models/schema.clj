(ns fist.models.schema
  (:require [clojure.java.jdbc :as sql]))

(def db-host "localhost")
(def db-port 5432)
(def db-name "fist")

(def db-spec
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname (str "//" db-host ":" db-port "/" db-name)
   :user "postgres"
   :password "postgres"})
