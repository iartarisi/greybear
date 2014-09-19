(ns greybear.model.game-invitations-test
  (:use midje.sweet
        [korma db core]
        [greybear.model.ddl :only [setup teardown game-invitations]]
        [greybear.model.players :only [create-player]]
        [greybear.model.game-invitations]
        [greybear.model-test :only [test-db-spec]]))


(namespace-state-changes
 [(around :facts (with-db test-db-spec ?form))
  (before :facts (setup test-db-spec))
  (after :facts (teardown test-db-spec))])

(facts "about create-game-invitation"
  (fact "creates a game invitation"
    (let [white-id (create-player "white" "secret")
          black-id (create-player "black" "secret")]
      (create-game-invitation white-id black-id) => {:white_id white-id
                                                   :black_id black-id}))
  (fact "does not create a invitation when one of the players does not exist"
    (create-game-invitation 404 403) => nil)
  (fact "does not create a invitation between one player"
    (let [player-id (create-player "black" "secret")]
      (create-game-invitation player-id player-id) => nil)))
