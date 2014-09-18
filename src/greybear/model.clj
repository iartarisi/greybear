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

(ns greybear.model
  (:use [clojure.string :only [split]]
        [clojure.tools.logging :as log]
        [korma db core]

        greybear.model.ddl
        [greybear.utils :only [place-stone parse-int]]))

(def ^:const ANONYMOUS 0)
(def ^:const BLACK 1)
(def ^:const WHITE 2)
(def ^:const starting-stones
  (apply str (repeat (* 19 19) "0")))

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
                            (where {:games_id game-id})
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
     (= user-id (:black_id game)) (if (nil? last-player)
                                    :me
                                    (if (= BLACK last-player)
                                      :opponent
                                      :me))
     (= user-id (:white_id game)) (if (nil? last-player)
                                    :opponent
                                    (if (= WHITE last-player)
                                      :opponent
                                      :me)))))

(defn user-turn?
  "Check if it's the current user's turn"
  [game-id user-id]
  (= :me (whos-turn game-id user-id)))

(defn create-game
  "Start a new game between two players, returns the new game's id"
  [black white]
  (let [game-id (:id (insert games
               (values {:stones starting-stones
                        :white_id white
                        :black_id black
                        :active true})))]
    (log/info "Game" game-id "created between" black "and" white)
    game-id))

(defn make-move
  "Make a new move in a game

  game - a database game id
  position - a string of the form '12-9'
  "
  [game position]
  ;; TODO a subselect with coalesce would've been nicer, but korma
  ;; doesn't make that easy
  (let [next-ordinal (+ 1 (or (:max
                               (first
                                (select moves
                                        (where {:games_id game})
                                        (aggregate (max :ordinal) :max))))
                              0))
        color (if (odd? next-ordinal) \1 \2)]
    ;; TODO - rollback
    ;; figure out why valid? doesn't work for rollbacks
    (transaction
     (insert moves
             (values {:move position
                      :games_id game
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

(defn load-credentials
  [user]
  (first (select players
                 (fields [:name :username]
                         [:id :user-id]
                         :password)
                 (where {:name user}))))
