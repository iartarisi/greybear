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

(ns greybear.pages.new-game
  (:require [cemerick.friend :as friend])
  (:use [hiccup form]
        [ring.util.response :as resp]
        [greybear.model.game-invitations]
        [greybear.utils :only [parse-int]]
        [greybear.pages.helpers :only [get-user-id]]   
        [greybear.pages.layout :only [base-layout]]))


(defn players-list
  "Outputs a list of players with some information about each"
  [players]
  [:div.row
   [:table.table.table-striped
    [:thead
     [:tr
      [:th "#"]
      [:th "Username"]]
     ]
    [:tbody
     (for [[idx player] (map-indexed vector players)]
       [:tr
        [:td (+ idx 1)]
        [:td (:name player)]])]]])

(defn new-game [request]
  (friend/authenticated
   (base-layout "Start a new game" request
                [:div.row
                 [:div.col-md-3]
                 [:div.col-md-6
                  (form-to [:post "/new-game"]
                           [:input {:type "hidden" :name "my-id"
                                     :value (get-user-id request)}]
                            (label "opponent-id" "Invite a player:")
                            [:input {:type "text" :name "opponent-id"}]
                            [:button.btn.btn-success "New Game"])
                  ]
                 [:div.col-md-3]]
                [:div.row
                 [:div.col-md-1]
                 [:div.col-md-10
                  [:p "Pending invitations: "]
                  (players-list (invited-by (get-user-id request)))]
                 [:div.col-md-1]])))

(defn new-game-post [params]
  (friend/authenticated
   (resp/redirect
    (str "/games/" (create-game-invitation
                    (parse-int (params :my-id))
                    (parse-int (params :opponent-id)))))))
