(ns greybear.core
  (:use [korma.db]))

(defdb db (postgres {:db "greybear"
                     :user "greybear"
                     :password "greybear"}))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
