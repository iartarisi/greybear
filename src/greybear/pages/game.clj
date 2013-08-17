(ns greybear.pages.game
  (:require [cemerick.friend :as friend])
  (:use [hiccup element page]
        [greybear.model :only [read-game]]
        [greybear.pages.layout :only [base-layout]]
        [greybear.pages.errors :only [game-not-found]]))

(defn game [request game-id]
  (let [game (read-game game-id)
        user-id (or (:user-id
                     (friend/current-authentication
                      (friend/identity request)))
                    0)]
    (if game
      {:body
       (base-layout (str "Game #" game-id) request
                    [:div.container-fluid
                     [:div#game-area.row-fluid
                      [:div.span10 [:canvas#goBoard]]
                      [:div.span2
                       [:div#players
                        "Players: " (game :white) " vs. " (game :black)
                        user-id]]]]
                    (include-js "/js/goboard.js"
                                "/js/game.js")
                    (javascript-tag
                     (str "window.user_id = " user-id ";")))}
      (game-not-found game-id))))
