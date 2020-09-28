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
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_score.*
import net.mqduck.deuce.common.*

class ScoreFragment : Fragment() {
    companion object {
        const val BALL_ANIMATION_DURATION = 250L
        const val UNDO_ANIMATION_DURATION = 700L
        val GAME_SCORE_Y_TRANSLATION_WITHOUT_NAME: Float
        val GAME_SCORE_Y_TRANSLATION_WITH_NAME: Float
        val SETS_SCORES_Y_TRANSLATION_WITHOUT_NAME: Float
        val SETS_SCORES_Y_TRANSLATION_WITH_NAME: Float

        init {
            val displayDensity = Resources.getSystem().displayMetrics.density
            GAME_SCORE_Y_TRANSLATION_WITHOUT_NAME = -10F * displayDensity
            GAME_SCORE_Y_TRANSLATION_WITH_NAME = -5F * displayDensity
            SETS_SCORES_Y_TRANSLATION_WITHOUT_NAME = 10F * displayDensity
            SETS_SCORES_Y_TRANSLATION_WITH_NAME = 3F * displayDensity
        }
    }

    private var posXBallLeftT1 = 0F
    private var posXBallRightT1 = 0F
    private var posXBallLeftT2 = 0F
    private var posXBallRightT2 = 0F

    val inAmbientMode = mainActivity.inAmbientMode

    private val ballServingGreen: Int
    private val ballServingOrange: Int
    private val ballNotservingGreen: Int
    private val ballNotservingOrange: Int

    init {
        if (inAmbientMode) {
            ballServingGreen = R.drawable.ball_ambient
            ballServingOrange = R.drawable.ball_ambient
            ballNotservingGreen = R.drawable.ball_void
            ballNotservingOrange = R.drawable.ball_void
        } else {
            ballServingGreen = R.drawable.ball_green
            ballServingOrange = R.drawable.ball_orange
            ballNotservingGreen = R.drawable.ball_darkgreen
            ballNotservingOrange = R.drawable.ball_darkorange
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        posXBallLeftT1 = ball_notserving_t1.x
        posXBallRightT2 = posXBallLeftT1

        if (mainActivity.inAmbientMode) {
            text_scores_match_p1.setTextColor(Color.WHITE)
            text_scores_match_p2.setTextColor(Color.WHITE)
            text_clock.setTextColor(Color.WHITE)
            text_clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36F)

            text_clock.paint.isAntiAlias = false
            text_scores_match_p1.paint.isAntiAlias = false
            text_scores_match_p2.paint.isAntiAlias = false
            text_score_game_p1.paint.isAntiAlias = false
            text_score_game_p2.paint.isAntiAlias = false

            button_score_p1.isEnabled = false
            button_score_p2.isEnabled = false

            trophy_t1.setImageResource(R.drawable.trophy_ambient)
            trophy_t2.setImageResource(R.drawable.trophy_ambient)
        } else {
            if (!mainActivity.preferences.showClock) {
                text_clock.visibility = View.INVISIBLE
            }

            button_score_p1.setOnClickListener { score(Team.TEAM1) }
            button_score_p2.setOnClickListener { score(Team.TEAM2) }
        }

        updateTeamNames()

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
        if (mainActivity.currentMatch.winner != TeamOrNone.NONE) {
            button_score_p1.isEnabled = false
            button_score_p2.isEnabled = false
        }

        if (winners.game != TeamOrNone.NONE) {
            if (winners.match != TeamOrNone.NONE) {
                mainActivity.matchList.add(mainActivity.currentMatch)
                mainActivity.matchList.writeToFile()
                //mainActivity.syncMatchList(true)
                mainActivity.syncMatches()
            } else {
                syncData(mainActivity.dataClient, PATH_CURRENT_MATCH, true) { dataMap ->
                    dataMap.putInt(KEY_MATCH_STATE, MatchState.ONGOING.ordinal)
                    dataMap.putLongArray(KEY_SET_END_TIMES, mainActivity.currentMatch.setEndTimes.toLongArray())
                    dataMap.putInt(KEY_SCORE_SIZE, mainActivity.currentMatch.scoreLog.size)
                    dataMap.putLongArray(KEY_SCORE_ARRAY, mainActivity.currentMatch.scoreLog.toLongArray())
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
        text_score_game_p1.text = scores.player1
        text_score_game_p2.text = scores.player2

        if (mainActivity.currentMatch.winner == TeamOrNone.NONE) {
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

            trophy_t1.visibility = View.INVISIBLE
            trophy_t2.visibility = View.INVISIBLE
        } else {
            ball_serving_t1.visibility = View.INVISIBLE
            ball_notserving_t1.visibility = View.INVISIBLE
            ball_serving_t2.visibility = View.INVISIBLE
            ball_notserving_t2.visibility = View.INVISIBLE

            if (mainActivity.currentMatch.winner == TeamOrNone.TEAM1) {
                trophy_t1.visibility = View.VISIBLE
                trophy_t2.visibility = View.INVISIBLE
            } else {
                trophy_t1.visibility = View.INVISIBLE
                trophy_t2.visibility = View.VISIBLE
            }
        }

        var textScoresMatchP1 = ""
        var textScoresMatchP2 = ""
        for (set in mainActivity.currentMatch.sets) {
            textScoresMatchP1 += set.scoreP1.toString() + "  "
            textScoresMatchP2 += set.scoreP2.toString() + "  "
        }
        text_scores_match_p1.text = textScoresMatchP1.trim()
        text_scores_match_p2.text = textScoresMatchP2.trim()

        if (mainActivity.currentMatch.changeover && mainActivity.currentMatch.winner == TeamOrNone.NONE) {
            changeover_arrow_down.visibility = View.VISIBLE
            changeover_arrow_up.visibility = View.VISIBLE
            fragment_score.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        } else {
            changeover_arrow_down.visibility = View.INVISIBLE
            changeover_arrow_up.visibility = View.INVISIBLE
        }
    }

    fun updateTeamNames() {
        if (
            mainActivity.preferences.showCustomNames
            && (mainActivity.currentMatch.nameTeam1.isNotEmpty() || mainActivity.currentMatch.nameTeam2.isNotEmpty())
        ) {
            text_name_p1.text = mainActivity.currentMatch.displayNameShortTeam1
            text_name_p2.text = mainActivity.currentMatch.displayNameShortTeam2
            text_score_game_p1.translationY = GAME_SCORE_Y_TRANSLATION_WITH_NAME
            text_score_game_p2.translationY = -GAME_SCORE_Y_TRANSLATION_WITH_NAME
            if (inAmbientMode) {
                text_name_p1.visibility = View.GONE
                text_name_p2.visibility = View.GONE
            } else {
                text_scores_match_p1.translationY = SETS_SCORES_Y_TRANSLATION_WITH_NAME
                text_scores_match_p2.translationY = -SETS_SCORES_Y_TRANSLATION_WITH_NAME
            }
        } else {
            text_name_p1.text = ""
            text_name_p2.text = ""
            text_score_game_p1.translationY = GAME_SCORE_Y_TRANSLATION_WITHOUT_NAME
            text_score_game_p2.translationY = -GAME_SCORE_Y_TRANSLATION_WITHOUT_NAME
            if (inAmbientMode) {
                text_name_p1.visibility = View.GONE
                text_name_p2.visibility = View.GONE
            } else {
                text_scores_match_p1.translationY = SETS_SCORES_Y_TRANSLATION_WITHOUT_NAME
                text_scores_match_p2.translationY = -SETS_SCORES_Y_TRANSLATION_WITHOUT_NAME
            }
        }
    }
}
