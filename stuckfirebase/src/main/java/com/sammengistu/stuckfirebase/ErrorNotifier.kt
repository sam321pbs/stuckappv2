package com.sammengistu.stuckfirebase

import android.content.Context
import android.util.Log
import android.widget.Toast

class ErrorNotifier {
    companion object {
        val TAG = ErrorNotifier::class.java.simpleName

        fun notifyError(context: Context?, tag: String, message: String, e: Exception) {
            if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.e(tag, message, e)
        }

        fun notifyError(context: Context?, tag: String, message: String) {
            if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.e(tag, message)
        }

        fun notifyError(context: Context?, message: String) {
            if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.e(TAG, message)
        }
    }
}