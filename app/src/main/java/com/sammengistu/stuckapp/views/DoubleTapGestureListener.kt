package com.sammengistu.stuckapp.views

import android.view.GestureDetector
import android.view.MotionEvent

class DoubleTapGestureListener(val listener: DoubleTapGesture.DoubleTapListener)
    : GestureDetector.SimpleOnGestureListener() {

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        listener.onDoubleTapped()
        return true
    }

}