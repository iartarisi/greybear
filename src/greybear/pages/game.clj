(ns greybear.pages.game
  (:use [hiccup element page]
        [greybear.model :only [read-game]]
        [greybear.pages.layout :only [base-layout]]
        [greybear.pages.errors :only [game-not-found]]))

(defn- stones-to-js
  "Transforms a list of chars into a JSON array
  e.g. (\0 \0 \0 \1 \1 \2 \0) becomes: [\"0\", \"0\", \"1\", \"2\", \"0\"]
  "
  [stones]
  (format "[%s]" (apply str (interpose ", " stones))))

(defn js-draw
  [stones playing last-x last-y]
  (javascript-tag
   (format "goboard.draw(\"goBoard\", %s, %s, greybear.draw_callback, %s, %s)"
           (stones-to-js stones) playing last-x last-y)))

(defn game [session game-id]
  (let [game (read-game game-id)
        count (:count session 0)
        session (assoc session :count (inc count))]
    (if game
      {:session session
       :body
       (base-layout (str "Game #" game-id)
                    [:div.container-fluid
                     [:div.row-fluid
                      [:div.span10 [:canvas#goBoard]]
                      [:div.span2
                       [:div#players
                        "Players: " (game :white) " vs. " (game :black)]
                       [:div#caca "Username: " session]]]]
                    (include-js "/js/greybear.js")
                    (js-draw (game :stones) 1 18 17))}
      (game-not-found game-id))))

