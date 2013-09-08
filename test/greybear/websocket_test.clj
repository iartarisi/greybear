(ns greybear.websocket-test
  (:use [midje.sweet]
        [ring.middleware.session.store :only [write-session]]
        [greybear.websocket :only [on-message refresh make-move*]]
        [greybear.server :only [grey-session-store session-key get-identity]]
        [greybear.model :only [make-move user-turn?]]))

(fact "on-message make-move makes a new move and calls refresh"
  (let [message (str "{\"cmd\": \"make-move\", "
                     "\"game_id\": 142, "
                     "\"user_id\": 3,"
                     "\"x\": 1,"
                     "\"y\": 3}")]
    (on-message ...conn... message) => anything
    (provided
      (make-move* ...conn... {:cmd "make-move"
                              :game_id 142
                              :user_id 3
                              :x 1
                              :y 3}) => anything)))

(facts "about make-move*"
  (let [session-data {:cemerick.friend/identity
                      {:current "foo",
                       :authentications {"foo" {:identity "foo",
                                                :user-id ...user-id...
                                                :username "foo"}}}}
        cookie (write-session
                          grey-session-store session-key session-data)
        message {:user_id ...user-id...
                 :game_id ...game-id...
                 :cookie cookie
                 :x 19
                 :y 19}]
    (make-move* ...conn... message) => nil
    (provided
      (make-move ...game-id... "19-19") => nil
      (user-turn? ...game-id... ...user-id...) => true
      (refresh ...conn... message) => nil)))
