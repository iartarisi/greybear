(ns greybear.websocket-test
  (:use [midje.sweet]
        [greybear.websocket :only [on-message refresh]]
        [greybear.model :only [make-move]]))

(fact "on-message make-move makes a new move and calls refresh"
 (on-message nil "make-move: 142 1-3") => nil
 (provided
  (make-move 142 "1-3") => anything
  (refresh anything anything) => nil))
