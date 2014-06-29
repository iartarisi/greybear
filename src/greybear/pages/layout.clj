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

(ns greybear.pages.layout
  (:require [cemerick.friend :as friend])
  (:use [hiccup page element]))

(defn base-layout
  [subtitle request & content]
  (html5
   [:head
    [:title (str "Grey Bear - " subtitle)]
    (include-css "/bootstrap/css/bootstrap.css")
    (include-css "/style.css")
    (include-js "/js/jquery/jquery-2.0.3.js")
    (include-js "/js/jquery/jquery.cookie.js")
    (include-js "https://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular.min.js")]
   [:body
    [:div#content.container
     [:div.navbar.navbar-default
      [:div.navbar-header
       [:a.navbar-brand {:href "/"} "Greybear"]
       [:ul.nav.navbar-nav.navbar-left
        [:li
         (if (friend/identity request)
           [:a {:href "/new-game"} "New Game"])]
        [:li
         [:a {:href "/games"} "Games"]]]
       [:ul.nav.navbar-nav.navbar-right
        [:li
         (if-let [username (:username (friend/current-authentication
                                       (friend/identity request)))]
           [:a {:href "/logout"} "Logout"]
           [:a {:href "/login"} "Login"])]]]]
     content]]))
