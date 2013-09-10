(ns greybear.pages.new-game
  (:require [cemerick.friend :as friend])
  (:use [greybear.pages.layout :only [base-layout]]))

(defn new-game [request]
  (friend/authenticated
   (base-layout "Start a new game" request
                "OOO")))
