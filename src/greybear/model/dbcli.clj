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

(ns greybear.model.dbcli
  (:require [clojure.java.jdbc :as jdbc]
            [greybear.model.ddl :as ddl])
  (:use greybear.model.players))

(defn setup
  "Create database tables and two users"
  []
  (ddl/setup ddl/psql)
  (create-player "swede" "death")
  (create-player "colin" "guns"))

(defn teardown
  "Drop all database tables"
  []
  (ddl/teardown ddl/psql))
