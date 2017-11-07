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

package org.subhipstercollective.deucelibrary

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar

/**
 * Created by mqduck on 11/6/17.
 */
class SeekBarSets : SeekBar
{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val progressString get() = when(progress)
    {
        0 -> context.getString(R.string.best_of_1)
        1 -> context.getString(R.string.best_of_3)
        2 -> context.getString(R.string.best_of_5)
        3 -> context.getString(R.string.best_of_7)
        else -> "ERROR"
    }

    val numSets get() = when(progress)
    {
        1 -> 3
        2 -> 5
        3 -> 7
        else -> 1
    }
}