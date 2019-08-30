package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.GestureDetector

class DoubleTapGesture(context: Context, listener: DoubleTapListener)
    : GestureDetector(context, DoubleTapGestureListener(listener)) {

    interface DoubleTapListener {
        fun onDoubleTapped()
    }
}

