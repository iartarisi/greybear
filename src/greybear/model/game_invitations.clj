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
        [greybear.model.ddl :only [game-invitations players]]
        [greybear.model.players :only [get-player]]))


(defn create-game-invitation
  "Start looking for a new game"
  [host-id guest-id]
  (when (and (not= host-id guest-id)
             (get-player guest-id)
             (get-player host-id))
    (insert game-invitations
            (values {:host_id host-id
                     :guest_id guest-id}))))

(defn invited-by
  "Return a list of the players invited by :player:"
  [player-id]
  (select players
          (join :inner game-invitations (= :game_invitations.guest_id :id))
          (where (= :game_invitations.host_id player-id))))
