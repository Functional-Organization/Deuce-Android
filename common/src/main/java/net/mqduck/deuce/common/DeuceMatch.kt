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

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log

class DeuceMatch(
    val numSets: NumSets,
    startingServer: Team,
    overtimeRule: OvertimeRule,
    matchType: MatchType,
    startTime: Long,
    gameEndTimes: MutableList<Long>,
    scoreLog: ScoreStack,
    nameTeam1: String,
    nameTeam2: String
) : Match(
    numSets.winMinimum,
    DEFAULT_WIN_MARGIN_MATCH,
    DEFAULT_WIN_MINIMUM_SET,
    DEFAULT_WIN_MARGIN_SET,
    DEFAULT_WIN_MINIMUM_GAME,
    DEFAULT_WIN_MARGIN_GAME,
    DEFAULT_WIN_MINIMUM_GAME_TIEBREAK,
    DEFAULT_WIN_MARGIN_GAME_TIEBREAK,
    startingServer,
    overtimeRule,
    matchType,
    startTime,
    gameEndTimes,
    scoreLog,
    nameTeam1,
    nameTeam2
), Parcelable, Comparable<DeuceMatch> {
    companion object {
        private var defaultNameTeam1Singles = ""
        private var defaultNameTeam2Singles = ""
        private var defaultNameTeam1Doubles = ""
        private var defaultNameTeam2Doubles = ""
        private var defaultNameShortTeam1Doubles = ""
        private var defaultNameShortTeam2Doubles = ""

        val CREATOR = object : Parcelable.Creator<DeuceMatch> {
            override fun createFromParcel(parcel: Parcel): DeuceMatch {
                return DeuceMatch(parcel)
            }

            override fun newArray(size: Int): Array<DeuceMatch?> {
                return arrayOfNulls(size)
            }
        }

        fun init(context: Context) {
            defaultNameTeam1Singles = context.resources.getString(R.string.default_name_team1_singles)
            defaultNameTeam2Singles = context.resources.getString(R.string.default_name_team2_singles)
            defaultNameTeam1Doubles = context.resources.getString(R.string.default_name_team1_doubles)
            defaultNameTeam2Doubles = context.resources.getString(R.string.default_name_team2_doubles)
            defaultNameShortTeam1Doubles = context.resources.getString(R.string.default_name_short_team1_doubles)
            defaultNameShortTeam2Doubles = context.resources.getString(R.string.default_name_short_team2_doubles)
        }
    }

    constructor() : this(
        DEFAULT_NUM_SETS,
        DEFAULT_STARTING_SERVER,
        DEFAULT_OVERTIME_RULE,
        DEFAULT_MATCH_TYPE,
        System.currentTimeMillis(),
        ArrayList<Long>(),
        ScoreStack(),
        "",
        ""
    )

    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as NumSets,
        parcel.readSerializable() as Team,
        parcel.readSerializable() as OvertimeRule,
        parcel.readSerializable() as MatchType,
        parcel.readLong(),
        parcel.createLongArray()!!.toCollection(ArrayList()),
        parcel.readParcelable<ScoreStack>(ScoreStack::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    // TODO: for testing
    constructor(startTime: Long) : this(
        DEFAULT_NUM_SETS,
        DEFAULT_STARTING_SERVER,
        DEFAULT_OVERTIME_RULE,
        DEFAULT_MATCH_TYPE,
        startTime,
        ArrayList<Long>(),
        ScoreStack(),
        "",
        ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(numSets)
        parcel.writeSerializable(startingServer)
        parcel.writeSerializable(overtimeRule)
        parcel.writeSerializable(matchType)
        parcel.writeLong(startTime)
        parcel.writeLongArray(gameEndTimes.toLongArray())
        parcel.writeParcelable(scoreLog, flags)
        parcel.writeString(nameTeam1)
        parcel.writeString(nameTeam2)
    }

    override fun describeContents() = 0

    override fun compareTo(other: DeuceMatch): Int {
        Log.d("foo", "comparing")
        return startTime.compareTo(other.startTime)
    }

    override fun equals(other: Any?): Boolean {
        Log.d("foo", "checking equality")
        return other is DeuceMatch && startTime == other.startTime
    }

    override fun hashCode(): Int {
        Log.d("foo", "getting hash code")
        return startTime.hashCode()
    }

    override var nameTeam1 = nameTeam1
        set(value) {
            field = when {
                value == defaultNameTeam1Singles && matchType == MatchType.SINGLES -> ""
                value == defaultNameTeam1Doubles && matchType == MatchType.DOUBLES -> ""
                else -> value
            }
        }

    override var nameTeam2 = nameTeam2
        set(value) {
            field = when {
                value == defaultNameTeam2Singles && matchType == MatchType.SINGLES -> ""
                value == defaultNameTeam2Doubles && matchType == MatchType.DOUBLES -> ""
                else -> value
            }
        }

    val displayNameShortTeam1
        get() = if (nameTeam1.isEmpty()) {
            when (matchType) {
                MatchType.SINGLES -> defaultNameTeam1Singles
                MatchType.DOUBLES -> defaultNameShortTeam1Doubles
            }
        } else {
            nameTeam1
        }

    val displayNameTeam1
        get() = if (nameTeam1.isEmpty()) {
            when (matchType) {
                MatchType.SINGLES -> defaultNameTeam1Singles
                MatchType.DOUBLES -> defaultNameTeam1Doubles
            }
        } else {
            nameTeam1
        }

    val displayNameShortTeam2
        get() = if (nameTeam2.isEmpty()) {
            when (matchType) {
                MatchType.SINGLES -> defaultNameTeam2Singles
                MatchType.DOUBLES -> defaultNameShortTeam2Doubles
            }
        } else {
            nameTeam2
        }

    val displayNameTeam2
        get() = if (nameTeam2.isEmpty()) {
            when (matchType) {
                MatchType.SINGLES -> defaultNameTeam2Singles
                MatchType.DOUBLES -> defaultNameTeam2Doubles
            }
        } else {
            nameTeam2
        }

    override var winner get() = mScore.winner
        set(value) {
            mScore.winner = value
        }
}
