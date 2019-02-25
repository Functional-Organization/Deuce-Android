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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

interface ActivityMain {
    var buttonScoreP1: Button
    var buttonScoreP2: Button
    var textScoreP1: TextView
    var textScoreP2: TextView
    var imageBallServingT1: ImageView
    var imageBallNotservingT1: ImageView
    var imageBallServingT2: ImageView
    var imageBallNotservingT2: ImageView
    var textScoresMatchP1: TextView
    var textScoresMatchP2: TextView
    val context: Context

    var posXBallLeftT1: Float
    var posXBallRightT1: Float
    var posXBallLeftT2: Float
    var posXBallRightT2: Float
}