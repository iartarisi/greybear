(ns greybear.pages.layout
  (:use [hiccup page element]
        (sandbar stateful-session)))

(defn base-layout
  [subtitle & content]
  (html5
   [:head
    [:title (str "Grey Bear - " subtitle)]
    (include-css "/bootstrap/css/bootstrap.css")]
   [:body
    [:div.navbar
     [:div.navbar-inner
      [:a.brand {:href "/"} "Greybear"]
      [:div#user-links
       [:ul.nav
        (let [username (session-get :username false)]
          (if username
            [:li [:a {:href (str "/users/" username)} username]]
            [:li [:a {:href "/login"} "Login"]]))]]]]
    [:div#content content]]))
