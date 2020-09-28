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


import android.graphics.Typeface
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_score.view.*
import kotlinx.android.synthetic.main.set.view.*
import net.mqduck.deuce.common.TeamOrNone
import java.util.*
import kotlin.math.roundToInt

class ScoreRecyclerViewAdapter : RecyclerView.Adapter<ScoreRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = mainActivity.matchList[position]

        holder.view.text_team_1.text = match.displayNameTeam1
        holder.view.text_team_2.text = match.displayNameTeam2

        if (match.winner == TeamOrNone.NONE) {
            holder.view.text_date.text = mainActivity.getString(R.string.live)
            holder.view.text_date.setTextColor(getColorCompatibly(R.color.secondary_text_red))
            holder.view.text_date.setTypeface(null, Typeface.BOLD)
        } else {
            holder.view.text_date.text = dateFormat.format(Date(match.startTime))
            holder.view.text_date.setTextColor(getColorCompatibly(R.color.secondary_text))
            holder.view.text_date.setTypeface(null, Typeface.NORMAL)
        }

        when (match.winner) {
            TeamOrNone.NONE -> {
                holder.view.text_team_1.setTypeface(null, Typeface.NORMAL)
                holder.view.text_team_2.setTypeface(null, Typeface.NORMAL)
            }
            TeamOrNone.TEAM1 -> {
                holder.view.text_team_1.setTypeface(null, Typeface.BOLD)
                holder.view.text_team_2.setTypeface(null, Typeface.NORMAL)
            }
            TeamOrNone.TEAM2 -> {
                holder.view.text_team_1.setTypeface(null, Typeface.NORMAL)
                holder.view.text_team_2.setTypeface(null, Typeface.BOLD)
            }
        }

        fun addSetScore(setNum: Int, setNumColor: Int, scoreP1: String, scoreP2: String, winner: TeamOrNone) {
            val set = LayoutInflater.from(mainActivity).inflate(R.layout.set, holder.view.sets_container, false)
            set.set_number.text = setNum.toString()
            set.set_number.setTextColor(setNumColor)
            set.team1_set_score.text = scoreP1
            set.team2_set_score.text = scoreP2

            if (winner == TeamOrNone.TEAM1) {
                set.team1_set_score.setTypeface(set.team1_set_score.typeface, Typeface.BOLD)
            } else if (winner == TeamOrNone.TEAM2) {
                set.team2_set_score.setTypeface(set.team2_set_score.typeface, Typeface.BOLD)
            }

            holder.view.sets_container.addView(set, setLayoutParams)
        }

        holder.view.sets_container.removeAllViews()
        if (match.winner == TeamOrNone.NONE) {
            var i = 0
            while (i < match.sets.size) {
                addSetScore(
                    i + 1,
                    getColorCompatibly(R.color.secondary_text),
                    match.sets[i].scoreP1.toString(),
                    match.sets[i].scoreP2.toString(),
                    match.sets[i].winner
                )
                ++i
            }
            while (i < match.numSets.winMaximum) {
                addSetScore(
                    i + 1,
                    getColorCompatibly(R.color.tertiary_text),
                    "",
                    "",
                    TeamOrNone.NONE
                )
                ++i
            }
        } else {
            for (i in 0 until match.sets.size) {
                addSetScore(
                    i + 1,
                    getColorCompatibly(R.color.secondary_text),
                    match.sets[i].scoreP1.toString(),
                    match.sets[i].scoreP2.toString(),
                    match.sets[i].winner
                )
            }
        }

        holder.view.setOnClickListener { mainActivity.onMatchInteraction(match, position) }
    }

    override fun getItemCount() = mainActivity.matchList.size
}
