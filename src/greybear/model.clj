(ns greybear.model
  (:require [clojure.java.jdbc :as jdbc])
  (:use [clojure.string :only [split]]
        [korma db core]
        [cemerick.friend.credentials :only [hash-bcrypt bcrypt-verify]]
        [greybear.utils :only [place-stone parse-int]]))

(def psql {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost/greybear"
           :user "greybear"
           :password "greybear"})

(defn setup
  "use with a jdbc connection"
  []
  (try
    (jdbc/create-table :players
                       [:id :serial "primary key"]
                       [:name "varchar" :unique]
                       [:password "varchar"])

    (jdbc/create-table :games
                       [:id :serial "primary key"]
                       [:white_id :serial "references players (id)"]
                       [:black_id :serial "references players (id)"]
                       [:stones "varchar"])

    (jdbc/create-table :moves
                       [:move "varchar"]
                       [:ordinal "smallint"]
                       [:game_id :serial "references games (id)"]
                       ["PRIMARY KEY" "(game_id, ordinal)"]
                       ["UNIQUE" "(game_id, move)"])
    (catch Exception e
      (.getNextException e))))

(defn teardown
  "use with a jdbc connection"
  []
  (try
    (jdbc/drop-table :moves)
    (jdbc/drop-table :games)
    (jdbc/drop-table :players)
    (catch Exception e
      (.getNextException e))))

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

(defentity moves
  (table :moves)
  (entity-fields :move :order)
  (belongs-to games {:fk :game_id}))

(defentity games
  (table :games)
  (entity-fields :board :white :black :moves)
  (has-many moves {:fk :game_id})
  (belongs-to player-w {:fk :white_id})
  (belongs-to player-b {:fk :black_id}))

(defn read-game [game-id]
  "Return a 19x19 lazyseq of chars e.g. [\1 \0 \0 \2 ...]"
  (-> (select games
              (fields :stones
                      [:white.name :white]
                      [:black.name :black])
              (where {:id game-id})
              (join [players :white] (= :games.white_id :white.id))
              (join [players :black] (= :games.black_id :black.id))
              (limit 1))
      first
      (update-in [:stones] #(map char %))))

(defn last-move [game-id]
  "Return a map like {:player 1 :move \"4-5\"}, player 1 is black, 0 is white"
  (let [move (first (select moves
                            (where {:game_id game-id})
                            (order :ordinal :DESC)
                            (limit 1)))]
    (when move
      (let [[x y] (map parse-int (split (:move move) #"-"))]
        {:player (mod (:ordinal move) 2)
         :x x
         :y y}))))

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

(defn make-move
  "Make a new move in a game"
  [game position]
  ;; TODO a subselect with coalesce would've been nicer, but korma
  ;; doesn't make that easy
  (let [next-ordinal (+ 1 (or (:max
                               (first
                                (select moves
                                        (where {:game_id game})
                                        (aggregate (max :ordinal) :max))))
                              0))
        color (if (odd? next-ordinal) \1 \2)]
    ;; TODO - rollback
    ;; figure out why valid? doesn't work for rollbacks
    (transaction
     (insert moves
             (values {:move position
                      :game_id game
                      :ordinal next-ordinal}))
     (update games
             (set-fields {:stones
                          (place-stone (:stones (read-game game))
                                       position
                                       color)})
             (where {:id game})))))
