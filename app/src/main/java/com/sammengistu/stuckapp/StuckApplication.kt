package com.sammengistu.stuckapp

import android.app.Application
import com.facebook.stetho.Stetho
import com.sammengistu.stuckapp.notification.StuckNotificationFactory

class StuckApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
        StuckNotificationFactory(this)
    }
}