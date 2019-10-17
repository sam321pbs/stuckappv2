package com.sammengistu.stuckfirebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.sammengistu.stuckfirebase.constants.AnalyticEventType

class AnalyticsHelper {
    companion object {
        fun postSelectEvent(context: Context, eventType: AnalyticEventType, value: String) {
            val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            val bundle = Bundle()
            bundle.putString(eventType.toString(), value)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        }
    }
}