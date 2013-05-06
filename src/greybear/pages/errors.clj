(ns greybear.pages.errors
  (:use [hiccup.page :only [html5]]))

(def failed-authentication
  {:status 401
   :body (html5 "Failed authentication.")})

(def game-not-found
  {:status 404
   :body (html5 (format "Game not found: %s." game-id))})
