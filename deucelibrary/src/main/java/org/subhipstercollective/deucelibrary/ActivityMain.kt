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
    var imageBallP1LeftServing: ImageView
    var imageBallP1RightServing: ImageView
    var imageBallP2LeftServing: ImageView
    var imageBallP2RightServing: ImageView
    var imageBallP1LeftNotServing: ImageView
    var imageBallP1RightNotServing: ImageView
    var imageBallP2LeftNotServing: ImageView
    var imageBallP2RightNotServing: ImageView
    var textScoresMatchP1: TextView
    var textScoresMatchP2: TextView
    val context: Context
}