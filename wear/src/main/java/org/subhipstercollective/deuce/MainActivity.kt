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
import androidx.fragment.app.FragmentActivity
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.activity_main.*
import org.subhipstercollective.deucelibrary.Game
import org.subhipstercollective.deucelibrary.Team

class MainActivity : FragmentActivity() {
    private val setupFragment = SetupFragment()
    private val scoreFragment = ScoreFragment()

    val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setupFragment.mainActivity = this

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*// Enables Always-on
        setAmbientEnabled()*/

        Game.init(this)

        navigation_drawer.setAdapter(navigationAdapter)
        navigation_drawer.addOnItemSelectedListener {
            fragmentManager.beginTransaction().replace(
                R.id.fragment_container, when (if (scoreFragment.setup) it else it + 1) {
                    0 -> scoreFragment
                    else -> setupFragment
                }
            ).commit()
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, setupFragment).commit()
    }

    private class NavigationItem(val text: CharSequence, val drawableId: Int)

    private val navigationAdapter = object : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
        private val items = arrayOf(
            NavigationItem("Match", R.drawable.ball_green),
            NavigationItem("Setup", R.drawable.ball_orange)
        )

        override fun getItemText(pos: Int): CharSequence {
            return items[if (scoreFragment.setup) pos else pos + 1].text
        }

        override fun getItemDrawable(pos: Int): Drawable? {
            return getDrawable(items[if (scoreFragment.setup) pos else pos + 1].drawableId)
        }

        override fun getCount(): Int {
            return if (scoreFragment.setup) items.size else items.size - 1
        }
    }

    fun newMatch(winMinimumMatch: Int, startingServer: Team, tiebreak: Boolean) {
        scoreFragment.winMinimumMatch = winMinimumMatch
        scoreFragment.startingServer = startingServer
        scoreFragment.tiebreak = tiebreak
        scoreFragment.newMatch()
        fragmentManager.beginTransaction().replace(R.id.fragment_container, scoreFragment).commit()
        navigationAdapter.notifyDataSetChanged()
    }
}
