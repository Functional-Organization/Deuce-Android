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
    playTimes: PlayTimesData,
    setTimesLog: PlayTimesList,
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
    playTimes,
    setTimesLog,
    scoreLog,
    nameTeam1,
    nameTeam2
), Parcelable, Comparable<DeuceMatch> {
    companion object {
        private var defaultNameTeam1Singles = ""
        private var defaultNameTeam2Singles = ""
        private var defaultNameTeam1Doubles = ""
        private var defaultNameTeam2Doubles = ""

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
        }
    }

    constructor() : this(
        DEFAULT_NUM_SETS,
        DEFAULT_STARTING_SERVER,
        DEFAULT_OVERTIME_RULE,
        DEFAULT_MATCH_TYPE,
        PlayTimesData(),
        PlayTimesList(),
        ScoreStack(),
        "",
        ""
    )

    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as NumSets,
        parcel.readSerializable() as Team,
        parcel.readSerializable() as OvertimeRule,
        parcel.readSerializable() as MatchType,
        parcel.readParcelable<PlayTimesData>(PlayTimesData::class.java.classLoader)!!,
        //parcel.createTypedArrayList(PlayTimes.CREATOR)!!,
        PlayTimesList(parcel.createLongArray()!!, parcel.createLongArray()!!),
        parcel.readParcelable<ScoreStack>(ScoreStack::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(numSets)
        parcel.writeSerializable(startingServer)
        parcel.writeSerializable(overtimeRule)
        parcel.writeSerializable(matchType)
        parcel.writeParcelable(playTimes, flags)
        //parcel.writeTypedArray(setsTimesLog.toArray() as Array<PlayTimes>, flags)
        parcel.writeLongArray(setsTimesLog.getStartTimesArray())
        parcel.writeLongArray(setsTimesLog.getEndTimesArray())
        parcel.writeParcelable(scoreLog, flags)
        parcel.writeString(nameTeam1)
        parcel.writeString(nameTeam2)
    }

    override fun describeContents() = 0

    override fun compareTo(other: DeuceMatch)/* = playTimes.startTime.compareTo(other.playTimes.startTime)*/: Int {
        Log.d("foo", "comparing")
        return playTimes.startTime.compareTo(other.playTimes.startTime)
    }

    override fun equals(other: Any?)/* = other is DeuceMatch && playTimes.startTime == other.playTimes.startTime*/: Boolean {
        Log.d("foo", "checking equality")
        return other is DeuceMatch && playTimes.startTime == other.playTimes.startTime
    }

    override fun hashCode()/* = playTimes.startTime.hashCode()*/: Int {
        Log.d("foo", "getting hash code")
        return playTimes.startTime.hashCode()
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

    val displayNameTeam1
        get() = if (nameTeam1.isEmpty()) {
            when (matchType) {
                MatchType.SINGLES -> defaultNameTeam1Singles
                MatchType.DOUBLES -> defaultNameTeam1Doubles
            }
        } else {
            nameTeam1
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
}
