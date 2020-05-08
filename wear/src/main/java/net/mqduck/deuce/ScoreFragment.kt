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
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import kotlinx.android.synthetic.main.fragment_score.*
import net.mqduck.deuce.common.*

class ScoreFragment(private val mainActivity: MainActivity) : Fragment() {
    companion object {
        const val BALL_ANIMATION_DURATION = 250L
        const val UNDO_ANIMATION_DURATION = 700L
    }

    private var posXBallLeftT1 = 0F
    private var posXBallRightT1 = 0F
    private var posXBallLeftT2 = 0F
    private var posXBallRightT2 = 0F

    val ambientMode = mainActivity.ambientMode

    private val ballServingGreen = if (ambientMode)
        net.mqduck.deuce.common.R.drawable.ball_ambient
    else
        net.mqduck.deuce.common.R.drawable.ball_green

    private val ballServingOrange = if (ambientMode)
        net.mqduck.deuce.common.R.drawable.ball_ambient
    else
        net.mqduck.deuce.common.R.drawable.ball_orange

    private val ballNotservingGreen = if (ambientMode)
        net.mqduck.deuce.common.R.drawable.ball_void
    else
        net.mqduck.deuce.common.R.drawable.ball_darkgreen

    private val ballNotservingOrange = if (ambientMode)
        net.mqduck.deuce.common.R.drawable.ball_void
    else
        net.mqduck.deuce.common.R.drawable.ball_darkorange

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        posXBallLeftT1 = ball_notserving_t1.x
        posXBallRightT2 = posXBallLeftT1

        if (mainActivity.ambientMode) {
            text_scores_match_p1.setTextColor(Color.WHITE)
            text_scores_match_p2.setTextColor(Color.WHITE)
            text_clock.setTextColor(Color.WHITE)
            text_clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36F)

            text_clock.paint.isAntiAlias = false
            text_scores_match_p1.paint.isAntiAlias = false
            text_scores_match_p2.paint.isAntiAlias = false
            button_score_p1.paint.isAntiAlias = false
            button_score_p2.paint.isAntiAlias = false

