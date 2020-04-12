/*
 * Copyright (C) 2020 Jeffrey Thomas Piercy
 *
 * This file is part of Deuce-Android.
 *
 * Deuce-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deuce-Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Deuce-Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.subhipstercollective.deucelibrary

class Score(winMinimum: Int, winMargin: Int) {
    var winMinimum: Int = winMinimum
        set(value) {
            field = value
            updateWinner()
        }
    var winMargin: Int = winMargin
        set(value) {
            field = value
            updateWinner()
        }

    var scoreP1 = 0
        set(value) {
            field = value
            updateWinner()
        }

    var scoreP2 = 0
        set(value) {
            field = value
            updateWinner()
        }

    var winner = Winner.NONE
        private set

    private fun updateWinner() {
        winner = when {
            scoreP1 >= winMinimum && (scoreP1 - scoreP2) >= winMargin -> Winner.TEAM1
            scoreP2 >= winMinimum && (scoreP2 - scoreP1) >= winMargin -> Winner.TEAM2
            else -> Winner.NONE
        }
    }

    fun score(team: Team): Winner {
        when (team) {
            Team.TEAM1 -> ++scoreP1
            Team.TEAM2 -> ++scoreP2
        }
        return winner
    }

    fun getScore(team: Team) = when (team) {
        Team.TEAM1 -> scoreP1
        Team.TEAM2 -> scoreP2
    }
}
