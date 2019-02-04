package org.subhipstercollective.deuce

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.Button

class ScoreButton(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    init {
        View.inflate(context, R.layout.score_button, this)
    }
}
