(ns greybear.server
  (:require [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [ring.middleware.session :only [wrap-session]]
        [ring.util.response :only [redirect-after-post]]
        [hiccup core element page]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET POST]]
        [greybear.model :only [read-game verify-user-password]]
        [greybear.pages.login :only [login-page]]
        [greybear.error-pages :only [failed-authentication]]))

(defn stones-to-js
  "Transforms a string of chars into a JSON array
  e.g. \"00120\" becomes: [\"0\", \"0\", \"1\", \"2\", \"0\"]
  "
  [stones]
  (format "[%s]" (apply str (interpose ", " (map str stones)))))

(defn games-page [session game-id]
  (let [game (read-game game-id)
        count (:count session 0)
        session (assoc session :count (inc count))]
    {:session session
     :body
     (html5
      [:head
       [:title "Grey Bear"]
       (include-js "/js/greybear.js")]
      [:body
       [:div#players "Players: " (game :white) " vs. " (game :black)]
       [:div#caca "Username: " session]
       [:canvas#goBoard]
       (javascript-tag (format "goboard.draw(\"goBoard\", %s, 1, function(x, y) {console.log(x, y)}, 18, 17);"
                               (stones-to-js (game :stones))))])}))

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
       (games-page session (Integer. id)))
  (GET "/login" [] login-page)
  (POST "/login" request (login request))
  (GET "/" request (html5 request))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-session)
      (wrap-base-url)))
