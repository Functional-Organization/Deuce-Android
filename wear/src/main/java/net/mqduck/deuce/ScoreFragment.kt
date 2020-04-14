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
import net.mqduck.deuce.common.Team

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
    var posXBallLeftT1 = 0F
    var posXBallRightT1 = 0F
    var posXBallLeftT2 = 0F
    var posXBallRightT2 = 0F
    var viewCreated = false

    val ambientMode = mainActivity.ambientMode

    companion object {
        const val UNDO_ANIMATION_DURATION = 700L
    }

    init {
        mainActivity.controller.scoreView = this
    }

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
            mainActivity.controller.redrawDisplay()
        }

        viewCreated = true
        mainActivity.controller.redrawDisplay()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_score_p1.setOnClickListener {
            mainActivity.setTheme(R.style.DeuceWear_Ambient)
            mainActivity.controller.score(Team.TEAM1)
        }
        button_score_p2.setOnClickListener { mainActivity.controller.score(Team.TEAM2) }

        if (!mainActivity.controller.matchAdded) {
            mainActivity.newMatch()
        }
    }

    fun undo() {
        if (mainActivity.controller.undo()) {
            image_undo.visibility = View.VISIBLE
            val fadeout = AlphaAnimation(1F, 0F);
            fadeout.duration = UNDO_ANIMATION_DURATION
            image_undo.startAnimation(fadeout)
            image_undo.postDelayed(Runnable {
                image_undo.visibility = View.GONE
            }, UNDO_ANIMATION_DURATION)

        }
    }

    fun doHapticChangeover() {
        fragment_score.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
}
