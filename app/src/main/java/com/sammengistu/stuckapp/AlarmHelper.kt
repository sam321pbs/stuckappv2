package com.sammengistu.stuckapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sammengistu.stuckapp.constants.PendingIntentRequestCodes.Companion.REQUEST_DAILY_NOTIFIER
import com.sammengistu.stuckapp.receiver.StuckReceiver
import com.sammengistu.stuckapp.utils.DateUtils.Companion.MS_ONE_DAY

class AlarmHelper {
    companion object {
        fun setDailyNotifier(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, StuckReceiver::class.java)
            intent.action = StuckReceiver.ACTION_DAILY_NOTIFIER
            val interval = MS_ONE_DAY
            val firstTrigger = System.currentTimeMillis() + interval
            val pIntent = PendingIntent.getBroadcast(
                context, REQUEST_DAILY_NOTIFIER, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.setRepeating(
                AlarmManager.RTC,
                firstTrigger,
                interval,
                pIntent
            )
        }

        fun cancelDailyNotifier(context: Context) {
            val intent = Intent(context, StuckReceiver::class.java)
            val sender = PendingIntent.getBroadcast(
                context,
                REQUEST_DAILY_NOTIFIER,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
        }
    }
}