(ns greybear.pages.game
  (:use [hiccup element page]
        [greybear.model :only [read-game]]))

(defn- stones-to-js
  "Transforms a string of chars into a JSON array
  e.g. \"00120\" becomes: [\"0\", \"0\", \"1\", \"2\", \"0\"]
  "
  [stones]
  (format "[%s]" (apply str (interpose ", " (map str stones)))))

(defn game [session game-id]
  (let [game (read-game game-id)
        count (:count session 0)
        session (assoc session :count (inc count))]
    {:session session
     :body
     (html5
      [:head
       [:title "Grey Bear"]
       (include-js "/js/greybear.js")]
      [:body
       [:div#players "Players: " (game :white) " vs. " (game :black)]
       [:div#caca "Username: " session]
       [:canvas#goBoard]
       (javascript-tag (format "goboard.draw(\"goBoard\", %s, 1, function(x, y) {console.log(x, y)}, 18, 17);"
                               (stones-to-js (game :stones))))])}))
