/*
 * Copyright (C) 2019 Jeffrey Thomas Piercy
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

import android.content.Context

class Game(winMinimum: Int, winMargin: Int, private val controller: ControllerMain, val tiebreak: Boolean = false) {
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

    fun score(player: Player = Player.NONE) = mScore.score(player)

    fun getScore(player: Player) = mScore.getScore(player)

    fun getScoreStrs(): ScoreStrings {
        return when {
            mScore.winner == Player.PLAYER1 -> ScoreStrings("\uD83C\uDFC6", "")
            mScore.winner == Player.PLAYER2 -> ScoreStrings("", "\uD83C\uDFC6")
            tiebreak -> ScoreStrings(mScore.scoreP1.toString(), mScore.scoreP2.toString())
            mScore.scoreP1 < 3 || mScore.scoreP2 < 3 -> ScoreStrings(scoreMap[mScore.scoreP1], scoreMap[mScore.scoreP2])
            mScore.scoreP1 > mScore.scoreP2 -> when (controller.serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT, Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT -> ScoreStrings(strAdIn, "")
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT, Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT -> ScoreStrings(strAdOut, "")
            }
            mScore.scoreP1 < mScore.scoreP2 -> when (controller.serving) {
                Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT, Serving.PLAYER3_LEFT, Serving.PLAYER3_RIGHT -> ScoreStrings("", strAdOut)
                Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT, Serving.PLAYER4_LEFT, Serving.PLAYER4_RIGHT -> ScoreStrings("", strAdIn)
            }
            else -> ScoreStrings(strDeuce, strDeuce)
        }
    }
}
