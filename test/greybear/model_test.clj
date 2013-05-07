(ns greybear.model-test
  (:import org.postgresql.util.PSQLException)
  (:require [clojure.java.jdbc :as jdbc])
  (:use clojure.test
        korma.core
        greybear.model))

(def test-conn {:classname "org.postgresql.Driver"
                :subprotocol "postgresql"
                :subname "//localhost/greybear-test"
                :user "greybear-test"
                :password "greybear-test"})

(defn database-fixture [f]
  (jdbc/with-connection test-conn
    (try
      (setup)
      (f)
      (finally
        (teardown)))))

(use-fixtures :each database-fixture)


(deftest user-create
  (create-user "foo" "bar")
  (is (= (select players
                 (fields :name)
                 (limit 1))
         [{:name "foo"}])))

(deftest user-create-already-exists
  (create-user "foo" "bar")
  (is (nil? (create-user "foo" "bar")))
  (is (= {:cnt 1}
         (first (select players
                        (aggregate (count :*) :cnt))))))

(deftest verify-user-password-test
  (create-user "foo" "bar")
  (is (true? (verify-user-password "foo" "bar"))))

(deftest verify-user-password-no-user
  (is (nil? (verify-user-password "foo" "bar"))))

(deftest verify-user-password-wrong-password
  (create-user "foo" "bar")
  (is (false? (verify-user-password "foo" "qux"))))

(deftest read-game-test
  (create-user "user1" "foo")
  (create-user "user2" "bar")
  (insert games
          (values {:white_id (subselect players
                                        (fields :id)
                                        (where {:name [like "user1"]}))
                   :black_id (subselect players
                                        (fields :id)
                                        (where {:name [like "user2"]}))
                   :stones starting-stones}))
  (is (= {:white "user1" :black "user2" :stones starting-stones}
         (read-game 1))))

(deftest new-game-unknown-user-test
  ;; XXX think about raising better errors and at which layer
  (is (thrown-with-msg? PSQLException #"null value in column"
       (new-game "bogus1" "bogus2"))))

(deftest new-game-test
  (create-user "user1" "foo")
  (create-user "user2" "bar")
  (testing "return value"
    (is (= 1
           (new-game "user1" "user2"))))
  (testing "new game is in the database"
    (is (= {:stones starting-stones
            :white_id 1
            :black_id 2
            :id 1}
           (first (select games))))))
