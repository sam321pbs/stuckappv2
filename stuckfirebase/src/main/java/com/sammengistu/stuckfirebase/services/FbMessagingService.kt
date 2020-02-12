package com.sammengistu.stuckfirebase.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sammengistu.stuckfirebase.NotificationFactory
import com.sammengistu.stuckfirebase.access.DeviceTokenAccess
import com.sammengistu.stuckfirebase.constants.KEY_TARGET_REF
import com.sammengistu.stuckfirebase.models.DeviceTokenModel
import com.sammengistu.stuckfirebase.repositories.UserRepository

class FbMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New token - $token")

        UserRepository.getUserInstance(this) { user ->
            if (user != null) {
                val deviceToken = DeviceTokenModel(user.ref, token)
                DeviceTokenAccess(user.ref).createItemInFB(deviceToken)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val targetRef = message.data.getValue(KEY_TARGET_REF)
        UserRepository.getUserInstance(this) { user ->
            if (user != null && user.ref == targetRef && NotificationFactory.instance != null) {
                Log.d(TAG, "Notifying user")
                NotificationFactory.instance!!.createNotification(this, message.data)
            }
//            NotificationFactory.instance!!.createNotification(this, message.data)
        }
    }

    companion object {
        val TAG = FbMessagingService::class.java.simpleName
    }
}