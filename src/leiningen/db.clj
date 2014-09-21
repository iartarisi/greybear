(ns leiningen.db
  (:require [clojure.java.jdbc :as jdbc]
            [greybear.model.ddl :as ddl])
  (:use [leiningen.core.main :only [info warn]]
        greybear.model.players))

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

(def subtasks [#'setup #'teardown])

(defn ^{:subtasks subtasks} db
  "Setup database for project"
  [project & keys]
  (if (seq keys)
    (if-let [task (some #(when (= (:name (meta %))
                                  (symbol (first keys)))
                           %)
                        subtasks)]
      (apply task (rest keys))
      (warn "Not a task. See --help"))
    (warn "Please supply a task to run. See --help.")))

