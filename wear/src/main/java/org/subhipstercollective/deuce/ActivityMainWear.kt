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

package org.subhipstercollective.deuce

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.subhipstercollective.deucelibrary.*

class ActivityMainWear : WearableActivity(), ActivityMain {
    override lateinit var buttonScoreP1: Button
    override lateinit var buttonScoreP2: Button
    override lateinit var textScoreP1: TextView
    override lateinit var textScoreP2: TextView
    override lateinit var imageBallP2Left: ImageView
    override lateinit var imageBallP2Right: ImageView
    override lateinit var imageBallP1Left: ImageView
    override lateinit var imageBallP1Right: ImageView
    override lateinit var textScoresMatchP1: TextView
    override lateinit var textScoresMatchP2: TextView
    override val context = this

    val controller = ControllerMain(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()

        buttonScoreP1 = button_score_p1
        buttonScoreP2 = button_score_p2
        textScoreP1 = button_score_p1
        textScoreP2 = button_score_p2
        imageBallP2Left = image_ball_top_right
        imageBallP2Right = image_ball_top_left
        imageBallP1Left = image_ball_bottom_left
        imageBallP1Right = image_ball_bottom_right
        textScoresMatchP1 = text_scores_match_p1
        textScoresMatchP2 = text_scores_match_p2

        Game.init(this)

        button_score_p1.setOnClickListener { controller.score(Player.PLAYER1) }
        button_score_p2.setOnClickListener { controller.score(Player.PLAYER2) }

        startActivityForResult(Intent(this, ActivityAddMatch::class.java), R.id.code_request_add_match)
        //controller.addMatch()

        controller.updateDisplay()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            R.id.code_request_add_match -> {
                if (data == null)
                    return
                controller.winMinimumSet = data.getIntExtra(Key.INTENT_NUM_SETS, 1)
                controller.winMarginSet = if (data.getBooleanExtra(Key.INTENT_ADVANTAGE_SET, true)) 2 else 1
                controller.serving = data.getSerializableExtra(Key.INTENT_SERVER) as Serving
                controller.addMatch(3)
            }
        }
    }
}
