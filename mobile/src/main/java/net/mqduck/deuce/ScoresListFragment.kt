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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import net.mqduck.deuce.common.DeuceMatch
import net.mqduck.deuce.common.Match
import net.mqduck.deuce.common.ScoreStack
import net.mqduck.deuce.common.Team

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ScoresListFragment.OnMatchInteractionListener] interface.
 */
class ScoresListFragment : Fragment() {
    lateinit var view: RecyclerView

    /*// TODO: Customize parameters
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_scores_list, container, false) as RecyclerView

        // Set the adapter
        with(view) {
            val scoreLog = ScoreStack()
            for (i in 0 until 48) {
                scoreLog.push(Team.TEAM1)
            }
            adapter = ScoreRecyclerViewAdapter()
        }

        return view
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
        fun onMatchInteraction(item: DeuceMatch, position: Int)
    }

    /*companion object {
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
    }*/
}
