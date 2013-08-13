(ns greybear.server
  (:require [cemerick.friend :as friend]
            [cemerick.friend [credentials :as creds]
                             [workflows :as workflows]]
            [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [sandbar.stateful-session]
        [korma.core]
        [hiccup.page :only [html5]]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET POST]]
        [ring.util.response :as resp]
        [greybear.model :only [players]]
        [greybear.pages game login]))

(defroutes main-routes
  (GET ["/games/:id", :id #"[0-9]+"] [id :as request]
       (game request (Integer. id)))
  (GET "/login" request (login-get))
  (GET "/logout" request
       (friend/logout* (resp/redirect (str (:context request) "/"))))
  (GET "/requires-authentication" request
       (friend/authenticated "Thanks for authenticating!"))
  ;; (GET "/role-user" request
  ;;      (friend/authorize #{::users/user} "You're a user"))
  (GET "/" request (html5 request))
  (route/resources "/")
  (route/not-found "Page not found"))

(defn load-credentials
  [user]
  (first (select players
                 (fields [:name :username]
                         [:id :user-id]
                         :password)
                 (where {:name user}))))

(def app
  (-> (handler/site
       (friend/authenticate 
        main-routes
        {:allow-anon? true
         :login-uri "/login"
         :default-landing-uri "/"
         :unauthorized-handler #(-> (html5
                                     [:h2 "You're not authorized" (:uri %)])
                                    resp/response
                                    (resp/status 401))
         :credential-fn #(creds/bcrypt-credential-fn load-credentials %)
         :workflows [(workflows/interactive-form)]}))
      wrap-stateful-session
      (wrap-base-url)))
