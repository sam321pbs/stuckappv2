package com.sammengistu.stuckapp.helpers

import android.content.Context
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository

class UserPrefHelper {
    companion object {
        private const val NOTIFICATION_PREF = "notification_pref"
        private const val KEY_NOTIFICATION_VOTES = "_notification_votes"
        private const val KEY_NOTIFICATION_COMMENTS = "_notification_comments"

        fun addVotesPref(context: Context, enabled: Boolean) {
            UserRepository.getUserInstance(context) { user ->
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
            UserRepository.getUserInstance(context) { user ->
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