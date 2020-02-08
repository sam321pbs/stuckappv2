package com.sammengistu.stuckfirebase.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sammengistu.stuckfirebase.access.CommentAccess
import com.sammengistu.stuckfirebase.access.CommentsVoteAccess
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.models.CommentModel
import com.sammengistu.stuckfirebase.models.CommentVoteModel
import java.util.*
import kotlin.collections.HashMap

private val TAG = CommentsRepository::class.java.simpleName

class CommentsRepository private constructor(private val commentAccess: CommentAccess,
                         private val commentsVoteAccess: CommentsVoteAccess) {

    fun getCommentVotes(userRef: String, postRef: String): LiveData<HashMap<String, CommentVoteModel>> {
        val liveData = MutableLiveData<HashMap<String, CommentVoteModel>>()

        commentsVoteAccess.getCommentVotesForPost(userRef, postRef, object :
            FirebaseItemAccess.OnItemsRetrieved<CommentVoteModel> {
            override fun onSuccess(list: List<CommentVoteModel>) {
                val map = HashMap<String, CommentVoteModel>()
                for (commentVote in list) {
                    map[commentVote.commentRef] = commentVote
                }
                liveData.value = map
            }

            override fun onFailed(e: Exception) {
                Log.e(TAG, "Error loading comments votes", e)
                liveData.value = null
            }
        })
        return liveData
    }

    fun getItemsWherePostRefEquals(postRef: String): LiveData<List<CommentModel>> {
        val liveData = MutableLiveData<List<CommentModel>>()

        commentAccess.getItemsWhereEqual(
            "postRef",
            postRef,
            object : FirebaseItemAccess.OnItemsRetrieved<CommentModel> {
                override fun onSuccess(list: List<CommentModel>) {
                    liveData.value = ArrayList(list.reversed())
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Error loading comments", e)
                    liveData.value = null
                }
            }
        )

        return liveData
    }

    fun createComment(commentModel: CommentModel,
                      listener: FirebaseItemAccess.OnItemCreated<CommentModel>) {
        CommentAccess().createItemInFB(commentModel, listener)
    }

    companion object {
        @Volatile private var instance: CommentsRepository? = null

        fun getInstance(commentAccess: CommentAccess, commentsVoteAccess: CommentsVoteAccess) =
            instance
                ?: synchronized(this) {
                    instance
                        ?: CommentsRepository(
                            commentAccess,
                            commentsVoteAccess
                        ).also { instance = it }
                }
    }
}