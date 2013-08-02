(ns greybear.websocket-test
  (:use [midje.sweet]
        [greybear.websocket :only [on-message refresh]]
        [greybear.model :only [new-move]]))

(fact "on-message new-move makes a new move and calls refresh"
 (on-message nil "new-move: 142 1-3") => nil
 (provided
  (new-move 142 "1-3") => anything
  (refresh anything) => nil))
