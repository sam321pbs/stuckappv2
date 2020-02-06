package com.sammengistu.stuckfirebase.access

import android.content.Context
import com.sammengistu.stuckfirebase.database.AppDatabase
import com.sammengistu.stuckfirebase.models.DraftPostModel
import com.sammengistu.stuckfirebase.repositories.PostRepository

class DraftPostAccess(private val context: Context) {
    fun insertPost(post: DraftPostModel) {
        PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).insertDraftPost(post)
    }

    fun getPost(postId: Long) : DraftPostModel? {
        return PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).getDraftPost(postId).value?.get(0)
    }

    fun deletePost(postId: Long) {
        PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).deleteDraftPost(postId)
    }
}