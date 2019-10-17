package com.sammengistu.stuckapp.helpers

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation


class AnimationHelper {
    companion object {
        fun startRotateAnimation(view: View) {
            val rotate = RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 800
            rotate.interpolator = LinearInterpolator()
            rotate.repeatCount = Animation.INFINITE
            view.startAnimation(rotate)
        }
    }
}