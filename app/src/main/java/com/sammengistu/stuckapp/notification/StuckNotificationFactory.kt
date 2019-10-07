package com.sammengistu.stuckapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.SplashScreenActivity
import com.sammengistu.stuckapp.helpers.UserPrefHelper
import com.sammengistu.stuckfirebase.NotificationFactory
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.constants.KEY_BODY
import com.sammengistu.stuckfirebase.constants.KEY_POST_REF
import com.sammengistu.stuckfirebase.constants.KEY_TAG
import com.sammengistu.stuckfirebase.constants.KEY_TITLE
import java.util.*

class StuckNotificationFactory(context: Context) : NotificationFactory() {

    init {
        instance = this
        createNotificationChannel(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createCommentsChannel(context)
            createVotesChannel(context)
        }
    }

    private fun createCommentsChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Comments"
            val descriptionText = "Get notified about comments on your post"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(COMMENTS_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createVotesChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Votes"
            val descriptionText = "Get notified about votes on your post"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(VOTES_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun createNotification(context: Context, data: Map<String, String>) {
        val channelId: String = when {
            data[KEY_TAG] == "comment" -> COMMENTS_CHANNEL_ID
            data[KEY_TAG] == "vote" -> VOTES_CHANNEL_ID
            else -> return
        }

        UserHelper.getCurrentUser { user ->
            if (user != null) {
                if (data[KEY_TAG] == "comment" && UserPrefHelper.getCommentsPref(context, user) ||
                    data[KEY_TAG] == "vote" && UserPrefHelper.getVotesPref(context, user)) {
                    val builder = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.s_stuck_transparent)
                        .setContentTitle(data[KEY_TITLE])
                        .setContentText(data[KEY_BODY])
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(createPendingIntent(context, data[KEY_POST_REF]))
                        .setAutoCancel(true)

                    val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    notificationManager.notify(Random().nextInt(), builder.build())
                }
            }
        }
    }

    private fun createPendingIntent(context: Context, postRef: String?) : PendingIntent? {
        if (postRef == null) {
            return null
        }
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, SplashScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        Log.d(TAG, "Push notification ref : $postRef")
        intent.putExtra(EXTRA_POST_REF, postRef)
       return PendingIntent.getActivity(context, REQUEST_SHOW_POST, intent, FLAG_UPDATE_CURRENT)
    }

    companion object {
        val TAG = StuckNotificationFactory::class.java.simpleName
        const val COMMENTS_CHANNEL_ID = "comments"
        const val VOTES_CHANNEL_ID = "votes"
        const val REQUEST_SHOW_POST = 10100
        const val EXTRA_POST_REF = "post_ref"
    }
}