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

class Match : Parcelable {
    val winMinimumMatch: Int
    val winMarginMatch: Int
    val winMinimumSet: Int
    val winMarginSet: Int
    val winMinimumGame: Int
    val winMarginGame: Int
    val winMinimumGameTiebreak: Int
    val winMarginGameTiebreak: Int
    val startingServer: Team
    val overtimeRule: OvertimeRule
    val players: Players

    lateinit var sets: ArrayList<Set>
    private lateinit var mScore: Score
    val startTime = System.currentTimeMillis()
    lateinit var serving: Serving
        private set
    private var scoreLog = ScoreStack()
    var changeover = false
        private set
    var serviceChanged = true
        private set
    var matchAdded = false
        private set

    constructor(
        winMinimumMatch: Int, winMarginMatch: Int,
        winMinimumSet: Int, winMarginSet: Int,
        winMinimumGame: Int, winMarginGame: Int,
        winMinimumGameTiebreak: Int, winMarginGameTiebreak: Int,
        startingServer: Team,
        overtimeRule: OvertimeRule,
        players: Players
    ) {
        this.winMinimumMatch = winMinimumMatch
        this.winMarginMatch = winMarginMatch
        this.winMinimumSet = winMinimumSet
        this.winMarginSet = winMarginSet
        this.winMinimumGame = winMinimumGame
        this.winMarginGame = winMarginGame
        this.winMinimumGameTiebreak = winMinimumGameTiebreak
        this.winMarginGameTiebreak = winMarginGameTiebreak
        this.startingServer = startingServer
        this.overtimeRule = overtimeRule
        this.players = players

        mScore = Score(winMinimumMatch, winMarginMatch)
        serving = if (startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER2_RIGHT
        sets = arrayListOf(
            Set(
                winMinimumSet,
                winMarginSet,
                winMinimumGame,
                winMarginGame,
                winMinimumGameTiebreak,
                winMarginGameTiebreak,
                overtimeRule,
                this
            )
        )
    }

    constructor(parcel: Parcel) {
        winMinimumMatch = parcel.readInt()
        winMarginMatch = parcel.readInt()
        winMinimumSet = parcel.readInt()
        winMarginSet = parcel.readInt()
        winMinimumGame = parcel.readInt()
        winMarginGame = parcel.readInt()
        winMinimumGameTiebreak = parcel.readInt()
        winMarginGameTiebreak = parcel.readInt()
        startingServer = parcel.readSerializable() as Team
        overtimeRule = parcel.readSerializable() as OvertimeRule
        players = parcel.readSerializable() as Players

        loadScoreLog(parcel.readParcelable(ScoreStack::class.java.classLoader)!!)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(winMinimumMatch)
        parcel.writeInt(winMarginMatch)
        parcel.writeInt(winMinimumSet)
        parcel.writeInt(winMarginSet)
        parcel.writeInt(winMinimumGame)
        parcel.writeInt(winMarginGame)
        parcel.writeInt(winMinimumGameTiebreak)
        parcel.writeInt(winMarginGameTiebreak)
        parcel.writeSerializable(startingServer)
        parcel.writeSerializable(overtimeRule)
        parcel.writeSerializable(players)

        // TODO: Is just passing flags correct?
        parcel.writeParcelable(scoreLog, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Match> {
        override fun createFromParcel(parcel: Parcel) = Match(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Match>(size)
    }

    private fun loadScoreLog(scoreLog: ScoreStack) {
        this.scoreLog = ScoreStack()
        mScore = Score(winMinimumMatch, winMarginMatch)
        serving = if (startingServer == Team.TEAM1) Serving.PLAYER1_RIGHT else Serving.PLAYER2_RIGHT
        sets = arrayListOf(
            Set(
                winMinimumSet,
                winMarginSet,
                winMinimumGame,
                winMarginGame,
                winMinimumGameTiebreak,
                winMarginGameTiebreak,
                overtimeRule,
                this
            )
        )

        val numScores = scoreLog.size
        for (i in 0 until numScores) {
            score(scoreLog[i])
        }
    }

    val winner get() = mScore.winner

    val currentSet get() = sets.last()
    val currentGame get() = currentSet.currentGame

    fun score(team: Team/*, updateLog: Boolean = true*/) {
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
                    // Set is over, Match is not
                    sets.add(
                        Set(
                            winMinimumSet,
                            winMarginSet,
                            winMinimumGame,
                            winMarginGame,
                            winMinimumGameTiebreak,
                            winMarginGameTiebreak,
                            overtimeRule,
                            this
                        )
                    )
                }
            } else {
                // Game is over, Set is not
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

        scoreLog.push(team)

        if (currentGame.tiebreak && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(Team.TEAM2)) % 6 == 0) {
            changeover = true
        }
    }

    fun undo(): Boolean {
        if (scoreLog.size != 0) {
            scoreLog.pop()
            loadScoreLog(scoreLog)
            return true
        }
        return false
    }
}
