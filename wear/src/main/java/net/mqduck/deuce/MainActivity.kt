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

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.os.Bundle
import android.support.wearable.input.WearableButtons
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.activity_main.*
import net.mqduck.deuce.common.*

class MainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private enum class FragmentEnum { SETUP, ADVANCED_SETUP, SCORE }

    //TODO: Find a way to disable anti-aliasing on ambient images
    private enum class NavigationItemList(val list: Array<NavigationItem>) {
        NAVIGATION_ITEMS_WITHOUT_MATCH(
            arrayOf(
                NavigationItem.NAVIGATION_ITEM_SETUP,
                NavigationItem.NAVIGATION_ITEM_ADVANCED_SETUP
            )
        ),

        NAVIGATION_ITEMS_WITH_MATCH(
            arrayOf(
                NavigationItem.NAVIGATION_ITEM_MATCH,
                NavigationItem.NAVIGATION_ITEM_SETUP,
                NavigationItem.NAVIGATION_ITEM_ADVANCED_SETUP
            )
        ),

        NAVIGATION_ITEMS_WITHOUT_MATCH_AMBIENT(
            arrayOf(
                NavigationItem.NAVIGATION_ITEM_SETUP_AMBIENT,
                NavigationItem.NAVIGATION_ITEM_ADVANCED_SETUP_AMBIENT
            )
        ),

        NAVIGATION_ITEMS_WITH_MATCH_AMBIENT(
            arrayOf(
                NavigationItem.NAVIGATION_ITEM_MATCH_AMBIENT,
                NavigationItem.NAVIGATION_ITEM_SETUP_AMBIENT,
                NavigationItem.NAVIGATION_ITEM_ADVANCED_SETUP_AMBIENT
            )
        );

        internal enum class NavigationItem(val text: CharSequence, val drawableId: Int, val enum: FragmentEnum) {
            NAVIGATION_ITEM_MATCH(
                "Match",
                R.drawable.match,
                FragmentEnum.SCORE
            ),

            NAVIGATION_ITEM_SETUP(
                "Match Setup",
                R.drawable.setup,
                FragmentEnum.SETUP
            ),

            NAVIGATION_ITEM_ADVANCED_SETUP(
                "Advanced Setup",
                R.drawable.advanced_setup,
                FragmentEnum.ADVANCED_SETUP
            ),

            NAVIGATION_ITEM_MATCH_AMBIENT(
                "Match",
                R.drawable.match_ambient,
                FragmentEnum.SCORE
            ),

            NAVIGATION_ITEM_SETUP_AMBIENT(
                "Match Setup",
                R.drawable.setup_ambient,
                FragmentEnum.SETUP
            ),

            NAVIGATION_ITEM_ADVANCED_SETUP_AMBIENT(
                "Advanced Setup",
                R.drawable.advanced_setup_ambient,
                FragmentEnum.ADVANCED_SETUP
            )
        }
    }

    private class DeuceAmbientCallback(val mainActivity: MainActivity) : AmbientModeSupport.AmbientCallback() {
        override fun onEnterAmbient(ambientDetails: Bundle?) {
            mainActivity.ambientMode = true
            mainActivity.switchFragment(mainActivity.currentFragment)

            mainActivity.navigationDrawer.background.setTint(mainActivity.getColor(R.color.black))
            mainActivity.navigationAdapter.update()
        }

        override fun onExitAmbient() {
            mainActivity.ambientMode = false
            mainActivity.switchFragment(mainActivity.currentFragment)

            mainActivity.navigationDrawer.background.setTint(mainActivity.getColor(R.color.lighter_bg_1))
            mainActivity.navigationAdapter.update()
        }

        override fun onUpdateAmbient() {}
    }

    internal var match = Match(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        Team.TEAM1,
        OvertimeRule.TIEBREAK,
        MatchType.SINGLES
    )
    internal lateinit var preferences: DeuceWearPreferences

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

    lateinit var navigationDrawer: WearableNavigationDrawerView

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = DeuceAmbientCallback(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationDrawer = navigation_drawer

        Game.init(this)

        preferences = DeuceWearPreferences(PreferenceManager.getDefaultSharedPreferences(this))

        var fragment = FragmentEnum.SETUP

        savedInstanceState?.let {
            //TODO: bundle
            //controller.loadInstanceState(savedInstanceState.getBundle("controllerState")!!)
            if (savedInstanceState.containsKey("setupState")) {
                setupFragment.setInitialSavedState(savedInstanceState.getParcelable("setupState"))
            }
            if (savedInstanceState.containsKey("scoreState")) {
                scoreFragment.setInitialSavedState(savedInstanceState.getParcelable("scoreState"))
            }
            fragment = savedInstanceState.getSerializable("currentFragment") as FragmentEnum
            matchAdded = savedInstanceState.getBoolean("matchAdded")
            if (matchAdded) {
                navigationAdapter.notifyDataSetChanged()
            }
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

        //TODO: bundle
//        outState.putBundle("controllerState", controller.saveInstanceState())
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

    fun newMatch() {
        matchAdded = true
        navigationAdapter.enableMatch()
        switchFragment(FragmentEnum.SCORE)

        match = Match(
            preferences.numSets.value,
            DEFAULT_WIN_MARGIN_MATCH,
            DEFAULT_WIN_MINIMUM_SET,
            DEFAULT_WIN_MARGIN_SET,
            DEFAULT_WIN_MINIMUM_GAME,
            DEFAULT_WIN_MARGIN_GAME,
            DEFAULT_WIN_MINIMUM_GAME_TIEBREAK,
            DEFAULT_WIN_MARGIN_GAME_TIEBREAK,
            preferences.startingServer,
            preferences.overtime,
            preferences.players
        )
    }

    private val navigationAdapter =
        object : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
            //private val items = arrayListOf(NAVIGATION_ITEM_SETUP, NAVIGATION_ITEM_ADVANCED_SETUP)
            private var items = NavigationItemList.NAVIGATION_ITEMS_WITHOUT_MATCH

            override fun getItemText(pos: Int) = items.list[pos].text

            override fun getItemDrawable(pos: Int): Drawable? /*= getDrawable(items.list[pos].drawableId)*/ {
                val foo = getDrawable(items.list[pos].drawableId) as VectorDrawable
                foo.isFilterBitmap = false
                return foo
            }

            override fun getCount() = items.list.size

            fun enableMatch() {
                // Probably impossible for this condition to be true
                items = if (ambientMode)
                    NavigationItemList.NAVIGATION_ITEMS_WITH_MATCH_AMBIENT
                else
                    NavigationItemList.NAVIGATION_ITEMS_WITH_MATCH
                update()
            }

            fun getItemEnum(pos: Int) = items.list[pos].enum

            fun getEnumPos(enum: FragmentEnum): Int {
                for (i in items.list.indices) {
                    if (items.list[i].enum == enum) {
                        return i
                    }
                }
                throw java.lang.IllegalArgumentException("Invalid navigation drawer index")
            }

            fun update() {
                items = when (items) {
                    NavigationItemList.NAVIGATION_ITEMS_WITHOUT_MATCH,
                    NavigationItemList.NAVIGATION_ITEMS_WITHOUT_MATCH_AMBIENT ->
                        if (ambientMode)
                            NavigationItemList.NAVIGATION_ITEMS_WITHOUT_MATCH_AMBIENT
                        else
                            NavigationItemList.NAVIGATION_ITEMS_WITHOUT_MATCH
                    NavigationItemList.NAVIGATION_ITEMS_WITH_MATCH,
                    NavigationItemList.NAVIGATION_ITEMS_WITH_MATCH_AMBIENT ->
                        if (ambientMode)
                            NavigationItemList.NAVIGATION_ITEMS_WITH_MATCH_AMBIENT
                        else
                            NavigationItemList.NAVIGATION_ITEMS_WITH_MATCH
                }
                notifyDataSetChanged()
            }
        }

    override fun getTheme(): Resources.Theme {
        //return super.getTheme()
        val theme = super.getTheme()
        if (ambientMode) {
            theme.applyStyle(R.style.DeuceWear_Ambient, true)
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
}
