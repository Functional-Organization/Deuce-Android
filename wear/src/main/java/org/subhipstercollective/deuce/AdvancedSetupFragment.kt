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

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_advanced_setup.*

class AdvancedSetupFragment : Fragment() {
    lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_advanced_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        updateSwitchDoublesText()
        updateSwitchAdvantageText()
        updateSwitchTime()

        switch_doubles.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchDoublesText()
            preferences.edit().putBoolean("doubles", isChecked).apply()
        }
        switch_advantage.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchAdvantageText()
            preferences.edit().putBoolean("advantage", isChecked).apply()
        }
        switch_time.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchTime()
            preferences.edit().putBoolean("time", isChecked).apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        switch_doubles.isChecked = preferences.getBoolean("doubles", false)
        switch_advantage.isChecked = preferences.getBoolean("advantage", false)
        switch_time.isChecked = preferences.getBoolean("time", false)
    }

    private fun updateSwitchDoublesText() {
        switch_doubles.text = if (switch_doubles.isChecked) "Doubles" else "Singles"
    }

    private fun updateSwitchAdvantageText() {
        switch_advantage.text = if (switch_advantage.isChecked) "Advantage set" else "Tiebreak set"
    }

    private fun updateSwitchTime() {
        switch_time.text = if (switch_time.isChecked) "Show time" else "Don't show time"
    }
}
