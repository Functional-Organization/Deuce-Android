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
    private var currentFragment = MainFragment.SETUP
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
            currentFragment = savedInstanceState.getSerializable("currentFragment") as MainFragment
            matchAdded = savedInstanceState.getBoolean("matchAdded")
            navigationAdapter.notifyDataSetChanged()
        }
        setupFragment.mainActivity = this

        navigation_drawer.setAdapter(navigationAdapter)
        navigation_drawer.addOnItemSelectedListener {
            switchFragment(
                when (if (matchAdded) it else it + 1) {
                    0 -> MainFragment.SCORE
                    else -> MainFragment.SETUP
                }
            )
        }

        fragmentManager.beginTransaction().replace(
            R.id.fragment_container, when (currentFragment) {
                MainFragment.SETUP -> setupFragment
                MainFragment.SCORE -> scoreFragment
            }
        ).commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveCurrentFragment()
        setupState?.let { outState.putParcelable("setupState", setupState) }
        scoreState?.let { outState.putParcelable("scoreState", scoreState) }

        outState.putSerializable("currentFragment", currentFragment)
        outState.putBoolean("matchAdded", matchAdded)
    }

    private class NavigationItem(val text: CharSequence, val drawableId: Int)

    private val navigationAdapter = object : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
        private val items = arrayOf(
            NavigationItem("Match", R.drawable.ball_green),
            NavigationItem("Setup", R.drawable.ball_orange)
        )

        override fun getItemText(pos: Int): CharSequence {
            return items[if (matchAdded) pos else pos + 1].text
        }

        override fun getItemDrawable(pos: Int): Drawable? {
            return getDrawable(items[if (matchAdded) pos else pos + 1].drawableId)
        }

        override fun getCount(): Int {
            return if (matchAdded) items.size else items.size - 1
        }
    }

    fun newMatch(winMinimumMatch: Int, startingServer: Team, tiebreak: Boolean) {
        switchFragment(MainFragment.SCORE)
        scoreFragment.newMatch(winMinimumMatch, startingServer, tiebreak)
        matchAdded = true
        navigationAdapter.notifyDataSetChanged()
    }

    private fun switchFragment(fragment: MainFragment) {
        if (fragment != currentFragment) {
            saveCurrentFragment()
            currentFragment = fragment
            when (fragment) {
                MainFragment.SETUP -> fragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    setupFragment
                ).commit()
                MainFragment.SCORE -> fragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    scoreFragment
                ).commit()
            }
        }
    }

    private fun saveCurrentFragment() {
        when (currentFragment) {
            MainFragment.SETUP -> setupState = fragmentManager.saveFragmentInstanceState(setupFragment)
            MainFragment.SCORE -> scoreState = fragmentManager.saveFragmentInstanceState(scoreFragment)
        }
    }

    private enum class MainFragment { SETUP, SCORE }
}
