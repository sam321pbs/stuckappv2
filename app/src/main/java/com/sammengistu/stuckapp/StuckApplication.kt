package com.sammengistu.stuckapp

import android.app.Application
import com.facebook.stetho.Stetho
import com.sammengistu.stuckapp.notification.StunckNotificationFactory

class StuckApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
        StunckNotificationFactory(this)
    }
}