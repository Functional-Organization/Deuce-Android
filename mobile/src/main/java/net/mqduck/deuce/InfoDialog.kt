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

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.info_dialog.view.*
import net.mqduck.deuce.common.Match
import java.util.*

class InfoDialog(val match: Match, val scoresFragment: ScoresFragment) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.info_dialog, null)
            view.start_time.text = timeFormat.format(Date(match.startTime))
            if (match.endTime >= 0) {
                view.label_end_time.visibility = View.VISIBLE
                view.end_time.text = timeFormat.format(Date(match.endTime))
            } else {
                view.label_end_time.visibility = View.INVISIBLE
            }
            view.edit_name_team1.setText(match.nameTeam1)
            view.edit_name_team2.setText(match.nameTeam2)

            builder.setView(view)
                .setPositiveButton("Positive") { dialog, id ->
                    Log.d("foo", "positive")
                }
                .setNegativeButton("Negative") { dialog, which ->
                    Log.d("foo", "negative")
                }
            val dialog = builder.create()
            dialog.show()
            val buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val buttonNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

//                buttonPositive.isEnabled = false
//                buttonNegative.isEnabled = false
            buttonPositive.visibility = View.GONE
            buttonNegative.visibility = View.GONE

            fun updateButtons() {
                if (view.edit_name_team1.text.toString() == match.nameTeam1
                    && view.edit_name_team2.text.toString() == match.nameTeam2
                ) {
                    buttonPositive.visibility = View.GONE
                    buttonNegative.visibility = View.GONE
                } else {
                    buttonPositive.visibility = View.VISIBLE
                    buttonNegative.visibility = View.VISIBLE
                }
            }

            view.edit_name_team1.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = updateButtons()
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            view.edit_name_team2.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = updateButtons()
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            buttonPositive.setOnClickListener {
                match.nameTeam1 = view.edit_name_team1.text.toString()
                match.nameTeam2 = view.edit_name_team2.text.toString()
                scoresFragment.view.adapter?.notifyDataSetChanged()
                dismiss()
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}