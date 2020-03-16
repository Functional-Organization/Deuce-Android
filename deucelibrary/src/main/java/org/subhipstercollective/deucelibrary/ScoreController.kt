/*
 * Copyright (C) 2019 Jeffrey Thomas Piercy
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

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class ScoreController {
    var winMinimumMatch = 0
    var winMinimumSet = WIN_MINIMUM_SET
    var winMarginSet = WIN_MARGIN_SET
    var winMinimumGame = WIN_MINIMUM_GAME
    var winMarginGame = WIN_MARGIN_GAME
    var winMinimumGameTiebreak = WIN_MINIMUM_GAME_TIEBREAK
    var winMarginGameTiebreak = WIN_MARGIN_GAME_TIEBREAK
    var animationDuration = ANIMATION_DURATION

    private var nextAnimationDuration = 0L

    var tiebreak = false
    var doubles = true
    var startingServer = Team.TEAM1

    private var match = Match(0, 0, 0, 0, 0, this, tiebreak)
    var serving = Serving.PLAYER1_LEFT

    private val currentSet get() = match.currentSet
    private val currentGame get() = match.currentGame
    private var scoreLog = ScoreStack()

    var activityScore: ScoreView? = null

    var matchAdded = false
        private set

    fun loadInstanceState(savedInstanceState: Bundle) {
        winMinimumMatch = savedInstanceState.getInt("winMinimumMatch")
        winMinimumSet = savedInstanceState.getInt("winMinimumSet")
        winMarginSet = savedInstanceState.getInt("winMarginSet")
        winMinimumGame = savedInstanceState.getInt("winMinimumGame")
        winMarginGame = savedInstanceState.getInt("winMarginGame")
        winMinimumGameTiebreak = savedInstanceState.getInt("winMinimumGameTiebreak")
        winMarginGameTiebreak = savedInstanceState.getInt("winMarginGameTiebreak")
        animationDuration = savedInstanceState.getLong("animationDuration")
        nextAnimationDuration = savedInstanceState.getLong("nextAnimationDuration")
        tiebreak = savedInstanceState.getBoolean("tiebreak")
        doubles = savedInstanceState.getBoolean("doubles")
        startingServer = savedInstanceState.getSerializable("startingServer") as Team
        scoreLog = savedInstanceState.getParcelable("scores")!!
        matchAdded = savedInstanceState.getBoolean("matchAdded")

        addMatch()
        loadScores()
    }

    fun saveInstanceState(): Bundle {
        val outState = Bundle()
        outState.putParcelable("scores", scoreLog)
        outState.putInt("winMinimumMatch", winMinimumMatch)
        outState.putInt("winMinimumSet", winMinimumSet)
        outState.putInt("winMarginSet", winMarginSet)
        outState.putInt("winMinimumGame", winMinimumGame)
        outState.putInt("winMarginGame", winMarginGame)
        outState.putInt("winMinimumGameTiebreak", winMinimumGameTiebreak)
        outState.putInt("winMarginGameTiebreak", winMarginGameTiebreak)
        outState.putLong("animationDuration", animationDuration)
        outState.putLong("nextAnimationDuration", nextAnimationDuration)
        outState.putBoolean("tiebreak", tiebreak)
        outState.putBoolean("doubles", doubles)
        outState.putSerializable("startingServer", startingServer)
        outState.putBoolean("matchAdded", matchAdded)
        return outState
    }

    private fun loadScores() {
        val numScores = scoreLog.size
        for (i in 0 until numScores) {
            score(scoreLog[i], false)
        }
    }

    fun addMatch() {
        matchAdded = true
        match = Match(
            winMinimumMatch,
            winMinimumSet,
            winMarginSet,
            winMinimumGame,
            winMarginGame,
            this,
            tiebreak
        )
        serving = if (startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER2_RIGHT

        activityScore?.buttonScoreP1?.isEnabled = true
        activityScore?.buttonScoreP2?.isEnabled = true

        redrawDisplay()
    }

    fun redrawDisplay() {
        nextAnimationDuration = 0
        updateDisplay()
    }

    private fun updateDisplay() {
        fun moveBall(ball: ImageView, xPos: Float) {
            ObjectAnimator.ofFloat(ball, "translationX", xPos).apply {
                duration = nextAnimationDuration
                start()
            }
        }

        val mActivityScore = activityScore ?: return

        val scores = currentGame.getScoreStrs()
        mActivityScore.textScoreP1.text = scores.player1
        mActivityScore.textScoreP2.text = scores.player2

        if (mActivityScore.displayBalls) {
            when (serving) {
                Serving.PLAYER1_LEFT -> {
                    mActivityScore.imageBallServingT1.setImageResource(R.drawable.ball_green)
                    moveBall(mActivityScore.imageBallServingT1, mActivityScore.posXBallLeftT1)
                    mActivityScore.imageBallServingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT2.visibility = View.INVISIBLE

                    if (doubles) {
                        mActivityScore.imageBallNotservingT1.setImageResource(R.drawable.ball_darkorange)
                        moveBall(mActivityScore.imageBallNotservingT1, mActivityScore.posXBallRightT1)
                        mActivityScore.imageBallNotservingT1.visibility = View.VISIBLE
                        mActivityScore.imageBallNotservingT2.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER1_RIGHT -> {
                    mActivityScore.imageBallServingT1.setImageResource(R.drawable.ball_green)
                    moveBall(mActivityScore.imageBallServingT1, mActivityScore.posXBallRightT1)
                    mActivityScore.imageBallServingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT2.visibility = View.INVISIBLE

                    if (doubles) {
                        mActivityScore.imageBallNotservingT1.setImageResource(R.drawable.ball_darkorange)
                        moveBall(mActivityScore.imageBallNotservingT1, mActivityScore.posXBallLeftT1)
                        mActivityScore.imageBallNotservingT1.visibility = View.VISIBLE
                        mActivityScore.imageBallNotservingT2.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER2_LEFT -> {
                    mActivityScore.imageBallServingT2.setImageResource(R.drawable.ball_green)
                    moveBall(mActivityScore.imageBallServingT2, mActivityScore.posXBallLeftT2)
                    mActivityScore.imageBallServingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT1.visibility = View.INVISIBLE

                    if (doubles) {
                        mActivityScore.imageBallNotservingT2.setImageResource(R.drawable.ball_darkorange)
                        moveBall(mActivityScore.imageBallNotservingT2, mActivityScore.posXBallRightT2)
                        mActivityScore.imageBallNotservingT2.visibility = View.VISIBLE
                        mActivityScore.imageBallNotservingT1.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER2_RIGHT -> {
                    mActivityScore.imageBallServingT2.setImageResource(R.drawable.ball_green)
                    moveBall(mActivityScore.imageBallServingT2, mActivityScore.posXBallRightT2)
                    mActivityScore.imageBallServingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT1.visibility = View.INVISIBLE

                    if (doubles) {
                        mActivityScore.imageBallNotservingT2.setImageResource(R.drawable.ball_darkorange)
                        moveBall(mActivityScore.imageBallNotservingT2, mActivityScore.posXBallLeftT2)
                        mActivityScore.imageBallNotservingT2.visibility = View.VISIBLE
                        mActivityScore.imageBallNotservingT1.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER3_LEFT -> {
                    mActivityScore.imageBallServingT1.setImageResource(R.drawable.ball_orange)
                    moveBall(mActivityScore.imageBallServingT1, mActivityScore.posXBallLeftT1)
                    mActivityScore.imageBallServingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT2.visibility = View.INVISIBLE

                    mActivityScore.imageBallNotservingT1.setImageResource(R.drawable.ball_darkgreen)
                    moveBall(mActivityScore.imageBallNotservingT1, mActivityScore.posXBallRightT1)
                    mActivityScore.imageBallNotservingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallNotservingT2.visibility = View.INVISIBLE
                }
                Serving.PLAYER3_RIGHT -> {
                    mActivityScore.imageBallServingT1.setImageResource(R.drawable.ball_orange)
                    moveBall(mActivityScore.imageBallServingT1, mActivityScore.posXBallRightT1)
                    mActivityScore.imageBallServingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT2.visibility = View.INVISIBLE

                    mActivityScore.imageBallNotservingT1.setImageResource(R.drawable.ball_darkgreen)
                    moveBall(mActivityScore.imageBallNotservingT1, mActivityScore.posXBallLeftT1)
                    mActivityScore.imageBallNotservingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallNotservingT2.visibility = View.INVISIBLE
                }
                Serving.PLAYER4_LEFT -> {
                    mActivityScore.imageBallServingT2.setImageResource(R.drawable.ball_orange)
                    moveBall(mActivityScore.imageBallServingT2, mActivityScore.posXBallLeftT2)
                    mActivityScore.imageBallServingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT1.visibility = View.INVISIBLE

                    mActivityScore.imageBallNotservingT2.setImageResource(R.drawable.ball_darkgreen)
                    moveBall(mActivityScore.imageBallNotservingT2, mActivityScore.posXBallRightT2)
                    mActivityScore.imageBallNotservingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallNotservingT1.visibility = View.INVISIBLE
                }
                Serving.PLAYER4_RIGHT -> {
                    mActivityScore.imageBallServingT2.setImageResource(R.drawable.ball_orange)
                    moveBall(mActivityScore.imageBallServingT2, mActivityScore.posXBallRightT2)
                    mActivityScore.imageBallServingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT1.visibility = View.INVISIBLE

                    mActivityScore.imageBallNotservingT2.setImageResource(R.drawable.ball_darkgreen)
                    moveBall(mActivityScore.imageBallNotservingT2, mActivityScore.posXBallLeftT2)
                    mActivityScore.imageBallNotservingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallNotservingT1.visibility = View.INVISIBLE
                }
            }

            nextAnimationDuration = animationDuration
        }

        var textScoresMatchP1 = ""
        var textScoresMatchP2 = ""
        for (set in match.sets) {
            textScoresMatchP1 += set.scoreP1.toString() + "  "
            textScoresMatchP2 += set.scoreP2.toString() + "  "
        }
        mActivityScore.textScoresMatchP1.text = textScoresMatchP1.trim()
        mActivityScore.textScoresMatchP2.text = textScoresMatchP2.trim()
    }

    fun score(team: Team, updateLog: Boolean = true) {
        val winnerGame = currentGame.score(team)
        if (winnerGame != Winner.NONE) {
            val winnerSet = currentSet.score(team)
            if (winnerSet != Winner.NONE) {
                val winnerMatch = match.score(team)
                if (winnerMatch != Winner.NONE) {
                    // Match is over
                    activityScore?.buttonScoreP1?.isEnabled = false
                    activityScore?.buttonScoreP2?.isEnabled = false
                } else {
                    // Set is over
                    match.addNewSet()
                }
            } else {
                // Game is over
                currentSet.addNewGame()
                if (currentGame.tiebreak) {
                    serving = when (serving) {
                        Serving.PLAYER1_RIGHT -> Serving.PLAYER1_LEFT
                        Serving.PLAYER2_RIGHT -> Serving.PLAYER2_LEFT
                        Serving.PLAYER3_RIGHT -> Serving.PLAYER3_LEFT
                        Serving.PLAYER4_RIGHT -> Serving.PLAYER4_LEFT
                        else -> Serving.PLAYER1_RIGHT
                    }
                }
            }

            nextAnimationDuration = 0

            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT -> if (doubles && startingServer == Team.TEAM2) Serving.PLAYER4_RIGHT else Serving.PLAYER2_RIGHT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT -> if (doubles && startingServer == Team.TEAM1) Serving.PLAYER3_RIGHT else Serving.PLAYER1_RIGHT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT -> if (startingServer == Team.TEAM1) Serving.PLAYER4_RIGHT else Serving.PLAYER2_RIGHT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT -> if (startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER3_RIGHT
            }
        } else if (currentGame.tiebreak && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(Team.TEAM2)) % 2 == 1) {
            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT -> if (doubles && startingServer == Team.TEAM2) Serving.PLAYER4_LEFT else Serving.PLAYER2_LEFT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT -> if (doubles && startingServer == Team.TEAM1) Serving.PLAYER3_LEFT else Serving.PLAYER1_LEFT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT -> if (startingServer == Team.TEAM1) Serving.PLAYER4_LEFT else Serving.PLAYER2_LEFT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT -> if (startingServer == Team.TEAM1) Serving.PLAYER1_LEFT else Serving.PLAYER3_LEFT
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
            scoreLog.push(team)
            updateDisplay()
        }
    }

    fun undo() {
        if (scoreLog.size != 0) {
            scoreLog.pop()
            addMatch()

            loadScores()

            redrawDisplay()
        }
    }
}
