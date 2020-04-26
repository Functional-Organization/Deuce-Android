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
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.*
import kotlinx.android.synthetic.main.activity_main.*
import net.mqduck.deuce.common.*
import java.util.*

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener {
    var match = Match(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        Team.TEAM1,
        OvertimeRule.TIEBREAK,
        MatchType.SINGLES
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Game.init(this)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("foo", "outside")
        dataEvents.forEach { event ->
            // DataItem changed
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo(PATH_CURRENT_MATCH) == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            Log.d("foo", "inner")
                            match = Match(
                                getInt(KEY_NUM_SETS),
                                DEFAULT_WIN_MARGIN_MATCH,
                                DEFAULT_WIN_MINIMUM_SET,
                                DEFAULT_WIN_MARGIN_SET,
                                DEFAULT_WIN_MINIMUM_GAME,
                                DEFAULT_WIN_MARGIN_GAME,
                                DEFAULT_WIN_MINIMUM_GAME_TIEBREAK,
                                DEFAULT_WIN_MARGIN_GAME_TIEBREAK,
                                Team.fromOrdinal(getInt(KEY_SERVER)),
                                OvertimeRule.fromOrdinal(getInt(KEY_OVERTIME_RULE)),
                                MatchType.fromOrdinal(getInt(KEY_MATCH_TYPE)),
                                getLong(KEY_START_TIME),
                                ScoreStack(getInt(KEY_SCORE_SIZE), BitSet.valueOf(getLongArray(KEY_SCORE_ARRAY)))
                            )
                        }

                        val scoreStrings = match.currentGame.getScoreStrings()
                        score1.text = scoreStrings.player1
                        score2.text = scoreStrings.player2
                    }
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }
}
