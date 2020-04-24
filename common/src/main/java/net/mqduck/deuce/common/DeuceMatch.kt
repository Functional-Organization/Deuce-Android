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

class DeuceMatch(
    numSets: NumSets,
    startingServer: Team,
    overtimeRule: OvertimeRule,
    matchType: MatchType,
    startTime: Long,
    scoreLog: ScoreStack
) : Match(
    numSets.value,
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
    scoreLog
)