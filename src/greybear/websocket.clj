(ns greybear.websocket
  (:gen-class)
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler])
  (:use [clojure.string :only [split]]
        [greybear.utils :only [parse-int]]
        [greybear.model :only [new-move]]))

(defn on-open
  [conn]
  (.send conn "{\"oi\": \"caca\"}"))

(defn refresh
  [conn]
  (.send conn "{\"message\": \"BOOO\"}"))

(defn on-message
  [conn mess]
  (let [[cmd message] (split mess #": " 2)]
    (case cmd
      "new-move" (do
                   (let [[game move] (split message #"\s")]
                     (new-move (parse-int game) move))
                   (refresh conn)))))

(defn -main
  [& args]
  (doto (WebServers/createWebServer 8080)
    (.add "/websocket"
          (proxy [WebSocketHandler] []
            (onOpen [conn] (on-open conn))
            (onMessage [conn mess] (on-message conn mess))))
    (.start)))
