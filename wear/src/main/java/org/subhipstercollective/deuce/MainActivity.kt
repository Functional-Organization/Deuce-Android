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

package org.subhipstercollective.deuce

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.wearable.input.WearableButtons
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.activity_main.*
import org.subhipstercollective.deucelibrary.Game
import org.subhipstercollective.deucelibrary.ScoreController
import org.subhipstercollective.deucelibrary.Team
import java.text.DateFormat

class MainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    internal val controller = ScoreController()

    private var setupFragment = SetupFragment(this)
    private var advancedSetupFragment = AdvancedSetupFragment(this)
    private var scoreFragment = ScoreFragment(this)

    private val fragmentManager = supportFragmentManager
    private var currentFragment = FragmentEnum.SETUP
    private var matchAdded = false
    internal var ambientMode = false
        private set

    private lateinit var ambientController: AmbientModeSupport.AmbientController
    private var gestureDetector: GestureDetectorCompat? = null
    private var undoButton: Int? = null

    internal lateinit var timeFormat: DateFormat

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = DeuceAmbientCallback(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeFormat = android.text.format.DateFormat.getTimeFormat(this)

        Game.init(this)

        var fragment = FragmentEnum.SETUP

        savedInstanceState?.let {
            controller.loadInstanceState(savedInstanceState.getBundle("controllerState")!!)
            if (savedInstanceState.containsKey("setupState")) {
                setupFragment.setInitialSavedState(savedInstanceState.getParcelable("setupState"))
            }
            if (savedInstanceState.containsKey("scoreState")) {
                scoreFragment.setInitialSavedState(savedInstanceState.getParcelable("scoreState"))
            }
            fragment = savedInstanceState.getSerializable("currentFragment") as FragmentEnum
            matchAdded = savedInstanceState.getBoolean("matchAdded")
            navigationAdapter.notifyDataSetChanged()
        }

        navigation_drawer.setAdapter(navigationAdapter)
        navigation_drawer.addOnItemSelectedListener {
            switchFragment(navigationAdapter.getItemEnum(it))
        }

        ambientController = AmbientModeSupport.attach(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && WearableButtons.getButtonCount(this) > 0) {
            undoButton = KeyEvent.KEYCODE_STEM_1
        } else {
            gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    event1: MotionEvent,
                    event2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (currentFragment == FragmentEnum.SCORE && event1.x - event2.x >= 100 && velocityX <= -100) {
                        scoreFragment.undo()
                        activity_main.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        return true
                    }
                    return false
                }
            })
        }

        switchFragment(fragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBundle("controllerState", controller.saveInstanceState())
        outState.putSerializable("currentFragment", currentFragment)
        outState.putBoolean("matchAdded", matchAdded)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == undoButton && currentFragment == FragmentEnum.SCORE) {
            scoreFragment.undo()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val detector = gestureDetector
        return if (detector == null)
            super.dispatchTouchEvent(ev)
        else
            detector.onTouchEvent(ev) || super.dispatchTouchEvent(ev)
    }

    fun newMatch(numSets: Int, startingServer: Team, doubles: Boolean, tiebreak: Boolean) {
        matchAdded = true
        navigationAdapter.notifyDataSetChanged()
        switchFragment(FragmentEnum.SCORE)

        controller.winMinimumMatch = (numSets shr 1) + 1
        controller.startingServer = startingServer
        controller.doubles = doubles
        controller.tiebreak = tiebreak
        if (controller.matchAdded) {
            controller.addMatch()
        }
    }

    private class NavigationItem(
        val text: CharSequence,
        val drawableId: Int,
        val enum: FragmentEnum
    )

    private val navigationAdapter =
        object : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
            private val items = arrayOf(
                NavigationItem("Setup", R.drawable.ball_orange, FragmentEnum.SETUP),
                NavigationItem(
                    "Advanced Setup",
                    R.drawable.ball_darkorange,
                    FragmentEnum.ADVANCED_SETUP
                ),
                NavigationItem("Match", R.drawable.ball_green, FragmentEnum.SCORE)
            )

            override fun getItemText(pos: Int): CharSequence {
                return items[pos].text
            }

            override fun getItemDrawable(pos: Int): Drawable? {
                return getDrawable(items[pos].drawableId)
            }

            override fun getCount(): Int {
                return if (matchAdded) items.size else items.size - 1
            }

            fun getItemEnum(pos: Int): FragmentEnum {
                return items[pos].enum
            }

            fun getEnumPos(enum: FragmentEnum): Int {
                for (i in items.indices) {
                    if (items[i].enum == enum) {
                        return i
                    }
                }
                return -1
            }
        }

    override fun getTheme(): Resources.Theme {
        //return super.getTheme()
        val theme = super.getTheme()
        if (ambientMode) {
            theme.applyStyle(R.style.DeuceWear_ambient, true)
        } else {
            theme.applyStyle(R.style.DeuceWear, true)
        }
        return theme
    }

    private fun switchFragment(fragment: FragmentEnum) {
        if (fragment != currentFragment) {
            currentFragment = fragment
            navigation_drawer.setCurrentItem(navigationAdapter.getEnumPos(fragment), false)
        }

        when (fragment) {
            FragmentEnum.SETUP -> {
                if (setupFragment.ambientMode != ambientMode) {
                    setupFragment = SetupFragment(this)
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_container, setupFragment).commit()
            }
            FragmentEnum.ADVANCED_SETUP -> {
                if (advancedSetupFragment.ambientMode != ambientMode) {
                    advancedSetupFragment = AdvancedSetupFragment(this)
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_container, advancedSetupFragment).commit()
            }
            FragmentEnum.SCORE -> {
                if (scoreFragment.ambientMode != ambientMode) {
                    scoreFragment = ScoreFragment(this)
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_container, scoreFragment).commit()
            }
        }
    }

    private enum class FragmentEnum { SETUP, ADVANCED_SETUP, SCORE }

    private class DeuceAmbientCallback(val mainActivity: MainActivity) : AmbientModeSupport.AmbientCallback() {
        override fun onEnterAmbient(ambientDetails: Bundle?) {
            mainActivity.ambientMode = true
            mainActivity.switchFragment(mainActivity.currentFragment)
        }

        override fun onExitAmbient() {
            mainActivity.ambientMode = false
            mainActivity.switchFragment(mainActivity.currentFragment)
        }

        override fun onUpdateAmbient() {}
    }
}
