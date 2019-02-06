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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_add_match.*
import org.subhipstercollective.deucelibrary.Key
import org.subhipstercollective.deucelibrary.Serving
import kotlin.random.Random

class ActivityAddMatch : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_match)

        text_num_sets.text = seek_num_sets.progressString

        seek_num_sets.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                text_num_sets.text = seek_num_sets.progressString
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        button_start.setOnClickListener {
            val result = Intent()
            result.putExtra(Key.INTENT_NUM_SETS, seek_num_sets.numSets)
            result.putExtra(
                Key.INTENT_NUM_SETS,
                if (radio_server_me.isChecked || (radio_server_flip.isChecked && Random.nextInt(1) == 0))
                    Serving.PLAYER1_RIGHT
                else
                    Serving.PLAYER2_RIGHT
            )
            result.putExtra(Key.INTENT_ADVANTAGE_SET, toggle_margin_sets.isChecked)
            setResult(R.id.code_request_add_match, result)
            finish()
        }
    }
}
