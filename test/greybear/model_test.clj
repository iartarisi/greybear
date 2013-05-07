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
