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

class DeuceMatch(
    numSets: Int,
    startingServer: Team,
    overtimeRule: OvertimeRule,
    matchType: MatchType,
    playTimes: PlayTimes,
    setTimesLog: ArrayList<PlayTimes>,
    scoreLog: ScoreStack,
    nameTeam1: String,
    nameTeam2: String
) : Match(
    numSets,
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
), Parcelable {
    constructor() : this(
        0,
        Team.TEAM1,
        OvertimeRule.TIEBREAK,
        MatchType.SINGLES,
        PlayTimes(),
        ArrayList<PlayTimes>(),
        ScoreStack(),
        DEFAULT_NAME_TEAM1,
        DEFAULT_NAME_TEAM2
    )

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readSerializable() as Team,
        parcel.readSerializable() as OvertimeRule,
        parcel.readSerializable() as MatchType,
        parcel.readParcelable<PlayTimes>(PlayTimes::class.java.classLoader)!!,
        parcel.createTypedArrayList(PlayTimes.CREATOR)!!,
        parcel.readParcelable<ScoreStack>(ScoreStack::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(winMinimumMatch)
        parcel.writeSerializable(startingServer)
        parcel.writeSerializable(overtimeRule)
        parcel.writeSerializable(matchType)
        parcel.writeParcelable(playTimes, flags)
        parcel.writeTypedArray(setTimesLog.toArray() as Array<PlayTimes>, flags)
        parcel.writeParcelable(scoreLog, flags)
        parcel.writeString(nameTeam1)
        parcel.writeString(nameTeam2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeuceMatch> {
        override fun createFromParcel(parcel: Parcel): DeuceMatch {
            return DeuceMatch(parcel)
        }

        override fun newArray(size: Int): Array<DeuceMatch?> {
            return arrayOfNulls(size)
        }
    }
}
