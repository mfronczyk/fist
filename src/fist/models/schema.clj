(ns fist.models.schema
  (:use environ.core))

(def db-spec
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname (env :db-uri)
   :user (env :db-username)
   :password (env :db-password)})
