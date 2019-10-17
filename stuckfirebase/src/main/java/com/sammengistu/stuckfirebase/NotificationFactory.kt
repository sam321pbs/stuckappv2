package com.sammengistu.stuckfirebase

import android.content.Context

abstract class NotificationFactory {
    companion object {
        var instance: NotificationFactory? = null
    }

    abstract fun createNotification(context: Context, data: Map<String, String>)
}