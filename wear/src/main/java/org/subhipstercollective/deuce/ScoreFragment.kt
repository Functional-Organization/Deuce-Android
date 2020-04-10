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

package org.subhipstercollective.deuce

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_score.*
import org.subhipstercollective.deucelibrary.ScoreView
import org.subhipstercollective.deucelibrary.Team

class ScoreFragment(private val mainActivity: MainActivity) : Fragment(), ScoreView {
    override lateinit var buttonScoreP1: Button
    override lateinit var buttonScoreP2: Button
    override lateinit var textScoreP1: TextView
    override lateinit var textScoreP2: TextView
    override lateinit var imageBallServingT1: ImageView
    override lateinit var imageBallNotservingT1: ImageView
    override lateinit var imageBallServingT2: ImageView
    override lateinit var imageBallNotservingT2: ImageView
    override lateinit var textScoresMatchP1: TextView
    override lateinit var textScoresMatchP2: TextView
    override var posXBallLeftT1 = 0f
    override var posXBallRightT1 = 0f
    override var posXBallLeftT2 = 0f
    override var posXBallRightT2 = 0f

    override val ambientMode = mainActivity.ambientMode

    init {
        mainActivity.controller.activityScore = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

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
        } else if (!preferences.getBoolean("time", false)) {
            text_clock.visibility = View.INVISIBLE
        }

        view.post {
            posXBallRightT1 = view.width - posXBallLeftT1 - ball_serving_t1.width
            posXBallLeftT2 = posXBallRightT1
            mainActivity.controller.redrawDisplay()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_score_p1.setOnClickListener {
            mainActivity.setTheme(R.style.DeuceWear_ambient)
            mainActivity.controller.score(Team.TEAM1)
        }
        button_score_p2.setOnClickListener { mainActivity.controller.score(Team.TEAM2) }

        if (!mainActivity.controller.matchAdded) {
            mainActivity.controller.addMatch()
        }
    }

    fun undo() {
        mainActivity.controller.undo()
    }

    override fun doHapticChangeover() {
        fragment_score.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
}
