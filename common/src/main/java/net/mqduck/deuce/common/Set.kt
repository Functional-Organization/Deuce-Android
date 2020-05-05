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

class Set(
    private val winMinimum: Int, winMargin: Int,
    private val winMinimumGame: Int, private val winMarginGame: Int,
    private val winMinimumGameTiebreak: Int, private val winMarginGameTiebreak: Int,
    private val overtimeRule: OvertimeRule,
    private val match: Match
) {
    var games = ArrayList<Game>()
    private var mScore = Score(winMinimum, winMargin)

    init {
        games.add(Game(winMinimumGame, winMarginGame, match))
    }

    fun score(team: Team) = mScore.score(team)

    fun addNewGame() {
        if (overtimeRule == OvertimeRule.TIEBREAK && mScore.scoreTeam1 == winMinimum && mScore.scoreTeam2 == winMinimum) {
            mScore.winMargin = 1
            games.add(Game(winMinimumGameTiebreak, winMarginGameTiebreak, match, true))
        } else {
            games.add(Game(winMinimumGame, winMarginGame, match, false))
        }
    }

    val currentGame get() = games.last()

    val scoreP1 get() = mScore.scoreTeam1
    val scoreP2 get() = mScore.scoreTeam2
    val winner get() = mScore.winner
}
