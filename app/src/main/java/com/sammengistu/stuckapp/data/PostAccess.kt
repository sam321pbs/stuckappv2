package com.sammengistu.stuckapp.data

import android.content.Context

class PostAccess {
    companion object {
        fun insertPost(context: Context, post: Post) {
            PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).insertPost(post)
        }

        fun deletePost(context: Context, post: Post) {
            PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).deletePost(post)
        }
    }
}