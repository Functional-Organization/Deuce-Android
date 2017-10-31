/*
 * Copyright 2017 Jeffrey Thomas Piercy
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

package org.subhipstercollective.deuce

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_activity_main.*
import org.subhipstercollective.deucelibrary.Match
import org.subhipstercollective.deucelibrary.Player

class ActivityMain : AppCompatActivity()
{
    var match = Match(1, 6, 2, 4, 2)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        button_score_p1.setOnClickListener(View.OnClickListener { score(Player.PLAYER1) })
        button_score_p2.setOnClickListener(View.OnClickListener { score(Player.PLAYER2) })

        updateDisplay()
    }

    fun updateDisplay()
    {
        text_heading.setText(String.format("Set %d Game %d", match.setNumber, match.gameNumber))
        var scores = match.currentGame.getScoreStrs()
        text_score_p1.setText(scores[0])
        text_score_p2.setText(scores[1])
    }

    fun score(player: Player)
    {
        var winnerGame = match.currentGame.score(player)
        if(winnerGame != Player.NONE)
        {
            /*
            var winnerStr = if(winnerSet == Player.PLAYER1) "Player 1" else "Player 2"
            text_history.setText(String.format("%s\n%s wins set %d", text_history.text, ))*/

            var winnerSet = match.currentSet.score(winnerGame)
            if(winnerSet != Player.NONE)
            {
                var winnerStr = if(winnerSet == Player.PLAYER1) "Player 1" else "Player 2"
                text_history.setText(String.format("%s\n%s wins set %d", text_history.text, winnerStr, match.setNumber))
                match.addNewSet()
            }
            else
            {
                var winnerStr = if(winnerGame == Player.PLAYER1) "Player 1" else "Player 2"
                text_history.setText(String.format("%s\n%s wins game %d", text_history.text, winnerStr, match.gameNumber))
                match.addNewGame()
            }
        }

        updateDisplay()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId)
        {
            R.id.action_settings -> true
            else                 -> super.onOptionsItemSelected(item)
        }
    }
}
