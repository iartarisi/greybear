(ns greybear.pages.new-game
  (:require [cemerick.friend :as friend])
  (:use [greybear.pages.helpers :only [get-user-id]]   
        [greybear.pages.layout :only [base-layout]]
        [greybear.pages.games :only [games-partial]]))

(defn new-game [request]
  (friend/authenticated
   (base-layout "Start a new game" request
                [:div.row
                 [:div.col-md-5]
                 [:div.col-md-2
                  [:form {:method "post" :action "/new-game"}
                   [:input {:type "hidden" :name "user-id"
                            :value (get-user-id request)}]
                   [:button.btn.btn-success "New Game"]]]
                 [:div.col-md-5]])))
