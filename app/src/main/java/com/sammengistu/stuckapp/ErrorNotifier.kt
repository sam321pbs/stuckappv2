package com.sammengistu.stuckapp

import android.content.Context
import android.util.Log
import android.widget.Toast

class ErrorNotifier {
    companion object {
        val TAG = ErrorNotifier::class.java.simpleName

        fun notifyError(context: Context, tag: String, message: String, e: Exception) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.e(tag, message, e)
        }

        fun notifyError(context: Context, tag: String, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.e(tag, message)
        }

        fun notifyError(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.e(TAG, message)
        }
    }
}