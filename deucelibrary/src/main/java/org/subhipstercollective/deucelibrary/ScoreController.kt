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

package org.subhipstercollective.deucelibrary

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class ScoreController {
    var animationDuration = ANIMATION_DURATION

    private var nextAnimationDuration = 0L

    private var match = Match(0, 0, 0, 0, 0, 0, 0, 0, Team.TEAM1, Overtime.TIEBREAK, Players.SINGLES, this)
    var serving = Serving.PLAYER1_LEFT

    private val currentSet get() = match.currentSet
    private val currentGame get() = match.currentGame
    private var scoreLog = ScoreStack()

    var scoreView: ScoreView? = null

    var matchAdded = false
        private set

    fun loadInstanceState(savedInstanceState: Bundle) {
        addMatch(
            savedInstanceState.getInt("winMinimumMatch"),
            savedInstanceState.getInt("winMarginMatch"),
            savedInstanceState.getInt("winMinimumSet"),
            savedInstanceState.getInt("winMarginSet"),
            savedInstanceState.getInt("winMinimumGame"),
            savedInstanceState.getInt("winMarginGame"),
            savedInstanceState.getInt("winMinimumGameTiebreak"),
            savedInstanceState.getInt("winMarginGameTiebreak"),
            savedInstanceState.getSerializable("startingServer") as Team,
            savedInstanceState.getSerializable("overtime") as Overtime,
            savedInstanceState.getSerializable("players") as Players
        )

        animationDuration = savedInstanceState.getLong("animationDuration")
        nextAnimationDuration = savedInstanceState.getLong("nextAnimationDuration")
        scoreLog = savedInstanceState.getParcelable("scores")!!
        matchAdded = savedInstanceState.getBoolean("matchAdded")
    }

    fun saveInstanceState(): Bundle {
        val outState = Bundle()
        outState.putParcelable("scores", scoreLog)

        outState.putInt("winMinimumMatch", match.winMinimum)
        outState.putInt("winMarginMatch", match.winMargin)
        outState.putInt("winMinimumSet", match.winMinimumSet)
        outState.putInt("winMarginSet", match.winMarginSet)
        outState.putInt("winMinimumGame", match.winMinimumGame)
        outState.putInt("winMarginGame", match.winMarginGame)
        outState.putInt("winMinimumGameTiebreak", match.winMinimumGameTiebreak)
        outState.putInt("winMarginGameTiebreak", match.winMarginGameTiebreak)
        outState.putSerializable("overtime", match.overtime)
        outState.putSerializable("players", match.players)

        outState.putLong("animationDuration", animationDuration)
        outState.putLong("nextAnimationDuration", nextAnimationDuration)
        outState.putSerializable("startingServer", match.startingServer)
        outState.putBoolean("matchAdded", matchAdded)
        return outState
    }

    private fun loadScores() {
        val numScores = scoreLog.size
        for (i in 0 until numScores) {
            score(scoreLog[i], false)
        }
    }

    private fun addMatch(
        numSets: Int, winMarginMatch: Int,
        winMinimumSet: Int, winMarginSet: Int,
        winMinimumGame: Int, winMarginGame: Int,
        winMinimumGameTiebreak: Int, winMarginGameTiebreak: Int,
        startingServer: Team,
        overtime: Overtime,
        players: Players
    ) {
        matchAdded = true
        match = Match(
            numSets,
            winMarginMatch,
            winMinimumSet,
            winMarginSet,
            winMinimumGame,
            winMarginGame,
            winMinimumGameTiebreak,
            winMarginGameTiebreak,
            startingServer,
            overtime,
            players,
            this
        )

        serving = if (startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER2_RIGHT

        redrawDisplay()
    }

    fun addMatch(
        numSets: NumSets, winMarginMatch: Int,
        winMinimumSet: Int, winMarginSet: Int,
        winMinimumGame: Int, winMarginGame: Int,
        winMinimumGameTiebreak: Int, winMarginGameTiebreak: Int,
        startingServer: Team,
        overtime: Overtime,
        players: Players
    ) {
        addMatch(
            numSets.value,
            winMarginMatch,
            winMinimumSet,
            winMarginSet,
            winMinimumGame,
            winMarginGame,
            winMinimumGameTiebreak,
            winMarginGameTiebreak,
            startingServer,
            overtime,
            players
        )
    }

    fun redrawDisplay() {
        val mScoreView = scoreView
        if (mScoreView != null && mScoreView.viewCreated) {
            nextAnimationDuration = 0
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        fun moveBall(ball: ImageView, xPos: Float) {
            ObjectAnimator.ofFloat(ball, "translationX", xPos).apply {
                duration = nextAnimationDuration
                start()
            }
        }

        val mActivityScore = scoreView ?: return

        val scores = currentGame.getScoreStrings()
        mActivityScore.textScoreP1.text = scores.player1
        mActivityScore.textScoreP2.text = scores.player2

        val ballServingGreen = if (mActivityScore.ambientMode) R.drawable.ball_ambient else R.drawable.ball_green
        val ballServingOrange = if (mActivityScore.ambientMode) R.drawable.ball_ambient else R.drawable.ball_orange
        val ballNotservingGreen = if (mActivityScore.ambientMode) R.drawable.ball_void else R.drawable.ball_darkgreen
        val ballNotservingOrange = if (mActivityScore.ambientMode) R.drawable.ball_void else R.drawable.ball_darkorange

        if (match.winner == Winner.NONE) {
            when (serving) {
                Serving.PLAYER1_LEFT -> {
                    mActivityScore.imageBallServingT1.setImageResource(ballServingGreen)
                    moveBall(mActivityScore.imageBallServingT1, mActivityScore.posXBallLeftT1)
                    mActivityScore.imageBallServingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT2.visibility = View.INVISIBLE

                    if (match.players == Players.DOUBLES) {
                        mActivityScore.imageBallNotservingT1.setImageResource(ballNotservingOrange)
                        moveBall(mActivityScore.imageBallNotservingT1, mActivityScore.posXBallRightT1)
                        mActivityScore.imageBallNotservingT1.visibility = View.VISIBLE
                        mActivityScore.imageBallNotservingT2.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER1_RIGHT -> {
                    mActivityScore.imageBallServingT1.setImageResource(ballServingGreen)
                    moveBall(mActivityScore.imageBallServingT1, mActivityScore.posXBallRightT1)
                    mActivityScore.imageBallServingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT2.visibility = View.INVISIBLE

                    if (match.players == Players.DOUBLES) {
                        mActivityScore.imageBallNotservingT1.setImageResource(ballNotservingOrange)
                        moveBall(mActivityScore.imageBallNotservingT1, mActivityScore.posXBallLeftT1)
                        mActivityScore.imageBallNotservingT1.visibility = View.VISIBLE
                        mActivityScore.imageBallNotservingT2.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER2_LEFT -> {
                    mActivityScore.imageBallServingT2.setImageResource(ballServingGreen)
                    moveBall(mActivityScore.imageBallServingT2, mActivityScore.posXBallLeftT2)
                    mActivityScore.imageBallServingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT1.visibility = View.INVISIBLE

                    if (match.players == Players.DOUBLES) {
                        mActivityScore.imageBallNotservingT2.setImageResource(ballNotservingOrange)
                        moveBall(mActivityScore.imageBallNotservingT2, mActivityScore.posXBallRightT2)
                        mActivityScore.imageBallNotservingT2.visibility = View.VISIBLE
                        mActivityScore.imageBallNotservingT1.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER2_RIGHT -> {
                    mActivityScore.imageBallServingT2.setImageResource(ballServingGreen)
                    moveBall(mActivityScore.imageBallServingT2, mActivityScore.posXBallRightT2)
                    mActivityScore.imageBallServingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT1.visibility = View.INVISIBLE

                    if (match.players == Players.DOUBLES) {
                        mActivityScore.imageBallNotservingT2.setImageResource(ballNotservingOrange)
                        moveBall(mActivityScore.imageBallNotservingT2, mActivityScore.posXBallLeftT2)
                        mActivityScore.imageBallNotservingT2.visibility = View.VISIBLE
                        mActivityScore.imageBallNotservingT1.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER3_LEFT -> {
                    mActivityScore.imageBallServingT1.setImageResource(ballServingOrange)
                    moveBall(mActivityScore.imageBallServingT1, mActivityScore.posXBallLeftT1)
                    mActivityScore.imageBallServingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT2.visibility = View.INVISIBLE

                    mActivityScore.imageBallNotservingT1.setImageResource(ballNotservingGreen)
                    moveBall(mActivityScore.imageBallNotservingT1, mActivityScore.posXBallRightT1)
                    mActivityScore.imageBallNotservingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallNotservingT2.visibility = View.INVISIBLE
                }
                Serving.PLAYER3_RIGHT -> {
                    mActivityScore.imageBallServingT1.setImageResource(ballServingOrange)
                    moveBall(mActivityScore.imageBallServingT1, mActivityScore.posXBallRightT1)
                    mActivityScore.imageBallServingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT2.visibility = View.INVISIBLE

                    mActivityScore.imageBallNotservingT1.setImageResource(ballNotservingGreen)
                    moveBall(mActivityScore.imageBallNotservingT1, mActivityScore.posXBallLeftT1)
                    mActivityScore.imageBallNotservingT1.visibility = View.VISIBLE
                    mActivityScore.imageBallNotservingT2.visibility = View.INVISIBLE
                }
                Serving.PLAYER4_LEFT -> {
                    mActivityScore.imageBallServingT2.setImageResource(ballServingOrange)
                    moveBall(mActivityScore.imageBallServingT2, mActivityScore.posXBallLeftT2)
                    mActivityScore.imageBallServingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT1.visibility = View.INVISIBLE

                    mActivityScore.imageBallNotservingT2.setImageResource(ballNotservingGreen)
                    moveBall(mActivityScore.imageBallNotservingT2, mActivityScore.posXBallRightT2)
                    mActivityScore.imageBallNotservingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallNotservingT1.visibility = View.INVISIBLE
                }
                Serving.PLAYER4_RIGHT -> {
                    mActivityScore.imageBallServingT2.setImageResource(ballServingOrange)
                    moveBall(mActivityScore.imageBallServingT2, mActivityScore.posXBallRightT2)
                    mActivityScore.imageBallServingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallServingT1.visibility = View.INVISIBLE

                    mActivityScore.imageBallNotservingT2.setImageResource(ballNotservingGreen)
                    moveBall(mActivityScore.imageBallNotservingT2, mActivityScore.posXBallLeftT2)
                    mActivityScore.imageBallNotservingT2.visibility = View.VISIBLE
                    mActivityScore.imageBallNotservingT1.visibility = View.INVISIBLE
                }
            }
        } else {
            scoreView?.imageBallServingT1?.visibility = View.INVISIBLE
            scoreView?.imageBallNotservingT1?.visibility = View.INVISIBLE
            scoreView?.imageBallServingT2?.visibility = View.INVISIBLE
            scoreView?.imageBallNotservingT2?.visibility = View.INVISIBLE
        }

        nextAnimationDuration = animationDuration

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
                    scoreView?.buttonScoreP1?.isEnabled = false
                    scoreView?.buttonScoreP2?.isEnabled = false
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

            if (currentSet.games.size % 2 == 0) {
                scoreView?.doHapticChangeover()
            }

            nextAnimationDuration = 0

            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT ->
                    if (match.players == Players.DOUBLES && match.startingServer == Team.TEAM2)
                        Serving.PLAYER4_RIGHT
                    else
                        Serving.PLAYER2_RIGHT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT ->
                    if (match.players == Players.DOUBLES && match.startingServer == Team.TEAM1)
                        Serving.PLAYER3_RIGHT
                    else
                        Serving.PLAYER1_RIGHT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT ->
                    if (match.startingServer == Team.TEAM1)
                        Serving.PLAYER4_RIGHT
                    else
                        Serving.PLAYER2_RIGHT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT ->
                    if (match.startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER3_RIGHT
            }
        } else if (currentGame.tiebreak && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(Team.TEAM2)) % 2 == 1) {
            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT ->
                    if (match.players == Players.DOUBLES && match.startingServer == Team.TEAM2)
                        Serving.PLAYER4_LEFT
                    else
                        Serving.PLAYER2_LEFT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT ->
                    if (match.players == Players.DOUBLES && match.startingServer == Team.TEAM1)
                        Serving.PLAYER3_LEFT
                    else
                        Serving.PLAYER1_LEFT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT ->
                    if
                            (match.startingServer == Team.TEAM1) Serving.PLAYER4_LEFT
                    else
                        Serving.PLAYER2_LEFT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT ->
                    if (match.startingServer == Team.TEAM1)
                        Serving.PLAYER1_LEFT
                    else
                        Serving.PLAYER3_LEFT
            }
            nextAnimationDuration = 0
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

        if (currentGame.tiebreak && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(Team.TEAM2)) % 6 == 0) {
            scoreView?.doHapticChangeover()
        }
    }

    fun undo(): Boolean {
        if (scoreLog.size != 0) {
            scoreLog.pop()
            addMatch(
                match.winMinimum,
                match.winMargin,
                match.winMinimumSet,
                match.winMarginSet,
                match.winMinimumGame,
                match.winMarginGame,
                match.winMinimumGameTiebreak,
                match.winMarginGameTiebreak,
                match.startingServer,
                match.overtime,
                match.players
            )

            loadScores()

            redrawDisplay()

            return true
        }
        return false
    }
}
