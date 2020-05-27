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
    var matchType
        get() = MatchType.fromOrdinal(preferences.getInt(KEY_MATCH_TYPE, DEFAULT_MATCH_TYPE.ordinal))
        set(players) = preferences.edit().putInt(KEY_MATCH_TYPE, players.ordinal).apply()

    var startingServer
        get() = Team.fromOrdinal(preferences.getInt(KEY_SERVER, DEFAULT_STARTING_SERVER.ordinal))
        set(startingServer) = preferences.edit().putInt(KEY_SERVER, startingServer.ordinal).apply()

    var numSets
        get() = NumSets.fromOrdinal(preferences.getInt(KEY_NUM_SETS, DEFAULT_NUM_SETS.ordinal))
        set(numSets) = preferences.edit().putInt(KEY_NUM_SETS, numSets.ordinal).apply()

    var overtime
        get() = OvertimeRule.fromOrdinal(preferences.getInt(KEY_OVERTIME_RULE, DEFAULT_OVERTIME_RULE.ordinal))
        set(overtime) = preferences.edit().putInt(KEY_OVERTIME_RULE, overtime.ordinal).apply()

    var showClock
        get() = preferences.getBoolean(KEY_SHOW_CLOCK, DEFAULT_SHOW_CLOCK)
        set(show) = preferences.edit().putBoolean(KEY_SHOW_CLOCK, show).apply()

    var showCustomNames
        get() = preferences.getBoolean(KEY_SHOW_CUSTOM_NAMES, DEFAULT_SHOW_TEAM_NAMES)
        set(show) = preferences.edit().putBoolean(KEY_SHOW_CUSTOM_NAMES, show).apply()
}
