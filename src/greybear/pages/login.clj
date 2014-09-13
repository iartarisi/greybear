;; Copyright (C) 2013-2014 by Ionuț Arțăriși

;; This file is part of Greybear.

;; Greybear is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; Greybear is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with Greybear.  If not, see <http://www.gnu.org/licenses/>.

(ns greybear.pages.login
  (:use [hiccup element form]
        [greybear.pages.layout :only [base-layout]]))

(defn login-get [request login_failed]
  (base-layout
   "Login" request
   [:div.row
    [:div.col-md-4]
    [:div.col-md-4
     [:div.panel.panel-default
      [:div.panel-heading "Login"]
      [:div.panel-body
       (if (= login_failed "Y")
         [:div.alert.alert-danger {:role "alert"}
          "Login failed. "
          "The username or password you provided are incorrect."])

       [:div#login
        (form-to
         [:post "/login"]
         [:div.row
          [:div.col-md-2]
          [:div.col-md-8
           [:div.form-group
            (label "input-username" "Username:")
            (text-field {:id "input-username"
                         :class "form-control"
                         :placeholder "username"}
                        "username")]
           [:div.form-group
            (label "input-password" "Password:")
            (password-field {:id "input-password"
                             :class "form-control"
                             :placeholder "password"}
                            "password")]]
          [:div.col-md-2]]
         [:div.row
          [:div.col-md-4]
          [:div.col-md-4
           [:div.form-group (submit-button {:class "btn btn-primary"}
                                           "Login")]]
          [:div.col-md-4]])]]]]
    [:div.col-md-4]]))
