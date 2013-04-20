(ns greybear.server
  (:gen-class)
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
            [org.webbitserver.handler StaticFileHandler])
  (:require [compojure.route :as route]
            [compojure.handler :as handler])
  (:use [ring.adapter.jetty]
        [hiccup.core]
        [hiccup.middleware :only [wrap-base-url]]
        [compojure.core :only [defroutes GET]]))


(defn index-page []
  (html
   [:head [:title "CACA"]]))

(defroutes main-routes
  (GET "/" [] (index-page))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))

(defn -main
  [& args]
  (doto (WebServers/createWebServer 8080)
    (.add (StaticFileHandler. "resources/public"))
    (.start)))