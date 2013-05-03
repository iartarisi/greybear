(ns greybear.model
  (:require [clojure.java.jdbc :as jdbc])
  (:use [korma db core]))

(def psql {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost/greybear"
           :user "greybear"
           :password "greybear"})

(defn setup
  []
  (jdbc/with-connection psql
    (jdbc/create-table :players
                       [:id :serial "primary key"]
                       [:name "varchar"])

    (jdbc/create-table :games
                       [:id :serial "primary key"]
                       [:white_id :serial "references players (id)"]
                       [:black_id :serial "references players (id)"]
                       [:stones "varchar"])))

(defdb korma-db psql)

(declare player-b player-w games)

(defentity players
  (table :players)
  (entity-fields :name)
  (has-many games {:fk :black_id}))

(defentity player-b
  (table :players)
  (entity-fields :name)
  (has-many games {:fk :black_id}))

(defentity player-w
  (table :players)
  (entity-fields :name)
  (has-many games {:fk :white_id}))

(defentity games
  (table :games)
  (entity-fields :board :white :black)
  (belongs-to player-w {:fk :white_id})
  (belongs-to player-b {:fk :black_id}))