(ns greybear
  (:require [goog.events]
            [goog.net.WebSocket]
            [goboard]))

(defn on-open
  [event]
  (.log js/console "Opened!" event))

(defn on-message
  [event]
  (.log js/console "Got Messsage: " event))

(def ws (goog.net.WebSocket.))

(doto ws
  (goog.events/listen (.-OPENED goog.net.WebSocket/EventType) on-open)
  (goog.events/listen (.-MESSAGE goog.net.WebSocket/EventType) on-message)
  (.open "ws://localhost:8080/websocket"))

(defn draw-callback
  [x y]
  (.log js/console x y)
  (.send ws (str "new-move: " "1 " x "-" y)))
