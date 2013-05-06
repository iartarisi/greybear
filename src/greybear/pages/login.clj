(ns greybear.templates.login
  (:use [hiccup page form]))

(def login-page
  (html5
   [:body
    [:div#login
     (form-to [:post "/login"]
              [:div#username (text-field "username")]
              [:div#password (password-field "password")]
              (submit-button "login"))]]))
