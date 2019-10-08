package com.sammengistu.stuckfirebase.constants

import com.sammengistu.stuckfirebase.BuildConfig


class FirebaseConstants {
    companion object {
        val ENVIRONMENT_COLLECTION = if (BuildConfig.DEBUG) "debug" else "release"
    }
}

// Collections naming
const val POSTS = "posts"
const val USERS = "users"
const val USER_VOTES = "user_votes"
const val COMMENT_VOTES = "comments_votes"
const val POST_COMMENTS = "post_comments"
const val STARRED_POSTS = "starred_posts"
const val DEVICE_TOKENS = "device_tokens"
const val REPORTS = "reports"


// FCM data keys
const val KEY_TITLE = "title"
const val KEY_BODY = "body"
const val KEY_TARGET_REF = "targetRef"
const val KEY_POST_REF = "postId"
const val KEY_TAG = "tag"
const val KEY_DAILY_NOTIFY = "daily_notify"
