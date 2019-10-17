package com.sammengistu.stuckapp.helpers

import android.graphics.Rect
import android.util.Log
import android.view.View

class KeyboardStateHelper(view: View, private val onKeyBoardChanged: (open: Boolean) -> Unit) {
    var isKeyboardShowing = false

    init {
        // ContentView is the root view of the layout of this activity/fragment
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom

            Log.d(TAG, "keypadHeight = $keypadHeight")

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                    onKeyboardVisibilityChanged(true)
                }
            } else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                    onKeyboardVisibilityChanged(false)
                }
            }
        }
    }

    private fun onKeyboardVisibilityChanged(opened: Boolean) {
        onKeyBoardChanged(opened)
    }

    companion object {
        val TAG = KeyboardStateHelper::class.java.simpleName
    }
}