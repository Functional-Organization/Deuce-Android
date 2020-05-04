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


import android.app.Activity
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_score.view.*
import kotlinx.android.synthetic.main.set.view.*
import net.mqduck.deuce.ScoresFragment.OnMatchInteractionListener
import net.mqduck.deuce.common.Match
import net.mqduck.deuce.common.Winner
import java.util.*
import kotlin.math.roundToInt

//import net.mqduck.deuce.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnMatchInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
/*class ScoreRecyclerViewAdapter(
    private val mValues: List<DummyItem>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ScoreRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        holder.mContentView.text = item.content

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}*/

class ScoreRecyclerViewAdapter(
    private val matches: List<Match>,
    private val listener: OnMatchInteractionListener?,
    private val context: Activity
) : RecyclerView.Adapter<ScoreRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val date = view.text_date
        val nameTeam1 = view.text_team_1
        val nameTeam2 = view.text_team_2
        val setsContainer = view.sets_container as LinearLayout
    }

    companion object {
        val setLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        init {
            val margin = (8.0
                    * mainActivity.resources.displayMetrics.densityDpi.toFloat()
                    / DisplayMetrics.DENSITY_DEFAULT.toFloat()
                    ).roundToInt()
            setLayoutParams.setMargins(margin, 0, margin, 0)
        }
    }

    private val onClickListener: View.OnClickListener

    init {
        /*mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }*/
        onClickListener = View.OnClickListener { view ->
            val match = view.tag as Match
            listener?.onMatchInteraction(match)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matches[position]

        holder.date.text = dateFormat.format(Date(match.playTimes.startTime))
        holder.nameTeam1.text = match.nameTeam1
        holder.nameTeam2.text = match.nameTeam2

        if (match.winner == Winner.TEAM1) {
            holder.nameTeam1.setTypeface(holder.nameTeam1.typeface, Typeface.BOLD)
        } else if (match.winner == Winner.TEAM2) {
            holder.nameTeam2.setTypeface(holder.nameTeam2.typeface, Typeface.BOLD)
        }

        holder.setsContainer.removeAllViews()
        for (i in 0 until match.sets.size) {
            val set = LayoutInflater.from(context).inflate(R.layout.set, holder.setsContainer, false)
            set.set_number.text = (i + 1).toString()
            set.team1_set_score.text = match.sets[i].scoreP1.toString()
            set.team2_set_score.text = match.sets[i].scoreP2.toString()

            if (match.winner == Winner.TEAM1) {
                set.team1_set_score.setTypeface(set.team1_set_score.typeface, Typeface.BOLD)
            } else if (match.winner == Winner.TEAM2) {
                set.team2_set_score.setTypeface(set.team2_set_score.typeface, Typeface.BOLD)
            }

            holder.setsContainer.addView(set, setLayoutParams)
        }

        with(holder.view) {
            tag = match
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = matches.size
}
