package org.subhipstercollective.deucelibrary

import android.content.Context

class Game(winMinimum: Int, winMargin: Int, private val controller: ControllerMain, val tiebreak: Boolean = false) {
    companion object {
        private var strLove: String = ""

        fun init(context: Context) {
            strLove = context.resources.getString(R.string.love)
        }
    }

    private val mScore = Score(winMinimum, winMargin)

    fun score(player: Player = Player.NONE) = mScore.score(player)

    private fun mapScore(score: Int): String {
        return if (tiebreak) {
            score.toString()
        } else {
            when (score) {
                0 -> Game.strLove
                1 -> "15"
                2 -> "30"
                3 -> "40"
                else -> "ERROR"
            }
        }
    }

    fun getScore(player: Player) = mScore.getScore(player)

    fun getScoreStrs() = when {
        mScore.winner == Player.PLAYER1 -> ScoreStrings("\uD83C\uDFC6", "")
        mScore.winner == Player.PLAYER2 -> ScoreStrings("", "\uD83C\uDFC6")
        mScore.scoreP1 < 3 || mScore.scoreP2 < 3 -> ScoreStrings(mapScore(mScore.scoreP1), mapScore(mScore.scoreP2))
        mScore.scoreP1 > mScore.scoreP2 -> when (controller.serving) {
            Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT -> ScoreStrings(controller.activityMain.context.getString(R.string.ad_in), "")
            Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT -> ScoreStrings(controller.activityMain.context.getString(R.string.ad_out), "")
        }
        mScore.scoreP1 < mScore.scoreP2 -> when (controller.serving) {
            Serving.PLAYER1_LEFT, Serving.PLAYER1_RIGHT -> ScoreStrings("", controller.activityMain.context.getString(R.string.ad_out))
            Serving.PLAYER2_LEFT, Serving.PLAYER2_RIGHT -> ScoreStrings("", controller.activityMain.context.getString(R.string.ad_in))
        }
        else -> ScoreStrings(controller.activityMain.context.getString(R.string.deuce), controller.activityMain.context.getString(R.string.deuce))
    }
}