            button_score_p1.isEnabled = false
            button_score_p2.isEnabled = false
        } else {
            if (!mainActivity.preferences.clock) {
                text_clock.visibility = View.INVISIBLE
            }

            button_score_p1.setOnClickListener { score(Team.TEAM1) }
            button_score_p2.setOnClickListener { score(Team.TEAM2) }
        }

        view.post {
            posXBallRightT1 = view.width - posXBallLeftT1 - ball_serving_t1.width
            posXBallLeftT2 = posXBallRightT1
            updateDisplay(false)
        }
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_score_p1.setOnClickListener { score(Team.TEAM1) }
        button_score_p2.setOnClickListener { score(Team.TEAM2) }
    }*/

    private fun score(team: Team) {
        val winners = mainActivity.currentMatch.score(team)
        if (mainActivity.currentMatch.serviceChanged) {
            updateDisplay(false)
        } else {
            updateDisplay(true)
        }
        if (mainActivity.currentMatch.winner != Winner.NONE) {
            button_score_p1.isEnabled = false
            button_score_p2.isEnabled = false
        }

        if (winners.game != Winner.NONE) {
            if (winners.match != Winner.NONE) {
                mainActivity.matchList.add(mainActivity.currentMatch)
                mainActivity.matchList.writeToFile()
                mainActivity.syncMatchList(true)
            } else {
                val putDataRequest: PutDataRequest = PutDataMapRequest.create(PATH_CURRENT_MATCH).run {
                    dataMap.putInt(KEY_MATCH_STATE, MatchState.ONGOING.ordinal)
                    dataMap.putLong(KEY_MATCH_END_TIME, mainActivity.currentMatch.playTimes.endTime)
                    dataMap.putLongArray(
                        KEY_SETS_START_TIMES,
                        mainActivity.currentMatch.setsTimesLog.startTimes.toLongArray()
                    )
                    dataMap.putLongArray(
                        KEY_SETS_END_TIMES,
                        mainActivity.currentMatch.setsTimesLog.endTimes.toLongArray()
                    )
                    dataMap.putInt(KEY_SCORE_SIZE, mainActivity.currentMatch.scoreLogSize())
                    dataMap.putLongArray(KEY_SCORE_ARRAY, mainActivity.currentMatch.scoreLogArray())
                    asPutDataRequest()
                }
                putDataRequest.setUrgent()
                val putDataTask: Task<DataItem> = mainActivity.dataClient.putDataItem(putDataRequest)
                putDataTask.addOnSuccessListener {
                    //Log.d("foo", "update match success")
                }
            }
        }
    }

    internal fun updateDisplay(animate: Boolean) {
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

        val scores = mainActivity.currentMatch.currentGame.getScoreStrings()
        button_score_p1.text = scores.player1
        button_score_p2.text = scores.player2

        if (mainActivity.currentMatch.winner == Winner.NONE) {
            when (mainActivity.currentMatch.serving) {
                Serving.PLAYER1_LEFT -> {
                    ball_serving_t1.setImageResource(ballServingGreen)
                    moveBall(ball_serving_t1, posXBallLeftT1)
                    ball_serving_t1.visibility = View.VISIBLE
                    ball_serving_t2.visibility = View.INVISIBLE

                    if (mainActivity.currentMatch.matchType == MatchType.DOUBLES) {
                        ball_notserving_t1.setImageResource(ballNotservingOrange)
                        moveBall(ball_notserving_t1, posXBallRightT1)
                        ball_notserving_t1.visibility = View.VISIBLE
                        ball_notserving_t2.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER1_RIGHT -> {
                    ball_serving_t1.setImageResource(ballServingGreen)
                    moveBall(ball_serving_t1, posXBallRightT1)
                    ball_serving_t1.visibility = View.VISIBLE
                    ball_serving_t2.visibility = View.INVISIBLE

                    if (mainActivity.currentMatch.matchType == MatchType.DOUBLES) {
                        ball_notserving_t1.setImageResource(ballNotservingOrange)
                        moveBall(ball_notserving_t1, posXBallLeftT1)
                        ball_notserving_t1.visibility = View.VISIBLE
                        ball_notserving_t2.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER2_LEFT -> {
                    ball_serving_t2.setImageResource(ballServingGreen)
                    moveBall(ball_serving_t2, posXBallLeftT2)
                    ball_serving_t2.visibility = View.VISIBLE
                    ball_serving_t1.visibility = View.INVISIBLE

                    if (mainActivity.currentMatch.matchType == MatchType.DOUBLES) {
                        ball_notserving_t2.setImageResource(ballNotservingOrange)
                        moveBall(ball_notserving_t2, posXBallRightT2)
                        ball_notserving_t2.visibility = View.VISIBLE
                        ball_notserving_t1.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER2_RIGHT -> {
                    ball_serving_t2.setImageResource(ballServingGreen)
                    moveBall(ball_serving_t2, posXBallRightT2)
                    ball_serving_t2.visibility = View.VISIBLE
                    ball_serving_t1.visibility = View.INVISIBLE

                    if (mainActivity.currentMatch.matchType == MatchType.DOUBLES) {
                        ball_notserving_t2.setImageResource(ballNotservingOrange)
                        moveBall(ball_notserving_t2, posXBallLeftT2)
                        ball_notserving_t2.visibility = View.VISIBLE
                        ball_notserving_t1.visibility = View.INVISIBLE
                    }
                }
                Serving.PLAYER3_LEFT -> {
                    ball_serving_t1.setImageResource(ballServingOrange)
                    moveBall(ball_serving_t1, posXBallLeftT1)
                    ball_serving_t1.visibility = View.VISIBLE
                    ball_serving_t2.visibility = View.INVISIBLE

                    ball_notserving_t1.setImageResource(ballNotservingGreen)
                    moveBall(ball_notserving_t1, posXBallRightT1)
                    ball_notserving_t1.visibility = View.VISIBLE
                    ball_notserving_t2.visibility = View.INVISIBLE
                }
                Serving.PLAYER3_RIGHT -> {
                    ball_serving_t1.setImageResource(ballServingOrange)
                    moveBall(ball_serving_t1, posXBallRightT1)
                    ball_serving_t1.visibility = View.VISIBLE
                    ball_serving_t2.visibility = View.INVISIBLE

                    ball_notserving_t1.setImageResource(ballNotservingGreen)
                    moveBall(ball_notserving_t1, posXBallLeftT1)
                    ball_notserving_t1.visibility = View.VISIBLE
                    ball_notserving_t2.visibility = View.INVISIBLE
                }
                Serving.PLAYER4_LEFT -> {
                    ball_serving_t2.setImageResource(ballServingOrange)
                    moveBall(ball_serving_t2, posXBallLeftT2)
                    ball_serving_t2.visibility = View.VISIBLE
                    ball_serving_t1.visibility = View.INVISIBLE

                    ball_notserving_t2.setImageResource(ballNotservingGreen)
                    moveBall(ball_notserving_t2, posXBallRightT2)
                    ball_notserving_t2.visibility = View.VISIBLE
                    ball_notserving_t1.visibility = View.INVISIBLE
                }
                Serving.PLAYER4_RIGHT -> {
                    ball_serving_t2.setImageResource(ballServingOrange)
                    moveBall(ball_serving_t2, posXBallRightT2)
                    ball_serving_t2.visibility = View.VISIBLE
                    ball_serving_t1.visibility = View.INVISIBLE

                    ball_notserving_t2.setImageResource(ballNotservingGreen)
                    moveBall(ball_notserving_t2, posXBallLeftT2)
                    ball_notserving_t2.visibility = View.VISIBLE
                    ball_notserving_t1.visibility = View.INVISIBLE
                }
            }
        } else {
            ball_serving_t1.visibility = View.INVISIBLE
            ball_notserving_t1.visibility = View.INVISIBLE
            ball_serving_t2.visibility = View.INVISIBLE
            ball_notserving_t2.visibility = View.INVISIBLE
        }

        var textScoresMatchP1 = ""
        var textScoresMatchP2 = ""
        for (set in mainActivity.currentMatch.sets) {
            textScoresMatchP1 += set.scoreP1.toString() + "  "
            textScoresMatchP2 += set.scoreP2.toString() + "  "
        }
        text_scores_match_p1.text = textScoresMatchP1.trim()
        text_scores_match_p2.text = textScoresMatchP2.trim()

        if (mainActivity.currentMatch.changeover) {
            changeover_arrow_down.visibility = View.VISIBLE
            changeover_arrow_up.visibility = View.VISIBLE
            fragment_score.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        } else {
            changeover_arrow_down.visibility = View.INVISIBLE
            changeover_arrow_up.visibility = View.INVISIBLE
        }
    }
}
