(ns greybear.server
  (:require [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [ring.adapter.jetty]
        [hiccup core element page]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET]]
        [greybear.model :only [read-game]]))

(defn stones-to-js
  "Transforms a string of chars into a JSON array
  e.g. \"00120\" becomes: [\"0\", \"0\", \"1\", \"2\", \"0\"]
  "
  [stones]
  (format "[%s]" (apply str (interpose ", " (map str stones)))))

(defn games-page [game-id]
  (let [game (read-game game-id)]
    (html5
     [:head
      [:title "Grey Bear"]
      (include-js "/js/greybear.js")]
     [:body
      [:div#players "Players: " (game :white) " vs. " (game :black)]
      [:canvas#goBoard]
      (javascript-tag (format "goboard.draw(\"goBoard\", %s, 1, function(x, y) {console.log(x, y)}, 18, 17);"
                              (stones-to-js (game :stones))))])))

(defroutes main-routes
  (GET "/games/:id" [id] (games-page (Integer. id)))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))