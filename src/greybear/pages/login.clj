(ns greybear.pages.login
  (:use [hiccup element form]
        [greybear.pages.layout :only [base-layout]]))

(defn login-get [request]
  (base-layout "Login" request
               [:div#login
                (form-to [:post "/login"]
                         [:div#username (text-field "username")]
                         [:div#password (password-field "password")]
                         (submit-button "login"))]))
