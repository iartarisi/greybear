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
  (:use [ring.util.response :as resp]
        [greybear.model :only [create-game]]
        [greybear.utils :only [parse-int]]
        [greybear.pages.helpers :only [get-user-id]]   
        [greybear.pages.layout :only [base-layout]]
        [greybear.pages.games :only [games-partial]]))

(defn new-game [request]
  (friend/authenticated
   (base-layout "Start a new game" request
                [:div.row
                 [:div.col-md-5]
                 [:div.col-md-2
                  [:form {:method "post" :action "/new-game"}
                   [:input {:type "hidden" :name "my-id"
                            :value (get-user-id request)}]
                   [:input {:type "text" :name "opponent-id"}]
                   [:button.btn.btn-success "New Game"]]]
                 [:div.col-md-5]]
                [:div.row
                 (games-partial)])))

(defn new-game-post [params]
  (friend/authenticated
   (resp/redirect
    (str "/games/" (create-game
                    (parse-int (params :my-id))
                    (parse-int (params :opponent-id)))))))
