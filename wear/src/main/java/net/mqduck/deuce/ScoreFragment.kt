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

package net.mqduck.deuce

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_score.*
import net.mqduck.deuce.common.*

class ScoreFragment(private val mainActivity: MainActivity) : Fragment() {
    lateinit var buttonScoreP1: Button
    lateinit var buttonScoreP2: Button
    lateinit var textScoreP1: TextView
    lateinit var textScoreP2: TextView
    lateinit var imageBallServingT1: ImageView
    lateinit var imageBallNotservingT1: ImageView
    lateinit var imageBallServingT2: ImageView
    lateinit var imageBallNotservingT2: ImageView
    lateinit var textScoresMatchP1: TextView
    lateinit var textScoresMatchP2: TextView
    lateinit var changeoverArrowDown: ImageView
    lateinit var changeoverArrowUp: ImageView
    var posXBallLeftT1 = 0F
    var posXBallRightT1 = 0F
    var posXBallLeftT2 = 0F
    var posXBallRightT2 = 0F
    var viewCreated = false

    val ambientMode = mainActivity.ambientMode

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonScoreP1 = button_score_p1
        buttonScoreP2 = button_score_p2
        textScoreP1 = button_score_p1
        textScoreP2 = button_score_p2

        imageBallServingT1 = ball_serving_t1
        imageBallNotservingT1 = ball_notserving_t1
        imageBallServingT2 = ball_serving_t2
        imageBallNotservingT2 = ball_notserving_t2

        textScoresMatchP1 = text_scores_match_p1
        textScoresMatchP2 = text_scores_match_p2

        posXBallLeftT1 = ball_notserving_t1.x
        posXBallRightT2 = posXBallLeftT1

        changeoverArrowDown = changeover_arrow_down
        changeoverArrowUp = changeover_arrow_up

        image_undo.visibility = View.GONE

        if (mainActivity.ambientMode) {
            textScoresMatchP1.setTextColor(Color.WHITE)
            textScoresMatchP2.setTextColor(Color.WHITE)
            text_clock.setTextColor(Color.WHITE)
            text_clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36F)

            text_clock.paint.isAntiAlias = false
            text_scores_match_p1.paint.isAntiAlias = false
            text_scores_match_p2.paint.isAntiAlias = false
            button_score_p1.paint.isAntiAlias = false
            button_score_p2.paint.isAntiAlias = false

            button_score_p1.isEnabled = false
            button_score_p2.isEnabled = false
        } else if (!mainActivity.preferences.clock) {
            text_clock.visibility = View.INVISIBLE
        }

        view.post {
            posXBallRightT1 = view.width - posXBallLeftT1 - ball_serving_t1.width
            posXBallLeftT2 = posXBallRightT1
            updateDisplay(false)
        }

        viewCreated = true
