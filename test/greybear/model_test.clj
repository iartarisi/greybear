(ns greybear.model-test
  (:import org.postgresql.util.PSQLException)
  (:require [clojure.java.jdbc :as jdbc])
  (:use midje.sweet
        [korma core db]
        greybear.model))

(def ^:dynamic test-conn {:classname "org.postgresql.Driver"
                          :subprotocol "postgresql"
                          :subname "//localhost/greybear-test"
                          :user "greybear-test"
                          :password "greybear-test"})

(namespace-state-changes
 [(around :facts (jdbc/with-connection test-conn ?form))
  (before :facts (jdbc/with-connection test-conn (setup)))
  (after :facts (jdbc/with-connection test-conn (teardown)))])

(facts "about create-user"
  (fact "creates a new player in the database"
    (create-user "foo" "bar") => truthy
    (select players (fields :name)) => [{:name "foo"}])

  (fact "doesn't raise an error if the user already exists"
    (create-user "foo" "bar") => truthy
    (select players (aggregate (count :*) :cnt)) => [{:cnt 1}]))

(facts "about read-game"
  (fact "returns nil when a game could not be found"
    (read-game 1) => nil)
  (fact "reads a game initialized to an empty board"
    (create-user "user1" "foo")
    (create-user "user2" "foo")
    (insert games
            (values {:white_id (subselect players
                                          (fields :id)
                                          (where {:name [like "user1"]}))
                     :black_id (subselect players
                                          (fields :id)
                                          (where {:name [like "user2"]}))
                     :stones starting-stones}))
    (read-game 1) => {:white "user1" :black "user2" :black_id 2 :white_id 1
                      :stones (map char starting-stones)}))

(facts "about create-game"
  (fact "creating a game between inexistent users throws an error"
    ;; XXX think about raising better errors and at which layer
    (create-game 1 2) => (throws PSQLException))

  (fact "saves new game to the database"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (create-game 1 2) => 1
    (select games) => [{:stones starting-stones
                        :white_id 2
                        :black_id 1
                        :id 1}]))

(facts "about make-move"
  (fact "saves a new move in the database"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (create-game 1 2)
    (make-move 1 "1-1") => truthy
    (select moves) => [{:game_id 1, :ordinal 1, :move "1-1"}]
    (select games) => [{:black_id 1 :id 1 :white_id 2
                        :stones "0000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"}])

  (fact "new moves have correct ordinals"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (create-game 1 2)
    (make-move 1 "4-5")
    (make-move 1 "5-6")
    (make-move 1 "3-6")

    (select moves
            (fields :ordinal)) => [{:ordinal 1} {:ordinal 2} {:ordinal 3}])

  ;; TODO - raise better exceptions here and bellow
  (fact "same move in one game should raise exception"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (create-game 1 2)
    (make-move 1 "4-5")

    (make-move 1 "4-5") => (throws PSQLException #"duplicate key value violates unique constraint"))

  (fact "same ordinal in one game raises exception"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (create-game 1 2)
    (make-move 1 "4-5")

    (insert moves
            (values {:move "3-10"
                     :game_id 1
                     :ordinal 1})) => (throws PSQLException #"duplicate key value violates unique constraint")))

(facts "about last-move"
  (fact "returns nil values when there is no last move"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (create-game 1 2)

    (last-move 1) => nil)
  (fact "returns last move"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (create-game 1 2)
    (make-move 1 "4-5")
    (make-move 1 "5-6")
    (make-move 1 "14-3")

    (last-move 1) => {:player 1 :x 14 :y 3})) 

(facts "about games"
  (fact "returns a list of games with the right contents"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (create-game 1 2)
    (create-game 2 1)
    (make-move 1 "4-5")
    (make-move 1 "5-6")
    (make-move 1 "14-3")
    (make-move 2 "14-3")

    (games-list) => [{:id 1 :black_id 1 :white_id 2 :moves 3}
                     {:id 2 :black_id 2 :white_id 1 :moves 1}]))


(facts "about get-playing"
  (fact "returns BLACK if there are no previous moves and current user is black"
    (let [game {:black_id 111, :white_id 222}]
      (get-playing ...game-id... (:black_id game)) => BLACK
      (provided
        (read-game ...game-id...) => game
        (last-move ...game-id...) => nil)))
  (fact "returns matching color when user is not anonymous"
    (tabular
     (let [game {:black_id 111
                 :white_id 222}]
       (get-playing ...game-id... ?user-id) => ?result
       (provided
         (read-game ...game-id...) => game
         (last-move ...game-id...) => {:player ?player
                                       :x irrelevant
                                       :y irrelevant}))
     ?user-id          ?player ?result
     (:black_id game)  WHITE        BLACK
     (:white_id game)  BLACK        WHITE
     (:white_id game)  WHITE        nil
     ANONYMOUS         WHITE        nil)))

(facts "about whos-turn"
  (fact "when user is not playing the game"
    (let [user-id 1]
      (whos-turn ...game-id... user-id) => nil
      (provided
        (read-game ...game-id...) => {:black_id 0 :white_id 0})))

  (fact "when user is in the current game"
    (tabular
     (let [game {:black_id 111
                 :white_id 222}]
       (whos-turn ...game-id... ?user-id) => ?result
       (provided
         (read-game ...game-id...) => game
         (last-move ...game-id...) => {:player ?last-player}))
     ?user-id ?last-player ?result
     111      222          :me
     111      111          :opponent
     222      222          :opponent
     222      111          :me)))
