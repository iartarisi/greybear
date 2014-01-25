(ns greybear.model
  (:require [clojure.java.jdbc :as jdbc])
  (:use [clojure.string :only [split]]
        [korma db core]
        [cemerick.friend.credentials :only [hash-bcrypt bcrypt-verify]]
        [greybear.utils :only [place-stone parse-int]]))

(def ^:const ANONYMOUS 0)
(def ^:const BLACK 1)
(def ^:const WHITE 2)
(def ^:const starting-stones
  (apply str (repeat (* 19 19) "0")))


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

(defn read-game
  "Return a 19x19 lazyseq of chars e.g. [\1 \0 \0 \2 ...]"
  [game-id]
  (let [game (first (select games
                            (fields :stones
                                    [:white.name :white]
                                    [:black.name :black]
                                    [:white.id :white_id]
                                    [:black.id :black_id])
                            (where {:id game-id})
                            (join [players :white] (= :games.white_id :white.id))
                            (join [players :black] (= :games.black_id :black.id))
                            (limit 1)))]
    (when (:stones game)
      (update-in game [:stones] #(map char %)))))

(defn last-move
  "Return a map like {:player BLACK :x 4 :y 5}
  Returns nil when there is no last move"
  [game-id]
  (let [move (first (select moves
                            (where {:game_id game-id})
                            (order :ordinal :DESC)
                            (limit 1)))]
    (when move
      (let [[x y] (map parse-int (split (:move move) #"-"))]
        {:player (if (zero? (mod (:ordinal move) 2))
                   WHITE
                   BLACK)
         :x x
         :y y}))))

(defn get-playing
  "Return who is playing the next move
    nil - it is not the current user's turn
    WHITE - it is the user's turn and she plays white
    BLACK - it is the user's turn and she plays black"
  [game-id user-id]
  (let [game (read-game game-id)
        last-player (:player (last-move game-id))]
    (cond
     (and (nil? last-player) (= user-id (:black_id game))) BLACK
     (and (= last-player BLACK) (= user-id (:white_id game))) WHITE
     (and (= last-player WHITE) (= user-id (:black_id game))) BLACK)))

(defn whos-turn
  "Return:
   nil - user is signed out or is not playing in this game
   :me - it is the user's turn
   :opponent - it is the opponent's turn"
  [game-id user-id]
  (let [game (read-game game-id)
        last-player (:player (last-move game-id))]
    (cond
     (= user-id (:black_id game)) (if (= BLACK last-player)
                                    :opponent
                                    :me)
     (= user-id (:white_id game)) (if (= WHITE last-player)
                                    :opponent
                                    :me))))

(defn user-turn?
  "Check if it's the current user's turn"
  [game-id user-id]
  (= :me (whos-turn game-id user-id)))

(defn create-user
  [username password]
  (try
    (insert players
            (values {:name username :password (hash-bcrypt password)}))
    (catch org.postgresql.util.PSQLException e
      nil)))

(defn create-game
  "Start a new game between two players, returns the new game's id"
  [black white]
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

(defn games-list
  "Return a list of games hashes"
  []
  (select games
          (fields :games.id :games.black_id :games.white_id)
          (join moves)
          (aggregate (count :moves.ordinal) :moves :games.id)
          (order :games.id :ASC)))
