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
import android.graphics.Paint
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_setup.*
import org.subhipstercollective.deucelibrary.Team
import kotlin.random.Random

class SetupFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        view.post {
            // Set text_num_sets width to largest necessary
            val paint = Paint()
            text_num_sets.width = maxOf(
                paint.measureText(getString(R.string.best_of_1)),
                paint.measureText(getString(R.string.best_of_3)),
                paint.measureText(getString(R.string.best_of_5))
            ).toInt()
        }

        text_num_sets.text = seek_num_sets.progressString

        seek_num_sets.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                text_num_sets.text = seek_num_sets.progressString
                preferences.edit().putInt("num_sets", progress).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        radio_server_me.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                preferences.edit().putInt("server", 0).apply()
            }
        }

        radio_server_opponent.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                preferences.edit().putInt("server", 1).apply()
            }
        }

        radio_server_flip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                preferences.edit().putInt("server", 2).apply()
            }
        }

        button_start.setOnClickListener {
            mainActivity.newMatch(
                seek_num_sets.numSets,
                if (radio_server_me.isChecked || (radio_server_flip.isChecked && Random.nextInt(2) == 0)) Team.TEAM1 else Team.TEAM2,
                false
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (preferences.contains("num_sets")) {
            seek_num_sets.progress = preferences.getInt("num_sets", 2)
        }
        if (preferences.contains("server")) {
            when (preferences.getInt("server", 2)) {
                0 -> radio_server_me.isChecked = true
                1 -> radio_server_opponent.isChecked = true
                2 -> radio_server_flip.isChecked = true
            }
        }
    }
}