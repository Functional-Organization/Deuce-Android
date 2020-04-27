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
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.info_dialog.view.*
import net.mqduck.deuce.common.*
import java.util.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ScoresFragment.OnMatchInteractionListener] interface.
 */
class ScoresFragment : Fragment() {
    class InfoDialog(val match: Match) : DialogFragment() {
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
                /*.setPositiveButton("Positive", DialogInterface.OnClickListener { dialog, id ->
                    Log.d("foo", "positive")
                })*/
                /*.setNegativeButton("Negative", DialogInterface.OnClickListener { dialog, which ->
                    Log.d("foo", "negative")
                })*/
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnMatchInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scores_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                //adapter = ScoreRecyclerViewAdapter(DummyContent.ITEMS, listener)

                val scoreLog = ScoreStack()
                for (i in 0 until 72) {
                    scoreLog.push(Team.TEAM1)
                }
                val matches = arrayListOf(
                    DeuceMatch(),
                    DeuceMatch(
                        3,
                        Team.TEAM1,
                        OvertimeRule.TIEBREAK,
                        MatchType.SINGLES,
                        416846345451,
                        416847346451,
                        scoreLog,
                        "Myself",
                        "Opponent"
                    )
                )
                adapter = ScoreRecyclerViewAdapter(
                    matches,
                    listener,
                    mainActivity
                )
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMatchInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnMatchInteractionListener {
        fun onMatchInteraction(item: Match?)
    }

    companion object {
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ScoresFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
