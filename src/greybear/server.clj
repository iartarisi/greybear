(ns greybear.server
  (:gen-class)
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
            [org.webbitserver.handler StaticFileHandler])
  (:require [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [ring.adapter.jetty]
        [hiccup core element page]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET]]
        [korma core]
        [greybear.model :only [games]]))

(defn stones-to-js
  "Transforms a string of chars into a JSON array
  e.g. \"00120\" becomes: [\"0\", \"0\", \"1\", \"2\", \"0\"]
  "
  [stones]
  (format "[%s]" (apply str (interpose ", " (map str stones)))))

(defn index-page []
  (let [board (first (select games
                              (where {:id 2})
                              (limit 1)))]
    (html5
     [:head
      [:title "Grey Bear"]
      (include-js "goboard.js")]
     [:body
      [:canvas#goBoard]
      (javascript-tag (format "goboard.goboard.draw(\"goBoard\", %s, 18, 17);"
                              (stones-to-js (board :stones))))]
     )))

(defroutes main-routes
  (GET "/" [] (index-page))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))

(defn -main
  [& args]
  (doto (WebServers/createWebServer 8080)
    (.add (StaticFileHandler. "resources/public"))
    (.start)))