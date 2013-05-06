(ns greybear.server
  (:require [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [ring.middleware.session :only [wrap-session]]
        [ring.util.response :only [redirect-after-post]]
        [hiccup core element page]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET POST]]
        [greybear.model :only [verify-user-password]]
        [greybear.pages errors game login]))


(defn login
  [request]
  (let [creds (get request :params)]
    (if creds
      (let [{:keys [username password]} creds]
        (if (verify-user-password username password)
          ;; TODO redirect to where the user came from
          (-> (redirect-after-post "/")
              (assoc :session {:user username}))
          failed-authentication))
      {:body (html5 "no-params")})))

(defroutes main-routes
  (GET "/games/:id" [id :as {session :session}]
       (game session (Integer. id)))
  (GET "/login" [] login-page)
  (POST "/login" request (login request))
  (GET "/" request (html5 request))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-session)
      (wrap-base-url)))
