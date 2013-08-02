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

(facts "about verify-user-password"
  (fact "valid password"
    (create-user "foo" "bar")
    (verify-user-password "foo" "bar") => true)

  (fact "returns false when wrong password is given"
    (create-user "foo" "bar")
    (verify-user-password "foo" "qux") => false)

  (fact "returns nil when the given user doesn't exist"
    (verify-user-password "bogus" "qux") => nil))

(facts "about read-game"
  (create-user "user1" "foo")
  (create-user "user2" "foo")
  (fact "reads a game initialized to an empty board"
    (insert games
            (values {:white_id (subselect players
                                          (fields :id)
                                          (where {:name [like "user1"]}))
                     :black_id (subselect players
                                          (fields :id)
                                          (where {:name [like "user2"]}))
                     :stones starting-stones}))
    (read-game 1) => {:white "user1" :black "user2" :stones starting-stones}))

(facts "about new-game"
  (fact "creating a game between inexistent users throws an error"
    ;; XXX think about raising better errors and at which layer
    (new-game 1 2) => (throws PSQLException))

  (fact "saves new game to the database"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (new-game 1 2) => 1
    (select games) => [{:stones starting-stones
                        :white_id 1
                        :black_id 2
                        :id 1}]))

(facts "about new-move"
  (fact "saves a new move in the database"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (new-game 1 2)
    (new-move 1 "4-5") => truthy
    (select moves) => [{:game_id 1, :ordinal 1, :move "4-5"}])

  (fact "new moves have correct ordinals"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (new-game 1 2)
    (new-move 1 "4-5")
    (new-move 1 "5-6")
    (new-move 1 "3-6")

    (select moves
            (fields :ordinal)) => [{:ordinal 1} {:ordinal 2} {:ordinal 3}])

  ;; TODO - raise better exceptions here and bellow
  (fact "same move in one game should raise exception"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (new-game 1 2)
    (new-move 1 "4-5")

    (new-move 1 "4-5") => (throws PSQLException #"duplicate key value violates unique constraint"))

  (fact "same ordinal in one game raises exception"
    (create-user "user1" "foo")
    (create-user "user2" "bar")
    (new-game 1 2)
    (new-move 1 "4-5")

    (insert moves
            (values {:move "3-10"
                     :game_id 1
                     :ordinal 1})) => (throws PSQLException #"duplicate key value violates unique constraint")))
