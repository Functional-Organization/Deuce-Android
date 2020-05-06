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

enum class Team {
    TEAM1, TEAM2;

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            TEAM1.ordinal -> TEAM1
            TEAM2.ordinal -> TEAM2
            else -> DEFAULT_STARTING_SERVER
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

enum class MatchType {
    SINGLES, DOUBLES;

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            SINGLES.ordinal -> SINGLES
            DOUBLES.ordinal -> DOUBLES
            else -> DEFAULT_MATCH_TYPE
        }
    }
}

enum class NumSets(val winMinimum: Int, val winMaximum: Int) {
    ONE(1, 1),
    THREE(2, 3),
    FIVE(3, 5),
    INFINITE(Int.MAX_VALUE, Int.MAX_VALUE);

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            ONE.ordinal -> ONE
            THREE.ordinal -> THREE
            FIVE.ordinal -> FIVE
            INFINITE.ordinal -> INFINITE
            else -> DEFAULT_NUM_SETS
        }
    }
}

enum class OvertimeRule {
    TIEBREAK, ADVANTAGE;

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            TIEBREAK.ordinal -> TIEBREAK
            ADVANTAGE.ordinal -> ADVANTAGE
            else -> DEFAULT_OVERTIME_RULE
        }
    }
}
