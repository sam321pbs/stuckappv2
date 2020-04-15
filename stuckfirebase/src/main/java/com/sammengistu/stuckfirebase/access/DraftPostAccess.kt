package com.sammengistu.stuckfirebase.access

import android.content.Context
import com.sammengistu.stuckfirebase.database.AppDatabase
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.repositories.PostRepository

class DraftPostAccess(private val context: Context) {
    fun insertPost(post: PostModel) {
        PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).insertDraftPost(post)
    }

    fun getPost(draftId: Int) : PostModel? {
        return PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).getDraftPost(draftId).value?.get(0)
    }

    fun deletePost(draftId: Int) {
        PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).deleteDraftPost(draftId)
    }
}