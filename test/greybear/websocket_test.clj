(ns greybear.websocket-test
  (:use [midje.sweet]
        [greybear.websocket :only [on-message refresh get-playing]]
        [greybear.model :only [make-move]]))

(fact "on-message make-move makes a new move and calls refresh"
  (let [message (str "{\"cmd\": \"make-move\", "
                     "\"game_id\": 142, "
                     "\"user_id\": 3,"
                     "\"x\": 1,"
                     "\"y\": 3}")]
    (on-message nil message) => nil
    (provided
      (make-move 142 "1-3") => anything
      (refresh nil {:cmd "make-move"
                    :game_id 142
                    :user_id 3
                    :x 1
                    :y 3}) => nil)))

(facts "about get-playing"
  (fact "returns 0 when user is anonymous"
    (get-playing ...color... 0 ...game...) => 0)

  (fact "returns matching color when user is not anonymous"
    (let [game {:black_id 111
                :white_id 222}]
      (get-playing 1 111 game) => 1
      (get-playing 2 222 game) => 2))
  (fact "returns 0 when user is not anonymous, but it's not her turn"
    (let [game {:black_id 111
                :white_id 222}]
      (get-playing 1 222 game) => 0
      (get-playing 2 111 game) => 0
      (get-playing 2 1 game) => 0)))


