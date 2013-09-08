(ns greybear.server
  (:require [cemerick.friend :as friend]
            [cemerick.friend [credentials :as creds]
                             [workflows :as workflows]]
            [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [korma.core]
        [hiccup.page :only [html5]]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET POST]]
        [ring.middleware.session.cookie :only [cookie-store]]
        [ring.middleware.session.store :only [read-session]]
        [ring.util.response :as resp]
        [greybear.model :only [players]]
        [greybear.pages games game login]))

(defroutes main-routes
  (GET "/games" request (games request))
  (GET ["/games/:id", :id #"[0-9]+"] [id :as request]
       (game request (Integer. id)))
  (GET "/login" request (login-get request))
  (GET "/logout" request
       (friend/logout* (resp/redirect (str (:context request) "/"))))
  (GET "/requires-authentication" request
       (friend/authenticated "Thanks for authenticating!"))
  ;; (GET "/role-user" request
  ;;      (friend/authorize #{::users/user} "You're a user"))
  (GET "/" request (resp/redirect "/games"))
  (route/resources "/")
  (route/not-found "Page not found"))

(defn load-credentials
  [user]
  (first (select players
                 (fields [:name :username]
                         [:id :user-id]
                         :password)
                 (where {:name user}))))

(def ^:const session-key "a 16-byte secret")
(def grey-session-store
  (cookie-store {:key session-key}))

(defn get-identity
  "Read the identity information from an encrypted ring session cookie"
  [cookie]
  (friend/current-authentication
   (friend/identity
    {:session (read-session grey-session-store cookie)})))

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
         :workflows [(workflows/interactive-form)]})
       {:session {:store grey-session-store}})
      (wrap-base-url)))
