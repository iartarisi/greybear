(ns greybear.model.players-test
  (:use midje.sweet
        [korma db core]
        greybear.model.players
        [greybear.model.ddl :only [setup teardown players]]
        [greybear.model-test :only [test-db-spec]]))


(namespace-state-changes
 [(around :facts (with-db test-db-spec ?form))
  (before :facts (setup test-db-spec))
  (after :facts (teardown test-db-spec))])

(facts "about get-player"
  (fact "returns player when it exists"
    (insert players
            (values {:name "foo" :password "bar"})) => truthy
    (get-player 1) => {:id 1, :name "foo", :password "bar"})
  (fact "returns nil when player does not exist"
    (get-player 404) => nil))

(facts "about create-player"
  (fact "creates a new player in the database"
    (create-player "foo" "bar") => truthy
    (select players (fields :name)) => [{:name "foo"}])

  (fact "doesn't raise an error if the user already exists"
    (create-player "foo" "bar") => truthy
    (select players (aggregate (count :*) :cnt)) => [{:cnt 1}]))
