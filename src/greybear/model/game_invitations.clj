;; Copyright (C) 2014 by Ionuț Arțăriși

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

(ns greybear.model.game-invitations
  (:use [korma core]
        [greybear.model.ddl :only [game-invitations]]
        [greybear.model.players :only [get-player]]))


(defn create-game-invitation
  "Start looking for a new game"
  [white-id black-id]
  (when (and (not= white-id black-id)
             (get-player black-id)
             (get-player white-id))
    (insert game-invitations
            (values {:black_id black-id
                     :white_id white-id}))))
