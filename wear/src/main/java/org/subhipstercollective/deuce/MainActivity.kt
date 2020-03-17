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

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
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

    private var setupFragment = SetupFragment()
    private var advancedSetupFragment = AdvancedSetupFragment()
    private var scoreFragment = ScoreFragment(this)

    //private var setupState: Fragment.SavedState? = null
    private var scoreState: Fragment.SavedState? = null

    private val fragmentManager = supportFragmentManager
    private var currentFragment = FragmentEnum.SETUP
    private var matchAdded = false
    internal var ambientMode = false
        private set

    private lateinit var mAmbientController: AmbientModeSupport.AmbientController
    private lateinit var mDetector: GestureDetectorCompat

    internal lateinit var timeFormat: DateFormat

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = MyAmbientCallback(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeFormat = android.text.format.DateFormat.getTimeFormat(this)

        Game.init(this)

        savedInstanceState?.let {
            controller.loadInstanceState(savedInstanceState.getBundle("controllerState")!!)
            if (savedInstanceState.containsKey("setupState")) {
                setupFragment.setInitialSavedState(savedInstanceState.getParcelable("setupState"))
            }
            if (savedInstanceState.containsKey("scoreState")) {
                scoreFragment.setInitialSavedState(savedInstanceState.getParcelable("scoreState"))
            }
            currentFragment = savedInstanceState.getSerializable("currentFragment") as FragmentEnum
            matchAdded = savedInstanceState.getBoolean("matchAdded")
            navigationAdapter.notifyDataSetChanged()
        }
        setupFragment.mainActivity = this

        navigation_drawer.setAdapter(navigationAdapter)
        navigation_drawer.addOnItemSelectedListener {
            switchFragment(navigationAdapter.getItemEnum(it))
        }

        mAmbientController = AmbientModeSupport.attach(this)

        mDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                event1: MotionEvent,
                event2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (currentFragment == FragmentEnum.SCORE && event1.x - event2.x >= 100 && velocityX <= -100) {
                    scoreFragment.undo()
                    return true
                }
                return false
            }
        })

        fragmentManager.beginTransaction().replace(
            R.id.fragment_container, when (currentFragment) {
                FragmentEnum.SETUP -> setupFragment
                FragmentEnum.ADVANCED_SETUP -> advancedSetupFragment
                FragmentEnum.SCORE -> scoreFragment
            }
        ).commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveCurrentFragment()
        //setupState?.let { outState.putParcelable("setupState", setupState) }
        //scoreState?.let { outState.putParcelable("scoreState", scoreState) }

        outState.putBundle("controllerState", controller.saveInstanceState())
        outState.putSerializable("currentFragment", currentFragment)
        outState.putBoolean("matchAdded", matchAdded)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return mDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev)
    }

    fun newMatch(winMinimumMatch: Int, startingServer: Team, doubles: Boolean, tiebreak: Boolean) {
        matchAdded = true
        navigationAdapter.notifyDataSetChanged()
        switchFragment(FragmentEnum.SCORE)

        controller.winMinimumMatch = winMinimumMatch
        controller.startingServer = startingServer
        controller.doubles = doubles
        controller.tiebreak = tiebreak
        if (controller.matchAdded) {
            controller.addMatch()
        }
    }

    private fun switchFragment(fragment: FragmentEnum) {
        if (fragment != currentFragment) {
            saveCurrentFragment()
            currentFragment = fragment
            navigation_drawer.setCurrentItem(navigationAdapter.getEnumPos(fragment), false)
            fragmentManager.beginTransaction().replace(
                R.id.fragment_container, when (fragment) {
                    FragmentEnum.SETUP -> setupFragment
                    FragmentEnum.ADVANCED_SETUP -> advancedSetupFragment
                    FragmentEnum.SCORE -> scoreFragment
                }
            ).commit()
        }
    }

    private fun saveCurrentFragment() {
        when (currentFragment) {
            FragmentEnum.SETUP -> {
            }//setupState = fragmentManager.saveFragmentInstanceState(setupFragment)
            FragmentEnum.ADVANCED_SETUP -> {
            }
            FragmentEnum.SCORE -> scoreState =
                fragmentManager.saveFragmentInstanceState(scoreFragment)
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
        //Log.d("foo", ambientMode.toString())
        if (ambientMode) {
            theme.applyStyle(R.style.DeuceWear_ambient, true)
        } else {
            theme.applyStyle(R.style.DeuceWear, true)
        }
        return theme
    }

    /*fun recreateFragments() {
        val newSetupFragment = SetupFragment()
        val newAdvancedSetupFragment = AdvancedSetupFragment()
        val newScoreFragment = ScoreFragment(this)

        fragmentManager.beginTransaction().replace(
            R.id.fragment_container, when (currentFragment) {
                FragmentEnum.SETUP -> newSetupFragment
                FragmentEnum.ADVANCED_SETUP -> newAdvancedSetupFragment
                FragmentEnum.SCORE -> newScoreFragment
            }
        ).commit()

        setupFragment = newSetupFragment
        advancedSetupFragment = newAdvancedSetupFragment
        scoreFragment = newScoreFragment
    }*/

    fun recreateScoreFragment() {
        val newScoreFragment = ScoreFragment(this)
        if (currentFragment == FragmentEnum.SCORE) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, newScoreFragment).commit()
        }
        scoreFragment = newScoreFragment
    }

    private enum class FragmentEnum { SETUP, ADVANCED_SETUP, SCORE }

    private class MyAmbientCallback(val mainActivity: MainActivity) : AmbientModeSupport.AmbientCallback() {
        override fun onEnterAmbient(ambientDetails: Bundle?) {
            if (mainActivity.currentFragment == FragmentEnum.SCORE) {
                mainActivity.ambientMode = true
                mainActivity.recreateScoreFragment()
            } else {
                super.onEnterAmbient(ambientDetails)
            }
        }

        override fun onExitAmbient() {
            if (mainActivity.currentFragment == FragmentEnum.SCORE) {
                mainActivity.ambientMode = false
                mainActivity.recreateScoreFragment()
            } else {
                super.onExitAmbient()
            }
        }

        override fun onUpdateAmbient() {
            if (mainActivity.currentFragment == FragmentEnum.SCORE) {
                mainActivity.scoreFragment.ambientUpdate()
            } else {
                super.onUpdateAmbient()
            }
        }
    }
}
