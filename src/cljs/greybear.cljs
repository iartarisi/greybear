(ns greybear
  (:require [goog.events]
            [goog.net.WebSocket]
            [goboard]))

(defn on-open
  [event]
  (.log js/console "Opened!" event))

(def ws (goog.net.WebSocket.))

(doto ws
  (goog.events/listen (.-OPENED goog.net.WebSocket/EventType) on-open)
  (.open "ws://localhost:8080/websocket"))

(defn draw-callback
  [x y]
  (.log js/console x y)
  (.send ws (str x "-" y)))
