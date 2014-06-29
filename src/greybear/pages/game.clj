;; Copyright (C) 2013-2014 by Ionuț Arțăriși

;; This file is part of Greybear.

;; Greybear is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; Greybear is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with Greybear.  If not, see <http://www.gnu.org/licenses/>.

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
