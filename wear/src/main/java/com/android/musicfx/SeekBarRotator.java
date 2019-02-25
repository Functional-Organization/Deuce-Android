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
package com.android.musicfx;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/*
 *  This ViewGroup contains a single view, which will be rotated by 90 degrees counterclockwise.
 */
public class SeekBarRotator extends ViewGroup {
    public SeekBarRotator(Context context) {
        super(context);
    }

    public SeekBarRotator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarRotator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SeekBarRotator(Context context, AttributeSet attrs, int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View child = getChildAt(0);
        if (child.getVisibility() != GONE) {
            // swap width and height for child
            measureChild(child, heightMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(
                    child.getMeasuredHeightAndState(),
                    child.getMeasuredWidthAndState());
        } else {
            setMeasuredDimension(
                    resolveSizeAndState(0, widthMeasureSpec, 0),
                    resolveSizeAndState(0, heightMeasureSpec, 0));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View child = getChildAt(0);
        if (child.getVisibility() != GONE) {
            // rotate the child 90 degrees counterclockwise around its upper-left
            child.setPivotX(0);
            child.setPivotY(0);
            child.setRotation(-90);
            // place the child below this view, so it rotates into view
            int mywidth = r - l;
            int myheight = b - t;
            int childwidth = myheight;
            int childheight = mywidth;
            child.layout(0, myheight, childwidth, myheight + childheight);
        }
    }
}