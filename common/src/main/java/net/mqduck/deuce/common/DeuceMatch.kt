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
    constructor() : this(
        NumSets.ONE,
        Team.TEAM1,
        OvertimeRule.TIEBREAK,
        MatchType.SINGLES,
        PlayTimesData(),
        PlayTimesList(),
        ScoreStack(),
        DUMMY_NAME_TEAM1,
        DUMMY_NAME_TEAM2
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

    companion object CREATOR : Parcelable.Creator<DeuceMatch> {
        override fun createFromParcel(parcel: Parcel): DeuceMatch {
            return DeuceMatch(parcel)
        }

        override fun newArray(size: Int): Array<DeuceMatch?> {
            return arrayOfNulls(size)
        }
    }

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
}
