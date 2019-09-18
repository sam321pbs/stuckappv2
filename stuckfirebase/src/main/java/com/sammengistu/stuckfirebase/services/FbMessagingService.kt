package com.sammengistu.stuckfirebase.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.access.DeviceTokenAccess
import com.sammengistu.stuckfirebase.data.DeviceTokenModel

class FbMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New token - $token")

        UserHelper.getCurrentUser { user ->
            if (user != null) {
                val deviceToken = DeviceTokenModel(user.userId, user.ref, token)
                DeviceTokenAccess(user.userId).createItemInFB(deviceToken)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    companion object {
        val TAG = FbMessagingService::class.java.simpleName
    }
}