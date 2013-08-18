(ns greybear.pages.games
  (:use [hiccup element page]
        [greybear.pages.layout :only [base-layout]]
        [greybear.model :only [games-list]]))

(defn games [request]
  (base-layout "Games" request
               [:div.row-fluid
                [:div.span2]
                [:div.span8
                 [:table.table.table-condensed.table-hover
                  [:thead [:tr
                           [:th "#"] [:th "White"] [:th "Black"] [:th "Move"]]]
                  [:tbody
                   (for [game (games-list)]
                     [:tr
                      [:td (:id game)]
                      [:td (:white_id game)]
                      [:td (:black_id game)]
                      [:td (:moves game)]])]]]
                [:div.span2]]))
