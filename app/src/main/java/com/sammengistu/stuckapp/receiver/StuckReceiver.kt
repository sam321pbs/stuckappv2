package com.sammengistu.stuckapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sammengistu.stuckapp.constants.DailyNotifyMessages
import com.sammengistu.stuckfirebase.NotificationFactory
import com.sammengistu.stuckfirebase.constants.KEY_DAILY_NOTIFY

class StuckReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!intent.action.isNullOrEmpty()) {
            when (intent.action) {
                ACTION_DAILY_NOTIFIER -> {
                    val data = mapOf(Pair(KEY_DAILY_NOTIFY, DailyNotifyMessages.getRandomMessage()))
                    NotificationFactory.instance?.createNotification(context, data)
                }
            }
            StringBuilder().apply {
                append("Action: ${intent.action}\n")
                toString().also { log ->
                    Log.d(TAG, log)
                }
            }
        }
    }

    companion object {
        val TAG = StuckReceiver::class.java.simpleName
        const val ACTION_DAILY_NOTIFIER = "ACTION_DAILY_NOTIFIER"
    }
}