//        updateDisplay(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_score_p1.setOnClickListener { score(Team.TEAM1) }
        button_score_p2.setOnClickListener { score(Team.TEAM2) }

        if (!mainActivity.controller.matchAdded) {
            mainActivity.newMatch()
        }

        updateDisplay(false)
    }

    private fun score(team: Team) {
        mainActivity.controller.score(team)
        if (mainActivity.controller.serviceChanged) {
            updateDisplay(false)
        } else {
            updateDisplay(true)
        }
        if (mainActivity.controller.match.winner != Winner.NONE) {
            button_score_p1.isEnabled = false
            button_score_p2.isEnabled = false
        }
    }

    private fun updateDisplay(animate: Boolean) {
        fun moveBall(ball: ImageView, xPos: Float) {
            if (animate) {
                ObjectAnimator.ofFloat(ball, "translationX", xPos).apply {
                    duration = BALL_ANIMATION_DURATION
                    start()
                }
            } else {
                ball.x = xPos
            }
        }

        val scores = mainActivity.controller.currentGame.getScoreStrings()
        textScoreP1.text = scores.player1
        textScoreP2.text = scores.player2

        val ballServingGreen = if (ambientMode)
            net.mqduck.deuce.common.R.drawable.ball_ambient
        else
            net.mqduck.deuce.common.R.drawable.ball_green
        val ballServingOrange = if (ambientMode)
            net.mqduck.deuce.common.R.drawable.ball_ambient
        else
            net.mqduck.deuce.common.R.drawable.ball_orange
        val ballNotservingGreen = if (ambientMode)
            net.mqduck.deuce.common.R.drawable.ball_void
        else
            net.mqduck.deuce.common.R.drawable.ball_darkgreen
        val ballNotservingOrange = if (ambientMode)
            net.mqduck.deuce.common.R.drawable.ball_void
        else
            net.mqduck.deuce.common.R.drawable.ball_darkorange

        if (mainActivity.controller.match.winner == Winner.NONE) {
            when (mainActivity.controller.serving) {
                Serving.PLAYER1_LEFT -> {
                    imageBallServingT1.setImageResource(ballServingGreen)
                    moveBall(imageBallServingT1, posXBallLeftT1)
                    imageBallServingT1.visibility = View.VISIBLE
                    imageBallServingT2.visibility = View.INVISIBLE

                    if (mainActivity.controller.match.players == Players.DOUBLES) {
                        imageBallNotservingT1.setImageResource(ballNotservingOrange)
                        moveBall(imageBallNotservingT1, posXBallRightT1)
                        imageBallNotservingT1.visibility = View.VISIBLE
                        imageBallNotservingT2.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER1_RIGHT -> {
                    imageBallServingT1.setImageResource(ballServingGreen)
                    moveBall(imageBallServingT1, posXBallRightT1)
                    imageBallServingT1.visibility = View.VISIBLE
                    imageBallServingT2.visibility = View.INVISIBLE

                    if (mainActivity.controller.match.players == Players.DOUBLES) {
                        imageBallNotservingT1.setImageResource(ballNotservingOrange)
                        moveBall(imageBallNotservingT1, posXBallLeftT1)
                        imageBallNotservingT1.visibility = View.VISIBLE
                        imageBallNotservingT2.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER2_LEFT -> {
                    imageBallServingT2.setImageResource(ballServingGreen)
                    moveBall(imageBallServingT2, posXBallLeftT2)
                    imageBallServingT2.visibility = View.VISIBLE
                    imageBallServingT1.visibility = View.INVISIBLE

                    if (mainActivity.controller.match.players == Players.DOUBLES) {
                        imageBallNotservingT2.setImageResource(ballNotservingOrange)
                        moveBall(imageBallNotservingT2, posXBallRightT2)
                        imageBallNotservingT2.visibility = View.VISIBLE
                        imageBallNotservingT1.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER2_RIGHT -> {
                    imageBallServingT2.setImageResource(ballServingGreen)
                    moveBall(imageBallServingT2, posXBallRightT2)
                    imageBallServingT2.visibility = View.VISIBLE
                    imageBallServingT1.visibility = View.INVISIBLE

                    if (mainActivity.controller.match.players == Players.DOUBLES) {
                        imageBallNotservingT2.setImageResource(ballNotservingOrange)
                        moveBall(imageBallNotservingT2, posXBallLeftT2)
                        imageBallNotservingT2.visibility = View.VISIBLE
                        imageBallNotservingT1.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER3_LEFT -> {
                    imageBallServingT1.setImageResource(ballServingOrange)
                    moveBall(imageBallServingT1, posXBallLeftT1)
                    imageBallServingT1.visibility = View.VISIBLE
                    imageBallServingT2.visibility = View.INVISIBLE

                    imageBallNotservingT1.setImageResource(ballNotservingGreen)
                    moveBall(imageBallNotservingT1, posXBallRightT1)
                    imageBallNotservingT1.visibility = View.VISIBLE
                    imageBallNotservingT2.visibility = View.INVISIBLE
                }
                Serving.PLAYER3_RIGHT -> {
                    imageBallServingT1.setImageResource(ballServingOrange)
                    moveBall(imageBallServingT1, posXBallRightT1)
                    imageBallServingT1.visibility = View.VISIBLE
                    imageBallServingT2.visibility = View.INVISIBLE

                    imageBallNotservingT1.setImageResource(ballNotservingGreen)
                    moveBall(imageBallNotservingT1, posXBallLeftT1)
                    imageBallNotservingT1.visibility = View.VISIBLE
                    imageBallNotservingT2.visibility = View.INVISIBLE
                }
                Serving.PLAYER4_LEFT -> {
                    imageBallServingT2.setImageResource(ballServingOrange)
                    moveBall(imageBallServingT2, posXBallLeftT2)
                    imageBallServingT2.visibility = View.VISIBLE
                    imageBallServingT1.visibility = View.INVISIBLE

                    imageBallNotservingT2.setImageResource(ballNotservingGreen)
                    moveBall(imageBallNotservingT2, posXBallRightT2)
                    imageBallNotservingT2.visibility = View.VISIBLE
                    imageBallNotservingT1.visibility = View.INVISIBLE
                }
                Serving.PLAYER4_RIGHT -> {
                    imageBallServingT2.setImageResource(ballServingOrange)
                    moveBall(imageBallServingT2, posXBallRightT2)
                    imageBallServingT2.visibility = View.VISIBLE
                    imageBallServingT1.visibility = View.INVISIBLE

                    imageBallNotservingT2.setImageResource(ballNotservingGreen)
                    moveBall(imageBallNotservingT2, posXBallLeftT2)
                    imageBallNotservingT2.visibility = View.VISIBLE
                    imageBallNotservingT1.visibility = View.INVISIBLE
                }
            }
        } else {
            imageBallServingT1.visibility = View.INVISIBLE
            imageBallNotservingT1.visibility = View.INVISIBLE
            imageBallServingT2.visibility = View.INVISIBLE
            imageBallNotservingT2.visibility = View.INVISIBLE
        }

        var newTextScoresMatchP1 = ""
        var newTextScoresMatchP2 = ""
        for (set in mainActivity.controller.match.sets) {
            newTextScoresMatchP1 += set.scoreP1.toString() + "  "
            newTextScoresMatchP2 += set.scoreP2.toString() + "  "
        }
        textScoresMatchP1.text = newTextScoresMatchP1.trim()
        textScoresMatchP2.text = newTextScoresMatchP2.trim()

        if (mainActivity.controller.changeover) {
            changeoverArrowDown.visibility = View.VISIBLE
            changeoverArrowUp.visibility = View.VISIBLE
            fragment_score.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        } else {
            changeoverArrowDown.visibility = View.INVISIBLE
            changeoverArrowUp.visibility = View.INVISIBLE
        }
    }

    fun undo() {
        if (mainActivity.controller.undo()) {
            image_undo.visibility = View.VISIBLE
            val fadeout = AlphaAnimation(1F, 0F)
            fadeout.duration = UNDO_ANIMATION_DURATION
            image_undo.startAnimation(fadeout)
            image_undo.postDelayed({ image_undo.visibility = View.GONE }, UNDO_ANIMATION_DURATION)
            updateDisplay(false)
        }
    }
}
