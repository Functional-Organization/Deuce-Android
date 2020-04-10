/*
 * Copyright (C) 2019 Jeffrey Thomas Piercy
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
import androidx.annotation.RequiresApi

class SeekBarSets : SeekBar {
    companion object {
        const val BEST_OF_1 = 0
        const val BEST_OF_3 = 1
        const val BEST_OF_5 = 2
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(21)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    val progressString: String
        get() = when (progress) {
            BEST_OF_1 -> context.getString(R.string.best_of_1)
            BEST_OF_3 -> context.getString(R.string.best_of_3)
            BEST_OF_5 -> context.getString(R.string.best_of_5)
            else -> "ERROR"
        }

    val numSets
        get() = when (progress) {
            BEST_OF_1 -> 1
            BEST_OF_3 -> 3
            BEST_OF_5 -> 5
            else -> throw IllegalArgumentException("Invalid number of sets progress bar value")
        }
}
