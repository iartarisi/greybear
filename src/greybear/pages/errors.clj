(ns greybear.pages.errors
  (:use [hiccup.page :only [html5]]))

(def failed-authentication
  {:status 401
   :body (html5 "Failed authentication.")})
