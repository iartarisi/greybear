(ns greybear.websocket-test
  (:use [midje.sweet]
        [greybear.websocket :only [on-message refresh]]
        [greybear.model :only [make-move]]))

(fact "on-message make-move makes a new move and calls refresh"
  (let [message "{\"cmd\": \"make-move\", \"game_id\": 142, \"user_id\": 3, \"x\": 1, \"y\": 3}"]
    (on-message nil message) => nil
    (provided
      (make-move 142 "1-3") => anything
      (refresh nil {:cmd "make-move"
                    :game_id 142
                    :user_id 3
                    :x 1
                    :y 3}) => nil)))
