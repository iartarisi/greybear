(ns greybear.pages.game
  (:use [hiccup element page]
        [greybear.model :only [read-game]]
        [greybear.pages.layout :only [base-layout]]
        [greybear.pages.errors :only [game-not-found]]))

(defn game [session game-id]
  (let [game (read-game game-id)
        count (:count session 0)
        session (assoc session :count (inc count))]
    (if game
      {:session session
       :body
       (base-layout (str "Game #" game-id)
                    [:div.container-fluid
                     [:div#game-area.row-fluid
                      [:div.span10 [:canvas#goBoard]]
                      [:div.span2
                       [:div#players
                        "Players: " (game :white) " vs. " (game :black)]]]]
                    (include-js "/js/goboard.js"
                                "/js/game.js"))}
      (game-not-found game-id))))
