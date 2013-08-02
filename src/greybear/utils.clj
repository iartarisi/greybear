(ns greybear.utils
  (:use [clojure.string :only [split]]))

(defn parse-int
  [int]
  (Integer/parseInt int))

(defn position
  "Transform a string position into a tuple"
  [position]
  (let [[x y] (map parse-int (split position #"-"))]
    (assert (< x 19))
    (assert (< y 19))
    [x y]))

