package com.sammengistu.stuckfirebase.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.models.CommentModel
import com.sammengistu.stuckfirebase.models.CommentVoteModel
import com.sammengistu.stuckfirebase.repositories.CommentsRepository

private val TAG = CommentsViewModel::class.java.simpleName

class CommentsViewModel(
    private val repository: CommentsRepository
) : ViewModel() {

    private var postRef: String = ""
    private val _userRef = MutableLiveData<String>()

    val commentsLiveData : LiveData<List<CommentModel>?> =
        Transformations.switchMap(_userRef) {userREf ->
            if (userREf.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                repository.getItemsWherePostRefEquals(postRef)
            }
        }

    val commentVotesLiveData : LiveData<HashMap<String, CommentVoteModel>> =
        Transformations.switchMap(_userRef) {userREf ->
            if (userREf.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                repository.getCommentVotes(userREf, postRef)
            }
        }

    fun setUserAndPostRef(userRef: String, postRef: String) {
        this.postRef = postRef
        _userRef.value = userRef
    }

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