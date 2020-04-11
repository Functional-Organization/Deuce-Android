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
import org.subhipstercollective.deucelibrary.NumSets
import org.subhipstercollective.deucelibrary.Overtime
import org.subhipstercollective.deucelibrary.Players
import org.subhipstercollective.deucelibrary.StartingServer

class DeuceWearPreferences(private val preferences: SharedPreferences) {
    companion object {
        const val PREFERENCE_NUM_SETS = "num_sets"
        const val PREFERENCE_SERVER = "server"
        const val PREFERENCE_PLAYERS = "players"
        const val PREFERENCE_OVERTIME = "overtime"
        const val PREFERENCE_CLOCK = "clock"
    }

    var players
        get() = Players.fromValue(preferences.getInt(PREFERENCE_PLAYERS, Players.SINGLES.value))
        set(players) = preferences.edit().putInt(PREFERENCE_PLAYERS, players.value).apply()

    var startingServer
        get() = StartingServer.fromValue(preferences.getInt(PREFERENCE_SERVER, StartingServer.RANDOM.value))
        set(startingServer) = preferences.edit().putInt(PREFERENCE_SERVER, startingServer.value).apply()

    var numSets
        get() = NumSets.fromValue(preferences.getInt(PREFERENCE_NUM_SETS, NumSets.THREE.value))
        set(numSets) = preferences.edit().putInt(PREFERENCE_NUM_SETS, numSets.value).apply()

    var overtime
        get() = Overtime.fromValue(preferences.getInt(PREFERENCE_OVERTIME, Overtime.TIEBREAK.value))
        set(overtime) = preferences.edit().putInt(PREFERENCE_OVERTIME, overtime.value).apply()

    var clock
        get() = preferences.getBoolean(PREFERENCE_CLOCK, false)
        set(clock) = preferences.edit().putBoolean(PREFERENCE_CLOCK, clock).apply()
}
