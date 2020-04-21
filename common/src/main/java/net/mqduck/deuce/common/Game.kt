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

import android.content.Context

class Game(winMinimum: Int, winMargin: Int, private val match: Match, val tiebreak: Boolean = false) {
    companion object {
        private val scoreMap = arrayOf("Love", "15", "30", "40", "")
        private lateinit var strAdIn: String
        private lateinit var strAdOut: String
        private lateinit var strDeuce: String

        fun init(context: Context) {
            scoreMap[0] = context.resources.getString(R.string.love)
            strAdIn = context.getString(R.string.ad_in)
            strAdOut = context.getString(R.string.ad_out)
            strDeuce = context.getString(R.string.deuce)
        }
    }

    private val mScore = Score(winMinimum, winMargin)

    /**
     * Adds a point to the score of a team for this game.
     *
     * @param team The scoring team.
     * @return The winning team, if any.
     */
    fun score(team: Team) = mScore.score(team)

    /**
     * Returns the current score of a team for this game.
     *
     * @param team The team to get the score of.
     */
    fun getScore(team: Team) = mScore.getScore(team)

    /**
     * Returns Strings representing the current game scores in standard tennis terminology.
     */
    fun getScoreStrings(): ScoreStrings {
        return when {
            mScore.winner == Winner.TEAM1 -> ScoreStrings("\uD83C\uDFC6", "")
            mScore.winner == Winner.TEAM2 -> ScoreStrings("", "\uD83C\uDFC6")

            tiebreak -> ScoreStrings(mScore.scoreTeam1.toString(), mScore.scoreTeam2.toString())

            mScore.scoreTeam1 < 3 || mScore.scoreTeam2 < 3 ->
                ScoreStrings(scoreMap[mScore.scoreTeam1], scoreMap[mScore.scoreTeam2])
            mScore.scoreTeam1 > mScore.scoreTeam2 -> when (match.serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT, Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT ->
                    ScoreStrings(strAdIn, "")
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT, Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT ->
                    ScoreStrings(strAdOut, "")
            }
            mScore.scoreTeam1 < mScore.scoreTeam2 -> when (match.serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT, Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT ->
                    ScoreStrings("", strAdOut)
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT, Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT ->
                    ScoreStrings("", strAdIn)
            }
            else -> ScoreStrings(strDeuce, strDeuce)
        }
    }
}
