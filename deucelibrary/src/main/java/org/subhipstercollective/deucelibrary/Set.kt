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
class Set(val winMinimum:     Int, val winMargin:     Int,
          val winMinimumGame: Int, val winMarginGame: Int)
{
    var games = ArrayList<Game>()
    private var mScore = Game(winMinimum, winMargin)

    init
    {
        games.add(Game(winMinimumGame, winMarginGame))
    }

    fun score(player: Player = Player.NONE) = mScore.score(player)

    fun addNewGame() = games.add(Game(winMinimumGame, winMarginGame))

    fun getScoreStrs() = ScoreStrings(mScore.scoreP1.toString(), mScore.scoreP2.toString())

    val currentGame get() = games.last()

    /*val winMinimum get() = mScore.winMinimum
    val winMargin get() = mScore.winMargin
    val winMinimumGame get() = games[0].winMinimum
    val winMarginGame get() = games[0].winMargin*/
    val scoreP1 get() = mScore.scoreP1
    val scoreP2 get() = mScore.scoreP2
    val winner get() = mScore.winner
    val gameNumber get() = games.size
}
