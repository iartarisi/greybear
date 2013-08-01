(ns greybear.websocket-test
  (:use [expectations]
        [greybear.websocket :only [on-message]]
        [greybear.model :only [new-move]]))


(expect (interaction (new-move 142 "1-3"))
        (on-message nil "new-move: 142 1-3"))

