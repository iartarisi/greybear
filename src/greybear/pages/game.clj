(ns greybear.pages.game
  (:require [cemerick.friend :as friend])
  (:use [hiccup element page]
        [greybear.model :only [read-game whos-turn]]
        [greybear.pages.layout :only [base-layout]]
        [greybear.pages.errors :only [game-not-found]]))

(defn game [request game-id]
  (let [game (read-game game-id)
        user-id (or (:user-id
                     (friend/current-authentication
                      (friend/identity request)))
                    0)]
    (if game
      (base-layout (str "Game #" game-id) request
                   [:div#game-area.row
                    [:div.col-md-10 [:canvas#goBoard]]
                    [:div.col-md-2
                     [:div#turn
                      (case (whos-turn game-id user-id)
                        :me [:span.label.label-danger "Your turn!"]
                        :opponent [:span.label.label-warning "Opponent's turn."]
                        [:span.label.label-default "Watching game."])]
                     [:div#players
                      "Players: " (game :white) " vs. " (game :black)]]]
                   (include-js "/js/goboard.js"
                               "/js/game.js")
                   (javascript-tag
                    (str "window.user_id = " user-id ";")))
      (game-not-found game-id))))
