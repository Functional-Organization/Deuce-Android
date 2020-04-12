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

package org.subhipstercollective.deucelibrary

/**
 * Created by mqduck on 10/31/17.
 */
enum class Team {
    TEAM1, TEAM2;

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            TEAM2.ordinal -> TEAM2
            else -> TEAM1
        }
    }
}

enum class Winner {
    NONE, TEAM1, TEAM2;
}

// If playing doubles, player 3 is teamed with player 1 and player 4 is teamed with player 2.
enum class Serving {
    PLAYER1_LEFT,
    PLAYER1_RIGHT,
    PLAYER2_LEFT,
    PLAYER2_RIGHT,
    PLAYER3_LEFT,
    PLAYER3_RIGHT,
    PLAYER4_LEFT,
    PLAYER4_RIGHT;
}

enum class Players {
    SINGLES, DOUBLES;

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            DOUBLES.ordinal -> DOUBLES
            else -> SINGLES
        }
    }
}

enum class NumSets(val value: Int) {
    ONE(1), THREE(3), FIVE(5);

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            ONE.ordinal -> ONE
            FIVE.ordinal -> FIVE
            else -> THREE
        }
    }
}

enum class Overtime {
    TIEBREAK, ADVANTAGE;

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            ADVANTAGE.ordinal -> ADVANTAGE
            else -> TIEBREAK
        }
    }
}
