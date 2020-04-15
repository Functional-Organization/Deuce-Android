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

import net.mqduck.deuce.common.Match
import net.mqduck.deuce.common.Overtime
import net.mqduck.deuce.common.Players
import net.mqduck.deuce.common.Team

class ScoreController /*: ScoreController*/ {
    internal var match = Match(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        Team.TEAM1,
        Overtime.TIEBREAK,
        Players.SINGLES
    )

    /*override var serving = Serving.PLAYER1_LEFT
    private val currentSet get() = match.currentSet
    internal val currentGame get() = match.currentGame
    private var scoreLog = ScoreStack()
    var changeover = false
        private set
    var serviceChanged = true
        private set
    var matchAdded = false
        private set*/

    /*fun loadInstanceState(savedInstanceState: Bundle) {
        scoreLog = savedInstanceState.getParcelable("scores")!!
        matchAdded = savedInstanceState.getBoolean("matchAdded")

        addMatch(
            savedInstanceState.getInt("winMinimumMatch"),
            savedInstanceState.getInt("winMarginMatch"),
            savedInstanceState.getInt("winMinimumSet"),
            savedInstanceState.getInt("winMarginSet"),
            savedInstanceState.getInt("winMinimumGame"),
            savedInstanceState.getInt("winMarginGame"),
            savedInstanceState.getInt("winMinimumGameTiebreak"),
            savedInstanceState.getInt("winMarginGameTiebreak"),
            savedInstanceState.getSerializable("startingServer") as Team,
            savedInstanceState.getSerializable("overtime") as Overtime,
            savedInstanceState.getSerializable("players") as Players,
            false
        )
    }

    fun saveInstanceState(): Bundle {
        val outState = Bundle()
        outState.putParcelable("scores", scoreLog)

        outState.putInt("winMinimumMatch", match.winMinimum)
        outState.putInt("winMarginMatch", match.winMargin)
        outState.putInt("winMinimumSet", match.winMinimumSet)
        outState.putInt("winMarginSet", match.winMarginSet)
        outState.putInt("winMinimumGame", match.winMinimumGame)
        outState.putInt("winMarginGame", match.winMarginGame)
        outState.putInt("winMinimumGameTiebreak", match.winMinimumGameTiebreak)
        outState.putInt("winMarginGameTiebreak", match.winMarginGameTiebreak)
        outState.putSerializable("overtime", match.overtime)
        outState.putSerializable("players", match.players)

        outState.putSerializable("startingServer", match.startingServer)
        outState.putBoolean("matchAdded", matchAdded)
        return outState
    }*/

    /*private fun loadScores() {
        val numScores = scoreLog.size
        for (i in 0 until numScores) {
            score(scoreLog[i], false)
        }
    }*/

    /*private fun addMatch(
        numSets: Int, winMarginMatch: Int,
        winMinimumSet: Int, winMarginSet: Int,
        winMinimumGame: Int, winMarginGame: Int,
        winMinimumGameTiebreak: Int, winMarginGameTiebreak: Int,
        startingServer: Team,
        overtime: Overtime,
        players: Players,
        wipeScoreLog: Boolean
    ) {
        matchAdded = true
        if (wipeScoreLog) {
            scoreLog = ScoreStack()
        }
        match = Match(
            numSets,
            winMarginMatch,
            winMinimumSet,
            winMarginSet,
            winMinimumGame,
            winMarginGame,
            winMinimumGameTiebreak,
            winMarginGameTiebreak,
            startingServer,
            overtime,
            players
        )

        serving = if (startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER2_RIGHT
        serviceChanged = true
    }

    fun addMatch(
        numSets: NumSets, winMarginMatch: Int,
        winMinimumSet: Int, winMarginSet: Int,
        winMinimumGame: Int, winMarginGame: Int,
        winMinimumGameTiebreak: Int, winMarginGameTiebreak: Int,
        startingServer: Team,
        overtime: Overtime,
        players: Players
    ) {
        addMatch(
            numSets.value,
            winMarginMatch,
            winMinimumSet,
            winMarginSet,
            winMinimumGame,
            winMarginGame,
            winMinimumGameTiebreak,
            winMarginGameTiebreak,
            startingServer,
            overtime,
            players,
            true
        )
    }*/

