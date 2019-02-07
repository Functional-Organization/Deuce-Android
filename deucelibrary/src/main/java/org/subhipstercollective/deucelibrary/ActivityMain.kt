package org.subhipstercollective.deucelibrary

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

interface ActivityMain {
    var buttonScoreP1: Button
    var buttonScoreP2: Button
    var textScoreP1: TextView
    var textScoreP2: TextView
    var imageBallP1Left: ImageView
    var imageBallP1Right: ImageView
    var imageBallP2Left: ImageView
    var imageBallP2Right: ImageView
    var textScoresMatchP1: TextView
    var textScoresMatchP2: TextView
}