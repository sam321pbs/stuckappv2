package com.sammengistu.stuckfirebase.database.access

import android.content.Context
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckfirebase.database.DraftPostModel
import com.sammengistu.stuckfirebase.database.PostRepository

class DraftPostAccess(private val context: Context) {
    fun insertPost(post: DraftPostModel) {
        PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).insertPost(post)
    }

    fun getPost(postId: Long) : DraftPostModel? {
        return PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).getPost(postId).value?.get(0)
    }

    fun deletePost(postId: Long) {
        PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).deletePost(postId)
    }
}