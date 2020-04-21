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

import android.content.SharedPreferences
import net.mqduck.deuce.common.*

class DeuceWearPreferences(private val preferences: SharedPreferences) {
    companion object {
        const val KEY_NUM_SETS = "num_sets"
        const val KEY_SERVER = "server"
        const val KEY_PLAYERS = "players"
        const val KEY_OVERTIME_RULE = "overtime"
        const val KEY_CLOCK = "clock"
    }

    var players
        get() = MatchType.fromOrdinal(preferences.getInt(KEY_PLAYERS, DEFAULT_PLAYERS.ordinal))
        set(players) = preferences.edit().putInt(KEY_PLAYERS, players.ordinal).apply()

    var startingServer
        get() = Team.fromOrdinal(preferences.getInt(KEY_SERVER, DEFAULT_STARTING_SERVER.ordinal))
        set(startingServer) = preferences.edit().putInt(KEY_SERVER, startingServer.ordinal).apply()

    var numSets
        get() = NumSets.fromOrdinal(preferences.getInt(KEY_NUM_SETS, DEFAULT_NUM_SETS.ordinal))
        set(numSets) = preferences.edit().putInt(KEY_NUM_SETS, numSets.ordinal).apply()

    var overtime
        get() = OvertimeRule.fromOrdinal(preferences.getInt(KEY_OVERTIME_RULE, DEFAULT_OVERTIME_RULE.ordinal))
        set(overtime) = preferences.edit().putInt(KEY_OVERTIME_RULE, overtime.ordinal).apply()

    var clock
        get() = preferences.getBoolean(KEY_CLOCK, DEFAULT_CLOCK)
        set(clock) = preferences.edit().putBoolean(KEY_CLOCK, clock).apply()
}
