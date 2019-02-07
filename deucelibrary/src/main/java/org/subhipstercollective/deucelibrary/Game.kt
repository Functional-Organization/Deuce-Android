package org.subhipstercollective.deucelibrary

import android.content.Context

class Game(winMinimum: Int, winMargin: Int) {
    companion object {
        private var strLove: String = ""

        fun init(context: Context) {
            strLove = context.resources.getString(R.string.love)
        }
    }

    private val mScore = Score(winMinimum, winMargin)

    fun score(player: Player = Player.NONE) = mScore.score(player)

    private fun mapScore(score: Int) = when (score) {
        0    -> Game.strLove
        1    -> "15"
        2    -> "30"
        3    -> "40"
        else -> "ERROR"
    }

    fun getScoreStrs() = when {
        mScore.winner == Player.PLAYER1          -> ScoreStrings("\uD83C\uDFC6", "")
        mScore.winner == Player.PLAYER2          -> ScoreStrings("", "\uD83C\uDFC6")
        mScore.scoreP1 < 3 || mScore.scoreP2 < 3 -> ScoreStrings(mapScore(mScore.scoreP1), mapScore(mScore.scoreP2))
        mScore.scoreP1 > mScore.scoreP2          -> ScoreStrings("Advantage", "")
        mScore.scoreP1 < mScore.scoreP2          -> ScoreStrings("", "Advantage")
        else                                     -> ScoreStrings("Deuce", "Deuce")
    }
}
