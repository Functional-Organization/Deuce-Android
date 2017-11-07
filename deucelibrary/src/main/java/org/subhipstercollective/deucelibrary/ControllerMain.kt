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

import android.widget.Button
import android.widget.TextView

/**
 * Created by mqduck on 11/6/17.
 */
class ControllerMain
{
    private var matches = ArrayList<Match>()

    var winMinimumMatch = 1
    var winMinimumSet = 6
    var winMarginSet = 2
    var winMinimumGame = 4
    var winMarginGame = 2

    var displayHeading: TextView? = null
    var displayScoreP1: TextView? = null
    var displayScoreP2: TextView? = null
    var displayLog: TextView? = null
    var displayButtonScoreP1: Button? = null
    var displayButtonScoreP2: Button? = null

    private val currentMatch get() = matches.last()
    private val currentSet get() = currentMatch.currentSet
    private val currentGame get() = currentMatch.currentGame

    val matchNumber get() = matches.size
    val setNumber get() = currentMatch.setNumber
    val gameNumber get() = currentSet.gameNumber

    fun getMatchScoreStrs() = currentMatch.getScoreStrs()
    fun getSetScoreStrs() = currentSet.getScoreStrs()
    fun getGameScoreStrs() = currentGame.getScoreStrs()

    fun addMatch()
    {
        if(matches.size != 0 && matches.last().winner == Player.NONE)
            matches.removeAt(matches.size - 1)
        matches.add(Match(winMinimumMatch, winMinimumSet, winMarginSet, winMinimumGame, winMarginGame))
        displayButtonScoreP1?.isEnabled = true
        displayButtonScoreP2?.isEnabled = true

    }

    fun updateDisplay()
    {
        if(matches.size != 0)
        {
            displayHeading?.setText(String.format("Set %d Game %d", setNumber, gameNumber))
            val scores = getGameScoreStrs()
            displayScoreP1?.setText(scores.player1)
            displayScoreP2?.setText(scores.player2)
        }
    }

    fun score(player: Player)
    {
        val winnerGame = currentGame.score(player)
        if(winnerGame != Player.NONE)
        {
            val winnerSet = currentSet.score(winnerGame)
            if(winnerSet != Player.NONE)
            {
                val winnerMatch = currentMatch.score(winnerGame)
                if(winnerMatch != Player.NONE)
                {
                    val winnerStr = if(winnerMatch == Player.PLAYER1) "Player 1" else "Player 2"
                    displayLog?.setText(String.format("%s\n%s wins the match.", displayLog!!.text, winnerStr, setNumber))
                    displayButtonScoreP1?.isEnabled = false
                    displayButtonScoreP2?.isEnabled = false
                }
                else
                {
                    val winnerStr = if (winnerSet == Player.PLAYER1) "Player 1" else "Player 2"
                    displayLog?.setText(String.format("%s\n%s wins set %d.", displayLog!!.text, winnerStr, setNumber))
                    currentMatch.addNewSet()
                }
            }
            else
            {
                val winnerStr = if(winnerGame == Player.PLAYER1) "Player 1" else "Player 2"
                displayLog?.setText(String.format("%s\n%s wins game %d.", displayLog!!.text, winnerStr, currentMatch.gameNumber))
                currentSet.addNewGame()
            }
        }

        updateDisplay()
    }
}
