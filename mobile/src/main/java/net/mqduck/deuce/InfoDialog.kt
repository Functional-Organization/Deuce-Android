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
import android.view.View
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.info_dialog.view.*
import net.mqduck.deuce.common.*
import java.util.*

class InfoDialog(
    private val match: DeuceMatch,
    private val position: Int,
    private val scoresListFragment: ScoresListFragment
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.info_dialog, scoresListFragment.view, false)
        view.start_time.text = timeFormat.format(Date(match.startTime))
        if (match.winner != TeamOrNone.NONE) {
            view.label_end_time.visibility = View.VISIBLE
            view.end_time.text = timeFormat.format(Date(match.setEndTimes.last()))
        } else {
            view.label_end_time.visibility = View.INVISIBLE
        }
        view.edit_name_team1.setText(match.displayNameTeam1)
        view.edit_name_team2.setText(match.displayNameTeam2)

        builder.setView(view)
            .setPositiveButton(resources.getString(R.string.save_changes)) { _, _ ->
                match.nameTeam1 = view.edit_name_team1.text.toString().trim()
                match.nameTeam2 = view.edit_name_team2.text.toString().trim()
                scoresListFragment.view.adapter?.notifyItemChanged(position)
                mainActivity.matchList.writeToFile()
                if (match.winner == TeamOrNone.NONE) {
                    syncData(mainActivity.dataClient, PATH_UPDATE_NAMES, true) { dataMap ->
                        dataMap.putString(KEY_NAME_TEAM1, match.nameTeam1)
                        dataMap.putString(KEY_NAME_TEAM2, match.nameTeam2)
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.close)) { _, _ -> }
        val dialog = builder.create()
        dialog.show()

        val buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        buttonPositive.visibility = View.INVISIBLE

        fun updateButtons() {
            buttonPositive.visibility = if (
                view.edit_name_team1.text.toString() == match.displayNameTeam1
                && view.edit_name_team2.text.toString() == match.displayNameTeam2
            )
                View.INVISIBLE
            else
                View.VISIBLE
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

        return dialog
    }
}