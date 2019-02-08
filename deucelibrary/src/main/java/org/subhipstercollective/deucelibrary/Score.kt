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
    var winner = Player.NONE
        private set

    private fun updateWinner() {
        if (scoreP1 >= winMinimum && (scoreP1 - scoreP2) >= winMargin)
            winner = Player.PLAYER1
        else if (scoreP2 >= winMinimum && (scoreP2 - scoreP1) >= winMargin)
            winner = Player.PLAYER2
        else
            winner = Player.NONE
    }

    fun score(player: Player = Player.NONE): Player {
        when (player) {
            Player.PLAYER1 -> ++scoreP1
            Player.PLAYER2 -> ++scoreP2
            else -> {
            }
        }
        return winner
    }

    fun descore(player: Player = Player.NONE): Player {
        when (player) {
            Player.PLAYER1 -> --scoreP1
            Player.PLAYER2 -> --scoreP2
            else -> {
            }
        }
        return winner
    }

    fun getScore(player: Player) = when (player) {
        Player.NONE -> -1
        Player.PLAYER1 -> scoreP1
        Player.PLAYER2 -> scoreP2
    }
}
