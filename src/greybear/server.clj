(ns greybear.server
  (:require [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [ring.adapter.jetty]
        [hiccup core element page]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET]]
        [korma core]
        [greybear.model :only [games players]]))

(defn stones-to-js
  "Transforms a string of chars into a JSON array
  e.g. \"00120\" becomes: [\"0\", \"0\", \"1\", \"2\", \"0\"]
  "
  [stones]
  (format "[%s]" (apply str (interpose ", " (map str stones)))))

(defn games-page [game-id]
  (let [board (first
               (select games
                       (fields :stones
                               [:white.name :white]
                               [:black.name :black])
                       (where {:id game-id})
                       (join [players :white] (= :games.white_id :white.id))
                       (join [players :black] (= :games.black_id :black.id))
                       (limit 1)))]
    (html5
     [:head
      [:title "Grey Bear"]
      (include-js "/goboard.js")]
     [:body
      [:div#players "Players: " (board :white) " vs. " (board :black)]
      [:canvas#goBoard]
      (javascript-tag (format "goboard.draw(\"goBoard\", %s, 1, 18, 17);"
                              (stones-to-js (board :stones))))]
     )))

(defroutes main-routes
  (GET "/games/:id" [id] (games-page (Integer. id)))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))