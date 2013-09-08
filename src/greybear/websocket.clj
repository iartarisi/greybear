(ns greybear.websocket
  (:gen-class)
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler])
  (:require [clojure.data.json :as json])
  (:use [clojure.string :only [split]]
        [greybear.utils :only [parse-int]]
        [greybear.model :only [last-move make-move get-playing read-game
                               user-turn?]]
        [greybear.server :only [get-identity]]))

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
  (let [game-id (:game_id message)
        user-id (:user_id message)
        game (read-game game-id)
        {:keys [x y] :or {x nil, y nil}} (last-move game-id)]
    (.send conn
           (json/write-str {:cmd "board"
                            :stones (stones-to-js (game :stones))
                            :playing (get-playing game-id user-id)
                            :last-x x
                            :last-y y}))))

(defn make-move*
  [conn message]
  (let [user-id (:user-id (get-identity (:cookie message)))
        game-id (:game_id message)]
    (when (user-turn? game-id user-id)
      (make-move game-id (str (:x message) "-" (:y message)))
      (refresh conn message))))

(defn on-message
  [conn mess]
  (let [message (json/read-json mess)]
    (case (:cmd message)
      "init-game" (refresh conn message)
      "make-move" (make-move* conn message)
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
