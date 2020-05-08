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

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest

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
const val DEFAULT_SHOW_CLOCK = false

// TODO: Check if other should be called DUMMY_ too
const val DUMMY_NAME_TEAM1 = "Myself"
const val DUMMY_NAME_TEAM2 = "Opponent"

const val KEY_NUM_SETS = "num_sets"
const val KEY_SERVER = "server"
const val KEY_MATCH_TYPE = "type"
const val KEY_OVERTIME_RULE = "overtime"
const val KEY_CLOCK = "clock"
const val KEY_SCORE_ARRAY = "score_array"
const val KEY_SCORE_SIZE = "score_size"
const val KEY_MATCH_START_TIME = "match_start"
const val KEY_MATCH_END_TIME = "match_end"
const val KEY_SETS_START_TIMES = "sets_starts"
const val KEY_SETS_END_TIMES = "sets_ends"
const val KEY_CURRENT_FRAGMENT = "current_fragment"
const val KEY_MATCH = "match"
const val KEY_MATCH_ADDED = "match_added"
const val KEY_NAME_TEAM1 = "name_team1"
const val KEY_NAME_TEAM2 = "name_team2"
const val KEY_MATCH_LIST = "match_list"
const val KEY_MATCH_LIST_STATE = "match_list_state"
const val KEY_DELETE_CURRENT_MATCH = "delete_match"
const val KEY_MATCH_STATE = "game_state"

const val PATH_CURRENT_MATCH = "/current_match"
const val PATH_MATCH_LIST = "/matches"
const val PATH_TRANSMISSION_SIGNAL = "/trans_signal"
const val PATH_REQUEST_MATCH_SIGNAL = "/match_signal"

const val MATCH_LIST_FILE_NAME = "deuce_matches"

fun sendSignal(dataClient: DataClient, path: String, urgent: Boolean) {
    val putDataRequest: PutDataRequest =
        PutDataMapRequest.create(path).run {
            dataMap.putLong("dummy", System.currentTimeMillis())
            asPutDataRequest()
        }
    if (urgent) {
        putDataRequest.setUrgent()
    }
    val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataRequest)
    putDataTask.addOnSuccessListener {
        Log.d("foo", "sent signal on $path")
    }
}
