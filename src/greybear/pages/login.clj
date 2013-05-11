(ns greybear.pages.login
  (:use [hiccup element form]
        [sandbar.stateful-session]
        [ring.util.response :only [redirect-after-post]]
        [greybear.pages.errors :only [failed-authentication]]
        [greybear.pages.layout :only [base-layout]]
        [greybear.model :only [verify-user-password]]))

(defn login-get []
  (base-layout "Login"
               [:div#login
                (form-to [:post "/login"]
                         [:div#username (text-field "username")]
                         [:div#password (password-field "password")]
                         (submit-button "login"))]))

(defn login-post
  [request]
  (let [creds (get request :params)]
    (if creds
      (let [{:keys [username password]} creds]
        (if (verify-user-password username password)
          ;; TODO redirect to where the user came from
          (do (session-put! :username username)
              (redirect-after-post "/"))
          failed-authentication))
      (base-layout "no-params"))))
