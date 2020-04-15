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
    val players: Players
) {
    var sets = ArrayList<Set>()
    private var mScore = Score(winMinimum, winMargin)
    var startTime = System.currentTimeMillis()
        private set
    var serving = if (startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER2_RIGHT
        private set
    private var scoreLog = ScoreStack()
    var changeover = false
        private set
    var serviceChanged = true
        private set
    var matchAdded = false
        private set

    val winner
        get() = mScore.winner

    init {
        addNewSet()
    }

    val currentSet get() = sets.last()
    val currentGame get() = currentSet.currentGame

    private fun addNewSet() = sets.add(
        Set(
            winMinimumSet,
            winMarginSet,
            winMinimumGame,
            winMarginGame,
            winMinimumGameTiebreak,
            winMarginGameTiebreak,
            overtime,
            this
        )
    )

    //fun score(team: Team) = mScore.score(team)

    fun score(team: Team, updateLog: Boolean = true) {
        changeover = false
        serviceChanged = false
        val winnerGame = currentGame.score(team)
        if (winnerGame != Winner.NONE) {
            val winnerSet = currentSet.score(team)
            if (winnerSet != Winner.NONE) {
                val winnerMatch = mScore.score(team)
                if (winnerMatch != Winner.NONE) {
                    // Match is over
                    /*scoreView?.buttonScoreP1?.isEnabled = false
                    scoreView?.buttonScoreP2?.isEnabled = false*/
                    /*mainActivity.database
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
                        )*/
                } else {
                    // Set is over
                    addNewSet()
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
                    if (players == Players.DOUBLES && startingServer == Team.TEAM2)
                        Serving.PLAYER4_RIGHT
                    else
                        Serving.PLAYER2_RIGHT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT ->
                    if (players == Players.DOUBLES && startingServer == Team.TEAM1)
                        Serving.PLAYER3_RIGHT
                    else
                        Serving.PLAYER1_RIGHT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT ->
                    if (startingServer == Team.TEAM1)
                        Serving.PLAYER4_RIGHT
                    else
                        Serving.PLAYER2_RIGHT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT ->
                    if (startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER3_RIGHT
            }
        } else if (currentGame.tiebreak && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(
                Team.TEAM2
            )) % 2 == 1
        ) {
            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT ->
                    if (players == Players.DOUBLES && startingServer == Team.TEAM2)
                        Serving.PLAYER4_LEFT
                    else
                        Serving.PLAYER2_LEFT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT ->
                    if (players == Players.DOUBLES && startingServer == Team.TEAM1)
                        Serving.PLAYER3_LEFT
                    else
                        Serving.PLAYER1_LEFT
                Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT ->
                    if (startingServer == Team.TEAM1)
                        Serving.PLAYER4_LEFT
                    else
                        Serving.PLAYER2_LEFT
                Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT ->
                    if (startingServer == Team.TEAM1)
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
    }

    fun undo(): Boolean {
        if (scoreLog.size != 0) {
            scoreLog.pop()
            //TODO: rewrite old ScoreController old logic
            /*addMatch(
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

            loadScores()*/

            return true
        }
        return false
    }
}
