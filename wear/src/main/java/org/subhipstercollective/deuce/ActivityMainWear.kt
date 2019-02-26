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

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
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
    //    override lateinit var imageBallP2LeftServing: ImageView
//    override lateinit var imageBallP2RightServing: ImageView
//    override lateinit var imageBallP1LeftServing: ImageView
//    override lateinit var imageBallP1RightServing: ImageView
//    override lateinit var imageBallP2LeftNotServing: ImageView
//    override lateinit var imageBallP2RightNotServing: ImageView
//    override lateinit var imageBallP1LeftNotServing: ImageView
//    override lateinit var imageBallP1RightNotServing: ImageView
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
    override val context = this

    val controller = ControllerMain(this)

    private lateinit var mDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()

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

        Game.init(this)

        mDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                Log.d("debug", "" + event2.x + ", " + event1.x + ", " + velocityX)
                if (event1.x - event2.x >= 100 && velocityX <= -100) {
                    Log.d("debug", "undoing")
                    controller.undo()
                    return true
                }
                return false
            }
        })

        button_score_p1.setOnClickListener { controller.score(Team.TEAM1) }
        button_score_p2.setOnClickListener { controller.score(Team.TEAM2) }

        startActivityForResult(Intent(this, ActivityAddMatch::class.java), R.id.code_request_add_match)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        posXBallRightT1 = layout_main.width - ball_notserving_t1.x - ball_notserving_t1.width
        posXBallLeftT2 = posXBallRightT1

        controller.updateDisplay()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            R.id.code_request_add_match -> {
                if (data == null)
                    return
                controller.winMinimumMatch = data.getIntExtra(Key.INTENT_NUM_SETS, 0)
                controller.startingServer = data.getSerializableExtra(Key.INTENT_SERVER) as Team
                controller.tiebreak = data.getBooleanExtra(Key.INTENT_ADVANTAGE_SET, true)
                controller.addMatch()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return mDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev)
    }
}
