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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_advanced_setup.*

class AdvancedSetupFragment(val mainActivity: MainActivity) : Fragment() {
    val ambientMode = mainActivity.ambientMode

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_advanced_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radio_clock_hide.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.showClock = false
            }
        }
        radio_clock_show.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.showClock = true
            }
        }
        radio_names_hide.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.showCustomNames = false
            }
        }
        radio_names_show.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.showCustomNames = true
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (mainActivity.preferences.showClock) {
            radio_clock_show.isChecked = true
        } else {
            radio_clock_hide.isChecked = true
        }

        if (mainActivity.preferences.showCustomNames) {
            radio_names_show.isChecked = true
        } else {
            radio_names_hide.isChecked = true
        }
    }
}
