(ns greybear.server
  (:require [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [sandbar.stateful-session]
        [hiccup.page :only [html5]]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET POST]]
        [greybear.pages game login]))


(defroutes main-routes
  (GET ["/games/:id", :id #"[0-9]+"] [id :as {session :session}]
       (game session (Integer. id)))
  (GET "/login" [] (login-get))
  (POST "/login" request (login-post request))
  (GET "/" request (html5 request))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      wrap-stateful-session
      (wrap-base-url)))
