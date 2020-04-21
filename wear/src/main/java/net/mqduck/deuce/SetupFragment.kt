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
import kotlinx.android.synthetic.main.fragment_setup.*
import net.mqduck.deuce.common.MatchType
import net.mqduck.deuce.common.NumSets
import net.mqduck.deuce.common.Team
import kotlin.random.Random

class SetupFragment(private val mainActivity: MainActivity) : Fragment() {
    val ambientMode = mainActivity.ambientMode

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mainActivity.ambientMode) {
            button_start.paint.isAntiAlias = false
            text_players.paint.isAntiAlias = false
            radio_singles.paint.isAntiAlias = false
            radio_doubles.paint.isAntiAlias = false
            text_starting_server.paint.isAntiAlias = false
            radio_server_me.paint.isAntiAlias = false
            radio_server_opponent.paint.isAntiAlias = false
            button_flip_coin.paint.isAntiAlias = false
            text_num_sets.paint.isAntiAlias = false
            radio_best_of_1.paint.isAntiAlias = false
            radio_best_of_3.paint.isAntiAlias = false
            radio_best_of_5.paint.isAntiAlias = false
        }

        radio_singles.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.players = MatchType.SINGLES
            }
        }
        radio_doubles.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.players = MatchType.DOUBLES
            }
        }

        radio_server_me.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.startingServer = Team.TEAM1
            }
        }
        radio_server_opponent.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.startingServer = Team.TEAM2
            }
        }
        button_flip_coin.setOnClickListener {
            if (Random.nextBoolean()) {
                radio_server_opponent.isChecked = true
                radio_server_me.isChecked = true
            } else {
                radio_server_me.isChecked = true
                radio_server_opponent.isChecked = true
            }
        }

        radio_best_of_1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.numSets = NumSets.ONE
            }
        }
        radio_best_of_3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.numSets = NumSets.THREE
            }
        }
        radio_best_of_5.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.numSets = NumSets.FIVE
            }
        }
        radio_best_of_infinite.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainActivity.preferences.numSets = NumSets.INFINITE
            }
        }

        button_start.setOnClickListener {
            mainActivity.newMatch()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (mainActivity.preferences.players == MatchType.SINGLES) {
            radio_singles.isChecked = true
        } else {
            radio_doubles.isChecked = true
        }

        when (mainActivity.preferences.startingServer) {
            Team.TEAM1 -> radio_server_me.isChecked = true
            Team.TEAM2 -> radio_server_opponent.isChecked = true
        }

        when (mainActivity.preferences.numSets) {
            NumSets.ONE -> radio_best_of_1.isChecked = true
            NumSets.THREE -> radio_best_of_3.isChecked = true
            NumSets.FIVE -> radio_best_of_5.isChecked = true
            NumSets.INFINITE -> radio_best_of_infinite.isChecked = true
        }
    }
}
