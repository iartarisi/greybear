(ns greybear.pages.layout
  (:use [hiccup page element]))

(defn base-layout
  [subtitle & content]
  (html5
   [:head
    [:title (str "Grey Bear - " subtitle)]
   [:body
    [:div#content content]]))
