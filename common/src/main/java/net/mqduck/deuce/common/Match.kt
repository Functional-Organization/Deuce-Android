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

open class Match(
    val winMinimumMatch: Int,
    val winMarginMatch: Int,
    val winMinimumSet: Int,
    val winMarginSet: Int,
    val winMinimumGame: Int,
    val winMarginGame: Int,
    val winMinimumGameTiebreak: Int,
    val winMarginGameTiebreak: Int,
    val startingServer: Team,
    val overtimeRule: OvertimeRule,
    val matchType: MatchType,
    val startTime: Long,
    var setEndTimes: MutableList<Long>,
    scoreLog: ScoreStack,
    open var nameTeam1: String,
    open var nameTeam2: String
) {
    lateinit var sets: ArrayList<Set>
    protected lateinit var mScore: Score
    lateinit var serving: Serving
        private set
    var changeover = false
        private set
    var serviceChanged = true
        private set

    var scoreLog = ScoreStack()
        set(value) {
            field = value
            loadScoreLog()
        }
    var stats: Stats = MutableStats()
        private set

    init {
        this.scoreLog = scoreLog
    }

    private fun loadScoreLog() {
        mScore = Score(winMinimumMatch, winMarginMatch)
        stats = MutableStats()
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

        for (i in 0 until scoreLog.size) {
            score(scoreLog[i], false)
        }
    }

    open val winner get() = mScore.winner
    val currentSet get() = sets.last()
    val currentGame get() = currentSet.currentGame
    val isOngoing get() = winner == TeamOrNone.NONE
    val servingTeam get() = when (serving) {
        Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT, Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT -> Team.TEAM1
        Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT, Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT -> Team.TEAM2
    }

    private fun score(team: Team, updateLogs: Boolean): Winners {
        changeover = false
        serviceChanged = false
        var winnerMatch = TeamOrNone.NONE
        var winnerSet = TeamOrNone.NONE
        val currentServingTeam = servingTeam

        val winnerGame = currentGame.score(team)

        when (team) {
            Team.TEAM1 -> {
                ++(stats as MutableStats).pointsTeam1
                if (currentServingTeam == Team.TEAM1) {
                    ++(stats as MutableStats).servicePointsWonTeam1
                }
            }
            Team.TEAM2 -> {
                ++(stats as MutableStats).pointsTeam2
                if (currentServingTeam == Team.TEAM2) {
                    ++(stats as MutableStats).servicePointsWonTeam2
                }
            }
        }
        when (currentServingTeam) {
            Team.TEAM1 -> {
                ++(stats as MutableStats).servicePointsPlayedTeam1
                if (currentGame.breakPoint == TeamOrNone.TEAM2) {
                    ++(stats as MutableStats).breakPointsPlayedTeam2
                }
            }
            Team.TEAM2 -> {
                ++(stats as MutableStats).servicePointsPlayedTeam2
                if (currentGame.breakPoint == TeamOrNone.TEAM1) {
                    ++(stats as MutableStats).breakPointsPlayedTeam1
                }
            }
        }

        if (winnerGame != TeamOrNone.NONE) {
            when (team) {
                Team.TEAM1 -> {
                    ++(stats as MutableStats).gamesWonTeam1
                    if (currentServingTeam == Team.TEAM2) {
                        ++(stats as MutableStats).breakPointsWonTeam1
                    }
                }
                Team.TEAM2 -> {
                    ++(stats as MutableStats).gamesWonTeam2
                    if (currentServingTeam == Team.TEAM1) {
                        ++(stats as MutableStats).breakPointsWonTeam2
                    }
                }
            }

            winnerSet = currentSet.score(team)
            if (winnerSet != TeamOrNone.NONE) {
                if (updateLogs) {
                    setEndTimes.add(System.currentTimeMillis())
                }

                ++mScore[team]
                winnerMatch = mScore.winner
                if (winnerMatch == TeamOrNone.NONE) {
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
                    if (matchType == MatchType.DOUBLES && startingServer == Team.TEAM2)
                        Serving.PLAYER4_RIGHT
                    else
                        Serving.PLAYER2_RIGHT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT ->
                    if (matchType == MatchType.DOUBLES && startingServer == Team.TEAM1)
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
        } else if (
            currentGame.tiebreak
            && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(Team.TEAM2)) % 2 == 1
        ) {
            serving = when (serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT ->
                    if (matchType == MatchType.DOUBLES && startingServer == Team.TEAM2)
                        Serving.PLAYER4_LEFT
                    else
                        Serving.PLAYER2_LEFT
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT ->
                    if (matchType == MatchType.DOUBLES && startingServer == Team.TEAM1)
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

        if (currentGame.tiebreak && (currentGame.getScore(Team.TEAM1) + currentGame.getScore(Team.TEAM2)) % 6 == 0) {
            changeover = true
        }

        if (updateLogs) {
            scoreLog.push(team)
        }

        return Winners(winnerGame, winnerSet, winnerMatch)
    }

    fun score(team: Team) = score(team, true)

    fun undo(): Boolean {
        if (scoreLog.size != 0) {
            if (currentSet.scoreP1 == 0 && currentSet.scoreP2 == 0) {
                setEndTimes.removeAt(setEndTimes.lastIndex)
            }
            scoreLog.pop()
            loadScoreLog()
            return true
        }
        return false
    }

    interface Stats {
        val pointsTeam1: Int
        val pointsTeam2: Int
        val gamesWonTeam1: Int
        val gamesWonTeam2: Int
        val breakPointsWonTeam1: Int
        val breakPointsWonTeam2: Int
        val breakPointsPlayedTeam1: Int
        val breakPointsPlayedTeam2: Int
        val servicePointsWonTeam1: Int
        val servicePointsWonTeam2: Int
        val servicePointsPlayedTeam1: Int
        val servicePointsPlayedTeam2: Int
    }

    private class MutableStats : Stats {
        override var pointsTeam1 = 0
        override var pointsTeam2 = 0
        override var gamesWonTeam1 = 0
        override var gamesWonTeam2 = 0
        override var breakPointsWonTeam1 = 0
        override var breakPointsWonTeam2 = 0
        override var breakPointsPlayedTeam1 = 0
        override var breakPointsPlayedTeam2 = 0
        override var servicePointsWonTeam1 = 0
        override var servicePointsWonTeam2 = 0
        override var servicePointsPlayedTeam1 = 0
        override var servicePointsPlayedTeam2 = 0
    }
}
