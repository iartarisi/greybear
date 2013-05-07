(ns greybear.model-test
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


(def test-stones
  (apply str (repeat (* 19 19) "0")))

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
                   :stones test-stones}))
  (is (= {:white "user1" :black "user2" :stones test-stones}
         (read-game 1))))
