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
class Game(winMinimum: Int, winMargin: Int)
{
    var winMinimum: Int = winMinimum
        set(value)
        {
            field = value
            score()
        }
    var winMargin: Int = winMargin
        set(value)
        {
            field = value
            score()
        }

    var scoreP1 = 0
        set(value)
        {
            field = value
            score()
        }
    var scoreP2 = 0
        set(value)
        {
            field = value
            score()
        }
    var winner = Player.NONE
        private set

    fun score(player: Player = Player.NONE): Player
    {
        when(player)
        {
            Player.PLAYER1 ->
            {
                ++scoreP1
                if (scoreP1 >= winMinimum && (scoreP1 - scoreP2) >= winMargin)
                    winner = Player.PLAYER1
            }
            Player.PLAYER2 ->
            {
                ++scoreP2
                if (scoreP2 >= winMinimum && (scoreP2 - scoreP1) >= winMargin)
                    winner = Player.PLAYER2
            }
            else ->
            {
                if (scoreP1 >= winMinimum && (scoreP1 - scoreP2) >= winMargin)
                    winner = Player.PLAYER1
                else if (scoreP2 > winMinimum && (scoreP2 - scoreP1) >= winMargin)
                    winner = Player.PLAYER2
                else
                    winner = Player.NONE
            }
        }
        return winner
    }

    private fun mapScore(score: Int) = when(score)
    {
        0    -> "Love"
        1    -> "15"
        2    -> "30"
        3    -> "40"
        else -> "ERROR"
    }

    fun getScoreStrs() = when
    {
        winner == Player.PLAYER1   -> arrayOf("Winner", "\uD83C\uDFBE")
        winner == Player.PLAYER2   -> arrayOf("\uD83C\uDFBE", "Winner")
        scoreP1 < 3 || scoreP2 < 3 -> arrayOf(mapScore(scoreP1), mapScore(scoreP2))
        scoreP1 > scoreP2          -> arrayOf("Advantage", "\uD83C\uDFBE")
        scoreP1 < scoreP2          -> arrayOf("\uD83C\uDFBE", "Advantage")
        else                       -> arrayOf("Deuce", "Deuce")
    }
}