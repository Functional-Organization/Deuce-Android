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
import java.io.File
import java.util.*

//import android.util.Log

lateinit var mainActivity: MainActivity

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener,
    ScoresListFragment.OnMatchInteractionListener {
    private lateinit var scoresListFragment: ScoresListFragment
    internal lateinit var matchList: JSONMatchList

    init {
        mainActivity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        matchList = JSONMatchList(File(filesDir, MATCH_LIST_FILE_NAME))

        for (match in matchList) {
            Log.d("foo", "foo")
        }

        // TODO: Remove after testing
        if (matchList.isEmpty()) {
            val scoreLog = ScoreStack()
            for (i in 0 until 48) {
                scoreLog.push(Team.TEAM1)
            }
            matchList.add(
                DeuceMatch(
                    NumSets.THREE,
                    Team.TEAM1,
                    OvertimeRule.TIEBREAK,
                    MatchType.SINGLES,
                    PlayTimesData(416846345451, 416847346451),
                    PlayTimesList(
                        longArrayOf(0, 0, 0),
                        longArrayOf(0, 0, 0)
                    ),
                    scoreLog,
                    "Myself",
                    "Opponent"
                )
            )
            matchList.add(DeuceMatch())
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Game.init(this)

        scoresListFragment = fragment_scores as ScoresListFragment
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
                            when (MatchState.fromOrdinal(getInt(KEY_MATCH_STATE))) {
                                MatchState.NEW -> {
                                    Log.d("foo", "adding new match")
                                    val newMatch = DeuceMatch(
                                        NumSets.fromOrdinal(getInt(KEY_NUM_SETS)),
                                        Team.fromOrdinal(getInt(KEY_SERVER)),
                                        OvertimeRule.fromOrdinal(getInt(KEY_OVERTIME_RULE)),
                                        MatchType.fromOrdinal(getInt(KEY_MATCH_TYPE)),
                                        PlayTimesData(getLong(KEY_MATCH_START_TIME), getLong(KEY_MATCH_END_TIME)),
                                        PlayTimesList(
                                            getLongArray(KEY_SETS_START_TIMES),
                                            getLongArray(KEY_SETS_END_TIMES)
                                        ),
                                        ScoreStack(
                                            getInt(KEY_SCORE_SIZE),
                                            BitSet.valueOf(getLongArray(KEY_SCORE_ARRAY))
                                        ),
                                        getString(KEY_NAME_TEAM1),
                                        getString(KEY_NAME_TEAM2)
                                    )

                                    if (
                                        matchList.isNotEmpty() &&
                                        matchList.last().winner == Winner.NONE
                                    ) {
                                        matchList[matchList.size - 1] = newMatch
                                    } else {
                                        matchList.add(newMatch)
                                    }
                                }
                                MatchState.ONGOING -> {
                                    Log.d("foo", "updating current match")
                                    if (matchList.isEmpty()) {
                                        // TODO: request match information?
                                        return
                                    }

                                    val currentMatch = matchList.last()
                                    currentMatch.playTimes.endTime = getLong(KEY_MATCH_END_TIME)
                                    currentMatch.setsTimesLog = PlayTimesList(
                                        getLongArray(KEY_SETS_START_TIMES),
                                        getLongArray(KEY_SETS_END_TIMES)
                                    )
                                    currentMatch.scoreLog = ScoreStack(
                                        getInt(KEY_SCORE_SIZE),
                                        BitSet.valueOf(getLongArray(KEY_SCORE_ARRAY))
                                    )
                                }
                                MatchState.OVER -> {
                                    Log.d("foo", "removing current match")
                                    // TODO
                                }
                            }

                            scoresListFragment.view.adapter?.notifyDataSetChanged()
                        }
                    } else if (item.uri.path?.compareTo(PATH_MATCH_LIST) == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            matchList.addAll(getDataMapArrayList(KEY_MATCH_LIST).map { matchDataMap ->
                                DeuceMatch(
                                    NumSets.fromOrdinal(matchDataMap.getInt(KEY_NUM_SETS)),
                                    Team.fromOrdinal(matchDataMap.getInt(KEY_SERVER)),
                                    OvertimeRule.fromOrdinal(matchDataMap.getInt(KEY_OVERTIME_RULE)),
                                    MatchType.fromOrdinal(matchDataMap.getInt(KEY_MATCH_TYPE)),
                                    PlayTimesData(
                                        matchDataMap.getLong(KEY_MATCH_START_TIME),
                                        matchDataMap.getLong(KEY_MATCH_END_TIME)
                                    ),
                                    PlayTimesList(
                                        matchDataMap.getLongArray(KEY_SETS_START_TIMES),
                                        matchDataMap.getLongArray(KEY_SETS_END_TIMES)
                                    ),
                                    ScoreStack(
                                        matchDataMap.getInt(KEY_SCORE_SIZE),
                                        BitSet.valueOf(matchDataMap.getLongArray(KEY_SCORE_ARRAY))
                                    ),
                                    matchDataMap.getString(KEY_NAME_TEAM1),
                                    matchDataMap.getString(KEY_NAME_TEAM2)
                                )
                            })
                        }

                        /*DataMapItem.fromDataItem(item).dataMap.apply {
                            val numMatches = getInt(KEY_NUM_MATCHES)
                            val numSetsList = getIntegerArrayList(KEY_NUM_SETS)
                            val startingServerList = getIntegerArrayList(KEY_SERVER)
                            val overtimeRuleList = getIntegerArrayList(KEY_OVERTIME_RULE)
                            val matchTypeList = getIntegerArrayList(KEY_MATCH_TYPE)
                            val matchStartTimeList = getLongArray(KEY_MATCH_START_TIME)
                            val matchEndTimeList = getLongArray(KEY_MATCH_END_TIME)
                            val setsStartTimesList = ArrayList<LongArray>()
                            val setsEndTimesList = ArrayList<LongArray>()
                            val scoreLogBitsetLongArrayList = ArrayList<LongArray>()
                            for (i in 0 until numMatches) {
                                setsStartTimesList.add(getLongArray("${KEY_SETS_START_TIMES}_${i}"))
                                setsEndTimesList.add(getLongArray("${KEY_SETS_END_TIMES}_${i}"))
                                scoreLogBitsetLongArrayList.add(getLongArray("${KEY_SCORE_ARRAY}_${i}"))
                            }
                            val scoreLogSizeList = getIntegerArrayList(KEY_SCORE_SIZE)
                            val nameTeam1List = getStringArray(KEY_NAME_TEAM1)
                            val nameTeam2List = getStringArray(KEY_NAME_TEAM2)

                            val matches = ArrayList<DeuceMatch>()
                            for (i in 0 until numMatches) {
                                matches.add(
                                    DeuceMatch(
                                        NumSets.fromOrdinal(numSetsList[i]),
                                        Team.fromOrdinal(startingServerList[i]),
                                        OvertimeRule.fromOrdinal(overtimeRuleList[i]),
                                        MatchType.fromOrdinal(matchTypeList[i]),
                                        PlayTimesData(matchStartTimeList[i], matchEndTimeList[i]),
                                        PlayTimesList(setsStartTimesList[i], setsEndTimesList[i]),
                                        ScoreStack(scoreLogSizeList[i], BitSet.valueOf(scoreLogBitsetLongArrayList[i])),
                                        nameTeam1List[i],
                                        nameTeam2List[i]
                                    )
                                )
                            }
                            matchList.addAll(matches)
                        }
                        DataMapItem.fromDataItem(item).dataMap.put*/
                    }
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    override fun onMatchInteraction(item: Match?) {
        // TODO: Make less ugly
        item?.let {
            scoresListFragment.fragmentManager?.let {
                val infoDialog = InfoDialog(item, scoresListFragment)
                infoDialog.show(it, "info")
            }
        }
    }
}
