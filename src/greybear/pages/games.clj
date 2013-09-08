(ns greybear.pages.games
  (:use [hiccup element page]
        [greybear.pages.layout :only [base-layout]]
        [greybear.model :only [games-list]]))

(defn games [request]
  (base-layout "Games" request
               [:div.row
                [:div.col-md-2]
                [:div.col-md-8
                 [:table#games.table.table-condensed.table-hover
                  [:thead [:tr
                           [:th "#"] [:th "White"] [:th "Black"] [:th "Move"]]]
                  [:tbody
                   (for [game (games-list)]
                     [:tr {:onclick
                           (str "location.href='/games/" (:id game) "';")}
                      [:td (:id game)]
                      [:td (:white_id game)]
                      [:td (:black_id game)]
                      [:td (:moves game)]])]]]
                [:div.col-md-2]]))
