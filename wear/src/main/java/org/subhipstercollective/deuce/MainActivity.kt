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

class MainActivity : FragmentActivity() {
    private val setupFragment = SetupFragment()
    private val scoreFragment = ScoreFragment()

    val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*// Enables Always-on
        setAmbientEnabled()*/

        Game.init(this)

        navigation_drawer.setAdapter(navigationAdapter)
        navigation_drawer.addOnItemSelectedListener {
            fragmentManager.beginTransaction().replace(
                R.id.fragment_container, when (it) {
                    0 -> setupFragment
                    else -> scoreFragment
                }
            ).commit()
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, setupFragment).commit()
    }

    private class NavigationItem(val text: CharSequence, val drawableId: Int)

    private val navigationAdapter = object : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
        private val items = arrayOf(
            NavigationItem("Setup", R.drawable.ball_orange),
            NavigationItem("Match", R.drawable.ball_green)
        )

        override fun getItemText(pos: Int): CharSequence {
            return items[pos].text
        }

        override fun getItemDrawable(pos: Int): Drawable? {
            return getDrawable(items[pos].drawableId)
        }

        override fun getCount(): Int {
            return items.size
        }
    }
}
