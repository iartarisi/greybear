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

(defn clear-database []
  (jdbc/with-connection test-conn
    (teardown)
    (setup)))

(deftest user-create
  (clear-database)
  (create-user "foo" "bar")
  (is (= (select players
                 (fields :name)
                 (limit 1))
         [{:name "foo"}])))

(deftest user-create-already-exists
  (user-create)
  (is (nil? (create-user "foo" "bar")))
  (is (= {:cnt 1}
         (first (select players
                        (aggregate (count :*) :cnt))))))

(deftest verify-user-password-test
  (user-create)
  (is (true? (verify-user-password "foo" "bar"))))

(deftest verify-user-password-no-user
  (clear-database)
  (is (nil? (verify-user-password "foo" "bar"))))

(deftest verify-user-password-wrong-password
  (user-create)
  (is (false? (verify-user-password "foo" "qux"))))
