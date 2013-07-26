(ns greybear.model
  (:require [clojure.java.jdbc :as jdbc])
  (:use [korma db core]
        [cemerick.friend.credentials :only [hash-bcrypt bcrypt-verify]]))

(def psql {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost/greybear"
           :user "greybear"
           :password "greybear"})

(defn setup
  "use with a jdbc connection"
  []
  (jdbc/create-table :players
                     [:id :serial "primary key"]
                     [:name "varchar" :unique]
                     [:password "varchar"])

  (jdbc/create-table :games
                     [:id :serial "primary key"]
                     [:white_id :serial "references players (id)"]
                     [:black_id :serial "references players (id)"]
                     [:stones "varchar"]))

(defn teardown
  "use with a jdbc connection"
  []
  (jdbc/drop-table :games)
  (jdbc/drop-table :players))

(defdb korma-db psql)

(declare player-b player-w games)

(defentity players
  (table :players)
  (entity-fields :name :password)
  (has-many games {:fk :black_id}))

;; player-a and player-b are just hacks to lie to korma that we have two
;; different entities. Otherwise it won't allow us to create multiple
;; FKs to the same table
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


(defn read-game [game-id]
  (first
   (select games
           (fields :stones
                   [:white.name :white]
                   [:black.name :black])
           (where {:id game-id})
           (join [players :white] (= :games.white_id :white.id))
           (join [players :black] (= :games.black_id :black.id))
           (limit 1))))

(defn verify-user-password
  "Returns a bool if the password matches or not or nil if the user does
  not exist."
  [username password]
  (let [stored-pass (:password (first (select players
                                              (fields :password)
                                              (where {:name username}))))]
    (if stored-pass
      (bcrypt-verify password stored-pass)
      nil)))

(defn create-user
  [username password]
  (try
    (insert players
            (values {:name username :password (hash-bcrypt password)}))
    (catch org.postgresql.util.PSQLException e
      nil)))


(def ^:const starting-stones
  (apply str (repeat (* 19 19) "0")))

(defn new-game
  "Start a new game between two players, returns the new game's id"
  [white black]
  (:id (insert games
               (values {:stones starting-stones
                        :white_id white
                        :black_id black}))))
