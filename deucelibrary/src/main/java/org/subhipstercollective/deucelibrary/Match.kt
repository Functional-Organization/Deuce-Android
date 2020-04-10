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
class Match(
    val winMinimum: Int, val winMargin: Int,
    val winMinimumSet: Int, val winMarginSet: Int,
    val winMinimumGame: Int, val winMarginGame: Int,
    val winMinimumGameTiebreak: Int, val winMarginGameTiebreak: Int,
    val startingServer: Team,
    val overtime: Overtime,
    val players: Players,
    private val controller: ScoreController
) {
    var sets = ArrayList<Set>()
    private var mScore = Score(winMinimum, winMargin)

    init {
        addNewSet()
    }

    fun score(team: Team) = mScore.score(team)

    val currentSet get() = sets.last()
    val currentGame get() = currentSet.currentGame

    fun addNewSet() = sets.add(
        Set(
            winMinimumSet,
            winMarginSet,
            winMinimumGame,
            winMarginGame,
            winMinimumGameTiebreak,
            winMarginGameTiebreak,
            overtime,
            controller
        )
    )
}
