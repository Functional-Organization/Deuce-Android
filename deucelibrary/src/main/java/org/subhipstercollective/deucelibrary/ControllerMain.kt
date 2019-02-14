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

import android.view.View

/**
 * Created by mqduck on 11/6/17.
 */
class ControllerMain(val activityMain: ActivityMain) {
    private var match = Match(0, 0, 0, 0, 0, Player.PLAYER1, false, this)
    var serving = Serving.PLAYER1_LEFT

    var winMinimumMatch = 7
    var winMinimumSet = 7
    var winMarginSet = 1
    var winMinimumGame = 4
    var winMarginGame = 2
    var winMinimumGameTiebreak = 7
    var winMarginGameTiebreak = 1

    private val currentSet get() = match.currentSet
    private val currentGame get() = match.currentGame
    private val scoreLog = ArrayList<Player>()

    fun addMatch(startingServer: Player, advantage: Boolean) {
        match = Match(
                winMinimumMatch,
                winMinimumSet,
                winMarginSet,
                winMinimumGame,
                winMarginGame,
                startingServer,
                advantage,
                this)
        serving = if (startingServer == Player.PLAYER1) Serving.PLAYER1_RIGHT else Serving.PLAYER2_RIGHT
        activityMain.buttonScoreP1.isEnabled = true
        activityMain.buttonScoreP2.isEnabled = true

        updateDisplay()
    }

    fun updateDisplay() {
        val scores = currentGame.getScoreStrs()
        activityMain.textScoreP1.text = scores.player1
        activityMain.textScoreP2.text = scores.player2

        activityMain.imageBallP2Left.visibility = if (serving === Serving.PLAYER2_LEFT) View.VISIBLE else View.INVISIBLE
        activityMain.imageBallP2Right.visibility = if (serving === Serving.PLAYER2_RIGHT) View.VISIBLE else View.INVISIBLE
        activityMain.imageBallP1Left.visibility = if (serving === Serving.PLAYER1_LEFT) View.VISIBLE else View.INVISIBLE
        activityMain.imageBallP1Right.visibility = if (serving === Serving.PLAYER1_RIGHT) View.VISIBLE else View.INVISIBLE

        var textScoresMatchP1 = ""
        var textScoreMatchP2 = ""
        for (set in match.sets) {
            textScoresMatchP1 += set.scoreP1.toString() + "  "
            textScoreMatchP2 += set.scoreP2.toString() + "  "
        }
        activityMain.textScoresMatchP1.text = textScoresMatchP1.trim()
        activityMain.textScoresMatchP2.text = textScoreMatchP2.trim()
    }

    fun score(player: Player, updateLog: Boolean = true) {
        val winnerGame = currentGame.score(player)
        if (winnerGame != Player.NONE) {
            val winnerSet = currentSet.score(winnerGame)
            if (winnerSet != Player.NONE) {
                val winnerMatch = match.score(winnerGame)
                if (winnerMatch != Player.NONE) {
                    // Match is over
                    activityMain.buttonScoreP1.isEnabled = false
                    activityMain.buttonScoreP2.isEnabled = false
                } else {
                    // Set is over
                    match.addNewSet()
                }
            } else {
                // Game is over
                if (!match.advantage && currentSet.scoreP1 == currentSet.scoreP2 && currentSet.scoreP1 == winMinimumSet - 1) {
                    currentSet.addNewGame(winMinimumGameTiebreak, winMarginGameTiebreak, true)
                } else {
                    currentSet.addNewGame()
                }
            }

            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT -> Serving.PLAYER2_RIGHT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT -> Serving.PLAYER1_RIGHT
            }
        } else {
            serving = when (serving) {
                Serving.PLAYER1_LEFT -> Serving.PLAYER1_RIGHT
                Serving.PLAYER1_RIGHT -> Serving.PLAYER1_LEFT
                Serving.PLAYER2_LEFT -> Serving.PLAYER2_RIGHT
                Serving.PLAYER2_RIGHT -> Serving.PLAYER2_LEFT
            }
        }

        if (updateLog) {
            scoreLog.add(player)
            updateDisplay()
        }
    }

    fun undo() {
        if (scoreLog.size != 0) {
            scoreLog.removeAt(scoreLog.size - 1)
            addMatch(match.startingServer, match.advantage)

            val numScores = scoreLog.size
            for (i in 0 until numScores) {
                score(scoreLog[i], false)
            }

            updateDisplay()
        }
    }
}