    /*fun score(team: Team, updateLog: Boolean = true) {
        changeover = false
        serviceChanged = false
        val winnerGame = currentGame.score(team)
        if (winnerGame != Winner.NONE) {
            val winnerSet = currentSet.score(team)
            if (winnerSet != Winner.NONE) {
                val winnerMatch = match.score(team)
                if (winnerMatch != Winner.NONE) {
                    // Match is over
                    *//*scoreView?.buttonScoreP1?.isEnabled = false
                    scoreView?.buttonScoreP2?.isEnabled = false*//*
                    *//*mainActivity.database
                        .collection("match")
                        .document("user1")
                        .collection("matches")
                        .document("match1")
                        .set(
                            hashMapOf(
                                "slSize" to scoreLog.size,
                                "slArray" to scoreLog.bitSetToLongArray().toList(),
                                "wminMatch" to match.winMinimum,
                                "overtime" to match.overtime.ordinal,
                                "players" to match.players.ordinal,
                                "begin" to Timestamp(Date(match.startTime)),
                                "end" to Timestamp(Date())
                            )
                        )*//*
                } else {
                    // Set is over
                    match.addNewSet()
                }
            } else {
                // Game is over
                currentSet.addNewGame()
                if (currentGame.tiebreak) {
                    serving = when (serving) {
                        Serving.PLAYER1_RIGHT -> Serving.PLAYER1_LEFT
                        Serving.PLAYER2_RIGHT -> Serving.PLAYER2_LEFT
                        Serving.PLAYER3_RIGHT -> Serving.PLAYER3_LEFT
                        Serving.PLAYER4_RIGHT -> Serving.PLAYER4_LEFT
                        else -> Serving.PLAYER1_RIGHT
                    }
                }
            }

            if (currentSet.games.size % 2 == 0) {
                changeover = true
            }

            serviceChanged = true

            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT ->
                    if (match.players == Players.DOUBLES && match.startingServer == Team.TEAM2)
                        Serving.PLAYER4_RIGHT
                    else
                        Serving.PLAYER2_RIGHT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT ->
                    if (match.players == Players.DOUBLES && match.startingServer == Team.TEAM1)
                        Serving.PLAYER3_RIGHT
                    else
                        Serving.PLAYER1_RIGHT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT ->
                    if (match.startingServer == Team.TEAM1)
                        Serving.PLAYER4_RIGHT
                    else
                        Serving.PLAYER2_RIGHT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT ->
                    if (match.startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER3_RIGHT
            }
        } else if (currentGame.tiebreak && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(
                Team.TEAM2
            )) % 2 == 1
        ) {
            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT ->
                    if (match.players == Players.DOUBLES && match.startingServer == Team.TEAM2)
                        Serving.PLAYER4_LEFT
                    else
                        Serving.PLAYER2_LEFT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT ->
                    if (match.players == Players.DOUBLES && match.startingServer == Team.TEAM1)
                        Serving.PLAYER3_LEFT
                    else
                        Serving.PLAYER1_LEFT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT ->
                    if (match.startingServer == Team.TEAM1)
                        Serving.PLAYER4_LEFT
                    else
                        Serving.PLAYER2_LEFT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT ->
                    if (match.startingServer == Team.TEAM1)
                        Serving.PLAYER1_LEFT
                    else
                        Serving.PLAYER3_LEFT
            }

            serviceChanged = true
        } else {
            serving = when (serving) {
                Serving.PLAYER1_LEFT -> Serving.PLAYER1_RIGHT
                Serving.PLAYER1_RIGHT -> Serving.PLAYER1_LEFT
                Serving.PLAYER2_LEFT -> Serving.PLAYER2_RIGHT
                Serving.PLAYER2_RIGHT -> Serving.PLAYER2_LEFT
                Serving.PLAYER3_LEFT -> Serving.PLAYER3_RIGHT
                Serving.PLAYER3_RIGHT -> Serving.PLAYER3_LEFT
                Serving.PLAYER4_LEFT -> Serving.PLAYER4_RIGHT
                Serving.PLAYER4_RIGHT -> Serving.PLAYER4_LEFT
            }
        }

        if (updateLog) {
            scoreLog.push(team)
        }

        if (currentGame.tiebreak && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(
                Team.TEAM2
            )) % 6 == 0
        ) {
            changeover = true
        }
    }*/

    /*fun undo(): Boolean {
        if (scoreLog.size != 0) {
            scoreLog.pop()
            addMatch(
                match.winMinimum,
                match.winMargin,
                match.winMinimumSet,
                match.winMarginSet,
                match.winMinimumGame,
                match.winMarginGame,
                match.winMinimumGameTiebreak,
                match.winMarginGameTiebreak,
                match.startingServer,
                match.overtime,
                match.players,
                false
            )

            loadScores()

            return true
        }
        return false
    }*/
}
