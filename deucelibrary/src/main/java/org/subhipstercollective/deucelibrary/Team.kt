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
enum class Team(val value: Int) {
    TEAM1(0), TEAM2(1)
}

enum class StartingServer(val value: Int) {
    TEAM1(Team.TEAM1.value), TEAM2(Team.TEAM2.value), RANDOM(2); // TODO: ensure uniqueness

    companion object {
        fun fromValue(value: Int) = when (value) {
            TEAM1.value -> TEAM1
            TEAM2.value -> TEAM2
            RANDOM.value -> RANDOM
            else -> throw IllegalArgumentException("Invalid StartingServer value")
        }
    }
}

enum class Winner {
    NONE, TEAM1, TEAM2;

    val value get() = ordinal
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

    val value get() = ordinal
}

enum class Players {
    SINGLES, DOUBLES;

    val value get() = ordinal

    companion object {
        fun fromValue(value: Int) = when (value) {
            SINGLES.value -> SINGLES
            DOUBLES.value -> DOUBLES
            else -> throw IllegalArgumentException("Invalid Players value")
        }
    }
}

enum class NumSets(val value: Int) {
    ONE(1), THREE(3), FIVE(5);

    companion object {
        fun fromValue(value: Int) = when (value) {
            ONE.value -> ONE
            THREE.value -> THREE
            FIVE.value -> FIVE
            else -> throw IllegalArgumentException("Invalid NumSets value")
        }
    }
}

enum class Overtime {
    TIEBREAK, ADVANTAGE;

    val value get() = ordinal

    companion object {
        fun fromValue(value: Int) = when (value) {
            TIEBREAK.value -> TIEBREAK
            ADVANTAGE.value -> ADVANTAGE
            else -> throw IllegalArgumentException("Invalid overtime preference value")
        }
    }
}
