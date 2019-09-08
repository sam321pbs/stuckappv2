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
const val COMMENTS = "comments"
const val POST_COMMENTS = "post_comments"
const val STARRED_POSTS = "starred_posts"
const val VOTES = "votes"
const val COMMENT_UP_VOTES = "comment_up_votes"

// Document naming
const val USER_STATS = "user_stats"
const val APP_STATS = "app_stats"
