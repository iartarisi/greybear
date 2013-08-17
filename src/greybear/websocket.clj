(ns greybear.websocket
  (:gen-class)
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler])
  (:require [clojure.data.json :as json])
  (:use [clojure.string :only [split]]
        [greybear.utils :only [parse-int]]
        [greybear.model :only [make-move last-move read-game]]))

(defn- stones-to-js
  "Transforms a list of chars into a JSON array
  e.g. (\0 \0 \0 \1 \1 \2 \0) becomes: [\"0\", \"0\", \"1\", \"2\", \"0\"]
  "
  [stones]
  (format "[%s]" (apply str (interpose ", " stones))))

(defn on-open
  [conn]
  (.send conn "{\"cmd\": \"caca\"}"))


(defn- opponent
  "Return the opponent of a player i.e. 2 for 1 and 1 for 2"
  [player] (if (= player 1) 2 1))

(defn get-playing
  "Returns who is playing the next move:
    0 - it is not the current user's turn
    2 - it is the user's turn and she plays white
    1 - it is the user's turn and she plays black"
  [color user-id game]
  (if (and (not= 0 user-id)
           (case color
             1 (= user-id (:black_id game))
             2 (= user-id (:white_id game))))
    ;; color represents the last move, but we want to draw the next move
    (opponent color)
    0))

(defn refresh
  [conn message]
  (let [game-id (:game_id message)
        user-id (:user_id message)
        game (read-game game-id)
        {:keys [last-player x y]
         :or {last-player 1 x nil y nil}}
        (last-move game-id)]
    (.send conn
           (json/write-str {:cmd "board"
                            :stones (stones-to-js (game :stones))
                            :playing (get-playing last-player user-id game)
                            :last-x x
                            :last-y y}))))

(defn on-message
  [conn mess]
  (let [message (json/read-json mess)]
    (case (:cmd message)
      "init-game" (refresh conn message)
      "make-move" (do
                    (make-move (:game_id message)
                               (str (:x message) "-" (:y message)))
                    (refresh conn message))
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
