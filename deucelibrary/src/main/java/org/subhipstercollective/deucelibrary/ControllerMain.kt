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
    var winMinimumMatch = 0
    var winMinimumSet = WIN_MINIMUM_SET
    var winMarginSet = WIN_MARGIN_SET
    var winMinimumGame = WIN_MINIMUM_GAME
    var winMarginGame = WIN_MARGIN_GAME
    var winMinimumGameTiebreak = WIN_MINIMUM_GAME_TIEBREAK
    var winMarginGameTiebreak = WIN_MARGIN_GAME_TIEBREAK

    private var match = Match(0, 0, 0, 0, 0, this)
    var serving = Serving.PLAYER1_LEFT

    var advantage = false
    var doubles = true
    var startingServer = Player.PLAYER1

    private val currentSet get() = match.currentSet
    private val currentGame get() = match.currentGame
    private val scoreLog = ArrayList<Player>()

    fun addMatch() {
        match = Match(
                winMinimumMatch,
                winMinimumSet,
                winMarginSet,
                winMinimumGame,
                winMarginGame,
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

//        activityMain.imageBallP2Left.visibility = if (serving === Serving.PLAYER2_LEFT) View.VISIBLE else View.INVISIBLE
//        activityMain.imageBallP2Right.visibility = if (serving === Serving.PLAYER2_RIGHT) View.VISIBLE else View.INVISIBLE
//        activityMain.imageBallP1Left.visibility = if (serving === Serving.PLAYER1_LEFT) View.VISIBLE else View.INVISIBLE
//        activityMain.imageBallP1Right.visibility = if (serving === Serving.PLAYER1_RIGHT) View.VISIBLE else View.INVISIBLE

        when (serving) {
            Serving.PLAYER1_LEFT -> {
                if (doubles) {
                    activityMain.imageBallP1Left.setImageResource(R.drawable.ball_green)
                    activityMain.imageBallP1Right.setImageResource(R.drawable.ball_darkorange)
                    activityMain.imageBallP1Left.visibility = View.VISIBLE
                    activityMain.imageBallP1Right.visibility = View.VISIBLE
                    activityMain.imageBallP2Left.visibility = View.INVISIBLE
                    activityMain.imageBallP2Right.visibility = View.INVISIBLE
                } else {
                    activityMain.imageBallP1Left.visibility = View.VISIBLE
                    activityMain.imageBallP1Right.visibility = View.INVISIBLE
                    activityMain.imageBallP2Left.visibility = View.INVISIBLE
                    activityMain.imageBallP2Right.visibility = View.INVISIBLE
                }
            }
            Serving.PLAYER1_RIGHT -> {
                if (doubles) {
                    activityMain.imageBallP1Left.setImageResource(R.drawable.ball_darkorange)
                    activityMain.imageBallP1Right.setImageResource(R.drawable.ball_green)
                    activityMain.imageBallP1Left.visibility = View.VISIBLE
                    activityMain.imageBallP1Right.visibility = View.VISIBLE
                    activityMain.imageBallP2Left.visibility = View.INVISIBLE
                    activityMain.imageBallP2Right.visibility = View.INVISIBLE
                } else {
                    activityMain.imageBallP1Left.visibility = View.INVISIBLE
                    activityMain.imageBallP1Right.visibility = View.VISIBLE
                    activityMain.imageBallP2Left.visibility = View.INVISIBLE
                    activityMain.imageBallP2Right.visibility = View.INVISIBLE
                }
            }
            Serving.PLAYER2_LEFT -> {
                if (doubles) {
                    activityMain.imageBallP2Left.setImageResource(R.drawable.ball_green)
                    activityMain.imageBallP2Right.setImageResource(R.drawable.ball_darkorange)
                    activityMain.imageBallP1Left.visibility = View.INVISIBLE
                    activityMain.imageBallP1Right.visibility = View.INVISIBLE
                    activityMain.imageBallP2Left.visibility = View.VISIBLE
                    activityMain.imageBallP2Right.visibility = View.VISIBLE
                } else {
                    activityMain.imageBallP1Left.visibility = View.INVISIBLE
                    activityMain.imageBallP1Right.visibility = View.INVISIBLE
                    activityMain.imageBallP2Left.visibility = View.VISIBLE
                    activityMain.imageBallP2Right.visibility = View.INVISIBLE
                }
            }
            Serving.PLAYER2_RIGHT -> {
                if (doubles) {
                    activityMain.imageBallP2Left.setImageResource(R.drawable.ball_darkorange)
                    activityMain.imageBallP2Right.setImageResource(R.drawable.ball_green)
                    activityMain.imageBallP1Left.visibility = View.INVISIBLE
                    activityMain.imageBallP1Right.visibility = View.INVISIBLE
                    activityMain.imageBallP2Left.visibility = View.VISIBLE
                    activityMain.imageBallP2Right.visibility = View.VISIBLE
                } else {
                    activityMain.imageBallP1Left.visibility = View.INVISIBLE
                    activityMain.imageBallP1Right.visibility = View.INVISIBLE
                    activityMain.imageBallP2Left.visibility = View.INVISIBLE
                    activityMain.imageBallP2Right.visibility = View.VISIBLE
                }
            }
            Serving.PLAYER3_LEFT -> {
                activityMain.imageBallP1Left.setImageResource(R.drawable.ball_orange)
                activityMain.imageBallP1Right.setImageResource(R.drawable.ball_darkgreen)
                activityMain.imageBallP1Left.visibility = View.VISIBLE
                activityMain.imageBallP1Right.visibility = View.VISIBLE
                activityMain.imageBallP2Left.visibility = View.INVISIBLE
                activityMain.imageBallP2Right.visibility = View.INVISIBLE
            }
            Serving.PLAYER3_RIGHT -> {
                activityMain.imageBallP1Left.setImageResource(R.drawable.ball_darkgreen)
                activityMain.imageBallP1Right.setImageResource(R.drawable.ball_orange)
                activityMain.imageBallP1Left.visibility = View.VISIBLE
                activityMain.imageBallP1Right.visibility = View.VISIBLE
                activityMain.imageBallP2Left.visibility = View.INVISIBLE
                activityMain.imageBallP2Right.visibility = View.INVISIBLE
            }
            Serving.PLAYER4_LEFT -> {
                activityMain.imageBallP2Left.setImageResource(R.drawable.ball_orange)
                activityMain.imageBallP2Right.setImageResource(R.drawable.ball_darkgreen)
                activityMain.imageBallP1Left.visibility = View.INVISIBLE
                activityMain.imageBallP1Right.visibility = View.INVISIBLE
                activityMain.imageBallP2Left.visibility = View.VISIBLE
                activityMain.imageBallP2Right.visibility = View.VISIBLE
            }
            Serving.PLAYER4_RIGHT -> {
                activityMain.imageBallP2Left.setImageResource(R.drawable.ball_darkgreen)
                activityMain.imageBallP2Right.setImageResource(R.drawable.ball_orange)
                activityMain.imageBallP1Left.visibility = View.INVISIBLE
                activityMain.imageBallP1Right.visibility = View.INVISIBLE
                activityMain.imageBallP2Left.visibility = View.VISIBLE
                activityMain.imageBallP2Right.visibility = View.VISIBLE
            }
        }

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
                if (advantage && currentSet.scoreP1 == currentSet.scoreP2 && currentSet.scoreP1 == winMinimumSet - 1) {
                    currentSet.addNewGame(winMinimumGameTiebreak, winMarginGameTiebreak, true)
                } else {
                    currentSet.addNewGame()
                }
            }

            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT -> if (startingServer == Player.PLAYER1) Serving.PLAYER2_RIGHT else Serving.PLAYER4_RIGHT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT -> if (doubles && startingServer == Player.PLAYER1) Serving.PLAYER3_RIGHT else Serving.PLAYER1_RIGHT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT -> if (startingServer == Player.PLAYER1) Serving.PLAYER4_RIGHT else Serving.PLAYER2_RIGHT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT -> if (startingServer == Player.PLAYER1) Serving.PLAYER1_RIGHT else Serving.PLAYER3_RIGHT
            }
        } else {
            serving = when (serving) {
                Serving.PLAYER1_LEFT -> Serving.PLAYER1_RIGHT
                Serving.PLAYER1_RIGHT -> Serving.PLAYER1_LEFT
                Serving.PLAYER2_LEFT -> Serving.PLAYER2_RIGHT
                Serving.PLAYER2_RIGHT -> Serving.PLAYER2_LEFT
                Serving.PLAYER3_LEFT -> Serving.PLAYER3_RIGHT
                Serving.PLAYER3_RIGHT -> Serving.PLAYER3_LEFT
                Serving.PLAYER4_LEFT -> Serving.PLAYER4_RIGHT
                Serving.PLAYER4_RIGHT -> Serving.PLAYER4_LEFT
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
            addMatch()

            val numScores = scoreLog.size
            for (i in 0 until numScores) {
                score(scoreLog[i], false)
            }

            updateDisplay()
        }
    }
}
