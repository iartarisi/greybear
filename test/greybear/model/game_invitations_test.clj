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
      (create-game-invitation white-id black-id) => {:host_id white-id
                                                     :guest_id black-id}))
  (fact "does not create a invitation when one of the players does not exist"
    (create-game-invitation 404 403) => nil)
  (fact "does not create a invitation between one player"
    (let [player-id (create-player "black" "secret")]
      (create-game-invitation player-id player-id) => nil)))

(facts "about invited-by"
  (fact "returns nothing when there are no invitations"
    (create-player "santa" "secret")
    (invited-by 1) => ())
  (fact "returns the right guest when there are multiple invitations"
    (let [test-host-id (create-player "player1" "secret")
          test-guest1-id (create-player "player2" "secret")
          test-guest2-id (create-player "player3" "secret")
          other-id (create-player "player4" "secret")]

      (create-game-invitation test-host-id test-guest1-id)
      (create-game-invitation test-host-id test-guest2-id)

      (create-game-invitation test-guest1-id other-id)
      (create-game-invitation test-guest1-id test-host-id)
      (create-game-invitation other-id test-host-id)

      (let [result (invited-by test-host-id)]
        (count result) => 2
        (map :id result) => (list test-guest1-id test-guest2-id)))))
