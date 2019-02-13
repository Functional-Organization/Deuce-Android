/*
 * Copyright 2017 Jeffrey Thomas Piercy
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

/**
 * Created by mqduck on 10/31/17.
 */
class Match(val winMinimum: Int,
            val winMinimumSet: Int, val winMarginSet: Int,
            val winMinimumGame: Int, val winMarginGame: Int,
            val startingServer: Player,
            private val controller: ControllerMain) {
    var sets = ArrayList<Set>()
    private var mScore = Score(winMinimum, 1)

    init {
        sets.add(Set(winMinimumSet, winMarginSet, winMinimumGame, winMarginGame, controller))
    }

    fun score(player: Player = Player.NONE) = mScore.score(player)

    val currentSet get() = sets.last()
    val currentGame get() = currentSet.currentGame

    fun addNewSet() = sets.add(Set(winMinimumSet, winMarginSet, winMinimumGame, winMarginGame, controller))
    //fun addNewGame() = currentSet.addNewGame()

    fun getScore(player: Player) = mScore.getScore(player)

    fun getScoreStrs() = ScoreStrings(mScore.scoreP1.toString(), mScore.scoreP2.toString())

    /*val winMinimum get() = mScore.winMinimum
    val winMargin get() = mScore.winMargin
    val winMinimumSet get() = sets[0].winMinimum
    val winMarginSet get() = sets[0].winMargin
    val winMinimumGame get() = sets[0].games[0].winMinimum
    val winMarginGame get() = sets[0].games[0].winMargin*/
    val scoreP1 get() = mScore.scoreP1
    val scoreP2 get() = mScore.scoreP2
    val winner get() = mScore.winner
    val setNumber get() = sets.size
    val gameNumber get() = currentSet.gameNumber
}