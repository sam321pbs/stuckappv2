package com.sammengistu.stuckapp.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {

        // MS = milliseconds
        const val MS_ONE_SEC: Long = 1000
        const val MS_ONE_MIN: Long = MS_ONE_SEC * 60
        const val MS_ONE_HOUR: Long = MS_ONE_MIN * 60
        const val MS_ONE_DAY: Long = MS_ONE_HOUR * 24
        
        fun convertDateToTimeElapsed(date: Date?): String {
            if (date == null) {
                return "N/A"
            }

            val currentDateInSec = Calendar.getInstance().timeInMillis / 1000
            val dateInSec = date.time / 1000
            val secDifference = currentDateInSec - dateInSec

            return when {
                secDifference < 60 -> return "${secDifference.toInt()} sec(s) ago"
                secToHours(secDifference).toInt() < 1 -> return "${secToMin(secDifference).toInt()} min(s) ago"
                secToDays(secDifference) < 1 -> return "${secToHours(secDifference).toInt()} hour(s) ago"
                secToDays(secDifference) < 365 -> return "${secToDays(secDifference).toInt()} day(s) ago"
                else -> getSimpleDate(date)
            }
        }

        fun secToMin(sec: Long) = sec / 60

        fun secToHours(sec: Long) = secToMin(sec) / 60

        fun secToDays(sec: Long) = secToHours(sec) / 24

        fun getSimpleDate(date: Date): String = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                .format(date)
    }
}