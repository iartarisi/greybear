;; Copyright (C) 2013-2014 by Ionuț Arțăriși

;; This file is part of Greybear.

;; Greybear is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; Greybear is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with Greybear.  If not, see <http://www.gnu.org/licenses/>.

(ns greybear.model.ddl
  (:require [clojure.java.jdbc :as jdbc])
  (:use [korma db core]))

(def psql {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost/greybear"
           :user "greybear"
           :password "greybear"})

(defn setup
  "Setup the database tables"
  [dbspec]
  (try
    (jdbc/db-do-commands dbspec
     (jdbc/create-table-ddl :players
                            [:id :serial "primary key"]
                            [:name "varchar" :unique]
                            [:password "varchar"]
                            [:looking :boolean "NOT NULL" "DEFAULT FALSE"])

     (jdbc/create-table-ddl :games
                            [:id :serial "primary key"]
                            [:white_id :serial "references players (id)"]
                            [:black_id :serial "references players (id)"]
                            [:stones :varchar]
                            [:active :boolean "NOT NULL"])

     (jdbc/create-table-ddl :moves
                            [:move "varchar"]
                            [:ordinal "smallint"]
                            [:games_id :serial "references games (id)"]
                            ["PRIMARY KEY" "(games_id, ordinal)"]
                            ["UNIQUE" "(games_id, move)"]))
    (catch Exception e
      (.getNextException e))))

(defn teardown
  "use with a jdbc connection"
  [dbspec]
  (try
    (jdbc/db-do-commands dbspec
                         (jdbc/drop-table-ddl :moves)
                         (jdbc/drop-table-ddl :games)
                         (jdbc/drop-table-ddl :players))
    (catch Exception e
      (.getNextException e))))

(defdb korma-db psql)

(declare player-b player-w games)

(defentity players
  (table :players)
  (has-many games {:fk :black_id}))

;; player-a and player-b are just hacks to lie to korma that we have two
;; different entities. Otherwise it won't allow us to create multiple
;; FKs to the same table
(defentity player-b
  (table :players)
  (fields :name)
  (has-many games {:fk :black_id}))

(defentity player-w
  (table :players)
  (fields :name)
  (has-many games {:fk :white_id}))

(defentity moves
  (table :moves)
  (belongs-to games {:fk :games_id}))

(defentity games
  (table :games)
  (has-many moves)
  (belongs-to player-w {:fk :white_id})
  (belongs-to player-b {:fk :black_id}))
