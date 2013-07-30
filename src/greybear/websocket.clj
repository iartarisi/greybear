(ns greybear.websocket
  (:gen-class)
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler]))

(defn on-open
  [conn]
  (.send conn "{\"oi\": \"caca\"}"))

(defn on-message
  [conn mess]
  (println mess))

(defn -main
  [& args]
  (doto (WebServers/createWebServer 8080)
    (.add "/websocket"
          (proxy [WebSocketHandler] []
            (onOpen [conn] (on-open conn))
            (onMessage [conn mess] (on-message conn mess))))
    (.start)))
