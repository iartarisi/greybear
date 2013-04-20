(ns greybear.server
  (:gen-class)
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
            [org.webbitserver.handler StaticFileHandler]))

(defn -main
  [& args]
  (doto (WebServers/createWebServer 8080)
    (.add (StaticFileHandler. "resources/public"))
    (.start)))