package com.sammengistu.stuckfirebase.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.models.CommentModel
import com.sammengistu.stuckfirebase.repositories.CommentsRepository

private val TAG = CommentsViewModel::class.java.simpleName

class CommentsViewModel(
    private val repository: CommentsRepository,
    userRef: String, postRef: String
) : ViewModel() {
    val commentsLiveData = repository.getItemsWherePostRefEquals(postRef)
    val commentVotesLiveData = repository.getCommentVotes(userRef, postRef)

    fun createComment(commentModel: CommentModel) {
        repository.createComment(commentModel,
            object : FirebaseItemAccess.OnItemCreated<CommentModel> {
                override fun onSuccess(item: CommentModel) {
                    val list = commentsLiveData.value as ArrayList
                    list.add(item)
                    (commentsLiveData as MutableLiveData).value = list
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Failed to create comment", e)
                }
            })
    }
}