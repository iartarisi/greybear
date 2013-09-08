(ns greybear.pages.layout
  (:require [cemerick.friend :as friend])
  (:use [hiccup page element]
        (sandbar stateful-session)))

(defn base-layout
  [subtitle request & content]
  (html5
   [:head
    [:title (str "Grey Bear - " subtitle)]
    (include-css "/bootstrap/css/bootstrap.css")
    (include-css "/style.css")
    (include-js "https://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular.min.js")]
   [:body
    [:div#content.container
     [:div.navbar.navbar-default
      [:div.navbar-header
       [:a.navbar-brand {:href "/"} "Greybear"]

       [:ul.nav.navbar-nav.navbar-right
        [:li
         (if-let [username (:username (friend/current-authentication
                                       (friend/identity request)))]
           [:a {:href "/logout"} "Logout"]
           [:a {:href "/login"} "Login"])]]]]
     content]]))
