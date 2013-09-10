(ns greybear.pages.helpers
  (:require [cemerick.friend :as friend]))

(defn get-user-id
  "Return a user-id int or 0 if the user is anonymous"
  [request]
  (or (:user-id
       (friend/current-authentication
        (friend/identity request)))
      0))
