(ns greybear.model-test
  (:use clojure.test
        [korma.core]
        greybear.model))

(defn clear-database []
  (teardown)
  (setup))

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

