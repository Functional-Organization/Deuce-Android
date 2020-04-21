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

package net.mqduck.deuce.common

const val DEFAULT_WIN_MARGIN_MATCH = 1
const val DEFAULT_WIN_MINIMUM_SET = 6
const val DEFAULT_WIN_MARGIN_SET = 2
const val DEFAULT_WIN_MINIMUM_GAME = 4
const val DEFAULT_WIN_MARGIN_GAME = 2

const val DEFAULT_WIN_MINIMUM_GAME_TIEBREAK = 7
const val DEFAULT_WIN_MARGIN_GAME_TIEBREAK = 2

val DEFAULT_MATCH_TYPE = MatchType.SINGLES
val DEFAULT_STARTING_SERVER = Team.TEAM1
val DEFAULT_NUM_SETS = NumSets.THREE
val DEFAULT_OVERTIME_RULE = OvertimeRule.TIEBREAK
const val DEFAULT_CLOCK = false

const val KEY_NUM_SETS = "num_sets"
const val KEY_SERVER = "server"
const val KEY_MATCH_TYPE = "type"
const val KEY_OVERTIME_RULE = "overtime"
const val KEY_CLOCK = "clock"
const val KEY_SCORE_ARRAY = "score_array"
const val KEY_SCORE_SIZE = "score_size"

const val PATH_CURRENT_MATCH = "/current_match"
