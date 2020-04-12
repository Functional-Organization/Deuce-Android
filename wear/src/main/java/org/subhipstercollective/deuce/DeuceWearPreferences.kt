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

package org.subhipstercollective.deuce

import android.content.SharedPreferences
import org.subhipstercollective.deucelibrary.*

class DeuceWearPreferences(private val preferences: SharedPreferences) {
    companion object {
        const val PREFERENCE_NUM_SETS = "num_sets"
        const val PREFERENCE_SERVER = "server"
        const val PREFERENCE_PLAYERS = "players"
        const val PREFERENCE_OVERTIME = "overtime"
        const val PREFERENCE_CLOCK = "clock"
    }

    var players
        get() = Players.fromOrdinal(preferences.getInt(PREFERENCE_PLAYERS, DEFAULT_PLAYERS.ordinal))
        set(players) = preferences.edit().putInt(PREFERENCE_PLAYERS, players.ordinal).apply()

    var startingServer
        get() = Team.fromOrdinal(preferences.getInt(PREFERENCE_SERVER, DEFAULT_STARTING_SERVER.ordinal))
        set(startingServer) = preferences.edit().putInt(PREFERENCE_SERVER, startingServer.ordinal).apply()

    var numSets
        get() = NumSets.fromOrdinal(preferences.getInt(PREFERENCE_NUM_SETS, DEFAULT_NUM_SETS.ordinal))
        set(numSets) = preferences.edit().putInt(PREFERENCE_NUM_SETS, numSets.ordinal).apply()

    var overtime
        get() = Overtime.fromOrdinal(preferences.getInt(PREFERENCE_OVERTIME, DEFAULT_OVERTIME.ordinal))
        set(overtime) = preferences.edit().putInt(PREFERENCE_OVERTIME, overtime.ordinal).apply()

    var clock
        get() = preferences.getBoolean(PREFERENCE_CLOCK, DEFAULT_CLOCK)
        set(clock) = preferences.edit().putBoolean(PREFERENCE_CLOCK, clock).apply()
}
