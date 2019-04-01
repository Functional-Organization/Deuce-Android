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

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.activity_main.*
import org.subhipstercollective.deucelibrary.Game
import org.subhipstercollective.deucelibrary.Team

class MainActivity : FragmentActivity() {
    private val setupFragment = SetupFragment()
    private val scoreFragment = ScoreFragment()
    private var setupState: Fragment.SavedState? = null
    private var scoreState: Fragment.SavedState? = null
    private val fragmentManager = supportFragmentManager
    private var currentFragment = FragmentEnum.SETUP
    private var matchAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*// Enables Always-on
        setAmbientEnabled()*/

        Game.init(this)

        savedInstanceState?.let {
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

        fragmentManager.beginTransaction().replace(
            R.id.fragment_container, when (currentFragment) {
                FragmentEnum.SETUP -> setupFragment
                FragmentEnum.SCORE -> scoreFragment
            }
        ).commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveCurrentFragment()
        //setupState?.let { outState.putParcelable("setupState", setupState) }
        scoreState?.let { outState.putParcelable("scoreState", scoreState) }

        outState.putSerializable("currentFragment", currentFragment)
        outState.putBoolean("matchAdded", matchAdded)
    }

    fun newMatch(winMinimumMatch: Int, startingServer: Team, tiebreak: Boolean) {
        matchAdded = true
        navigationAdapter.notifyDataSetChanged()
        switchFragment(FragmentEnum.SCORE)
        scoreFragment.newMatch(winMinimumMatch, startingServer, tiebreak)
    }

    private fun switchFragment(fragment: FragmentEnum) {
        if (fragment != currentFragment) {
            saveCurrentFragment()
            currentFragment = fragment
            navigation_drawer.setCurrentItem(navigationAdapter.getEnumPos(fragment), false)
            fragmentManager.beginTransaction().replace(
                R.id.fragment_container, when (fragment) {
                    FragmentEnum.SETUP -> setupFragment
                    FragmentEnum.SCORE -> scoreFragment
                }
            ).commit()
        }
    }

    private fun saveCurrentFragment() {
        when (currentFragment) {
            FragmentEnum.SETUP -> {
            }//setupState = fragmentManager.saveFragmentInstanceState(setupFragment)
            FragmentEnum.SCORE -> scoreState = fragmentManager.saveFragmentInstanceState(scoreFragment)
        }
    }

    private class NavigationItem(val text: CharSequence, val drawableId: Int, val enum: FragmentEnum)

    private val navigationAdapter = object : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
        private val items = arrayOf(
            NavigationItem("Setup", R.drawable.ball_orange, FragmentEnum.SETUP),
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
            for (i in 0 until items.size) {
                if (items[i].enum == enum) {
                    return i
                }
            }
            return -1
        }
    }

    private enum class FragmentEnum { SETUP, SCORE }
}
