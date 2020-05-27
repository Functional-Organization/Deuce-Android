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

package net.mqduck.deuce.common

/**
 * Generic scorekeeping class for Games, Sets and Matches.
 *
 * @constructor
 * Creates a new 0-0 score.
 *
 * @param winMinimum The minimum number of points necessary for one team to win.
 * @param winMargin The minimum point margin between the winning and losing team to count as a win.
 */
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

    var scoreTeam1 = 0
        set(value) {
            field = value
            updateWinner()
        }

    var scoreTeam2 = 0
        set(value) {
            field = value
            updateWinner()
        }

    /**
     *  The winning team, if any.
     */
    var winner = Winner.NONE
        internal set

    private fun updateWinner() {
        winner = when {
            scoreTeam1 >= winMinimum && (scoreTeam1 - scoreTeam2) >= winMargin -> Winner.TEAM1
            scoreTeam2 >= winMinimum && (scoreTeam2 - scoreTeam1) >= winMargin -> Winner.TEAM2
            else -> Winner.NONE
        }
    }

    /**
     * Adds a point to the score of a team.
     *
     * @param team The scoring team.
     * @return The winning team, if any.
     */
    fun score(team: Team): Winner {
        when (team) {
            Team.TEAM1 -> ++scoreTeam1
            Team.TEAM2 -> ++scoreTeam2
        }
        return winner
    }

    /**
     * Returns the current score of a team.
     *
     * @param team The team to get the score of.
     */
    fun getScore(team: Team) = when (team) {
        Team.TEAM1 -> scoreTeam1
        Team.TEAM2 -> scoreTeam2
    }
}
