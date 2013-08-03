(ns greybear.websocket
  (:gen-class)
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler])
  (:require [clojure.data.json :as json])
  (:use [clojure.string :only [split]]
        [greybear.utils :only [parse-int]]
        [greybear.model :only [make-move read-game]]))

(defn- stones-to-js
  "Transforms a list of chars into a JSON array
  e.g. (\0 \0 \0 \1 \1 \2 \0) becomes: [\"0\", \"0\", \"1\", \"2\", \"0\"]
  "
  [stones]
  (format "[%s]" (apply str (interpose ", " stones))))

(defn on-open
  [conn]
  (.send conn "{\"cmd\": \"caca\"}"))

(defn refresh
  [conn message]
  (let [game-id (parse-int message)
        game (read-game game-id)]
    (.send conn
           (json/write-str {:cmd "board"
                            :stones (stones-to-js (game :stones))
                            :playing 1
                            :last-x 18
                            :last-y 17}))))

(defn on-message
  [conn mess]
  (let [[cmd message] (split mess #": " 2)]
    (case cmd
      "init-game" (refresh conn message)
      "make-move" (let [[game move] (split message #"\s")]
                    (make-move (parse-int game) move)
                    (refresh conn game))
      (.send conn (str "{\"cmd\": \"YO! " message "\"}")))))

(defn -main
  [& args]
  (doto (WebServers/createWebServer 8080)
    (.add "/websocket"
          (proxy [WebSocketHandler] []
            (onOpen [conn] (on-open conn))
            (onClose [conn] nil)
            (onMessage [conn mess] (on-message conn mess))))
    (.start)))
