package org.subhipstercollective.deucelibrary

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

interface ActivityMain {
    var buttonScoreP1: Button
    var buttonScoreP2: Button
    var imageBallTopLeft: ImageView
    var imageBallTopRight: ImageView
    var imageBallBottomLeft: ImageView
    var imageBallBottomRight: ImageView
    var textScoresMatchP1: TextView
    var textScoresMatchP2: TextView
}