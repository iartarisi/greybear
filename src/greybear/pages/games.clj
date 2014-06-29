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

(ns greybear.pages.games
  (:use [hiccup element page]
        [greybear.pages.layout :only [base-layout]]
        [greybear.model :only [games-list]]))

(defn games-partial []
  [:div.row
   [:div.col-md-2]
   [:div.col-md-8
    [:table#games.table.table-condensed.table-hover
     [:thead [:tr
              [:th "#"] [:th "White"] [:th "Black"] [:th "Move"]]]
     [:tbody
      (for [game (games-list)]
        [:tr {:onclick
              (str "location.href='/games/" (:id game) "';")}
         [:td (:id game)]
         [:td (:white_id game)]
         [:td (:black_id game)]
         [:td (:moves game)]])]]]
   [:div.col-md-2]])

(defn games [request]
  (base-layout "Games" request
               (games-partial)))
