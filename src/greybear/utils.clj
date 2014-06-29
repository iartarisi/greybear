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
