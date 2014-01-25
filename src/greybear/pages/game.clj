(ns greybear.pages.game
  (:require [cemerick.friend :as friend])
  (:use [hiccup element page]
        [greybear.model :only [read-game]]
        [greybear.pages.helpers :only [get-user-id]]
        [greybear.pages.layout :only [base-layout]]
        [greybear.pages.errors :only [game-not-found]]))

(defn game [request game-id]
  (let [game (read-game game-id)
        user-id (get-user-id request)]
    (if game
      (base-layout (str "Game #" game-id) request
                   [:div#game-area.row
                    [:div.col-md-10 [:canvas#goBoard]]
                    [:div.col-md-2
                     [:div#turn
                      [:span.label.label-default ""]]
                     [:div#players
                      "Players: " (game :white) " vs. " (game :black)]]]
                   (include-js "/js/goboard.js"
                               "/js/game.js")
                   (javascript-tag
                    (str "window.user_id = " user-id ";")))
      (game-not-found game-id))))
