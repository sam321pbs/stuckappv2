package com.sammengistu.stuckapp.helpers

import android.content.Context
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.models.UserModel

class UserPrefHelper {
    companion object {
        const val NOTIFICATION_PREF = "notification_pref"
        const val KEY_NOTIFICATION_VOTES = "_notification_votes"
        const val KEY_NOTIFICATION_COMMENTS = "_notification_comments"

        fun addVotesPref(context: Context, enabled: Boolean) {
            UserHelper.getCurrentUser { user ->
                if (user != null) {
                    val pref = context.getSharedPreferences(NOTIFICATION_PREF, Context.MODE_PRIVATE)
                    pref.edit().putBoolean("${user.ref}$KEY_NOTIFICATION_VOTES", enabled).apply()
                }
            }
        }

        fun getVotesPref(context: Context, user: UserModel): Boolean {
            val pref = context.getSharedPreferences(NOTIFICATION_PREF, Context.MODE_PRIVATE)
            return pref.getBoolean("${user.ref}$KEY_NOTIFICATION_VOTES", true)
        }

        fun addCommentsPref(context: Context, enabled: Boolean) {
            UserHelper.getCurrentUser { user ->
                if (user != null) {
                    val pref = context.getSharedPreferences(NOTIFICATION_PREF, Context.MODE_PRIVATE)
                    pref.edit().putBoolean("${user.ref}$KEY_NOTIFICATION_COMMENTS", enabled).apply()
                }
            }
        }

        fun getCommentsPref(context: Context, user: UserModel): Boolean {
            val pref = context.getSharedPreferences(NOTIFICATION_PREF, Context.MODE_PRIVATE)
            return pref.getBoolean("${user.ref}$KEY_NOTIFICATION_COMMENTS", true)
        }
    }
}