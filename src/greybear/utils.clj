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

(defn position-in-vec
  [[x y]]
  (+ x (* 19 y)))

(defn place-stone
  "Place a stone on a board represented by a list of strings"
  [stones pos color]
  (apply str (assoc (vec stones)
               (position-in-vec (position pos)) color)))
