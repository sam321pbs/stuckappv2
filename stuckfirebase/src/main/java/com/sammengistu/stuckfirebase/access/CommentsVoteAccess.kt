package com.sammengistu.stuckfirebase.access

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.sammengistu.stuckfirebase.constants.COMMENT_VOTES
import com.sammengistu.stuckfirebase.models.CommentVoteModel

class CommentsVoteAccess : FirebaseItemAccess<CommentVoteModel>() {

    override fun getModelClass(): Class<CommentVoteModel> {
        return CommentVoteModel::class.java
    }

    override fun getCollectionRef(): CollectionReference {
        return getEnvironmentCollectionRef(COMMENT_VOTES)
    }

    override fun onItemCreated(item: CommentVoteModel) {
        super.onItemCreated(item)
        if (item.voteType == UP_VOTE) {
            CommentAccess().incrementField(item.commentRef, "upVotes")
        } else if (item.voteType == DOWN_VOTE) {
            CommentAccess().incrementField(item.commentRef, "upVotes", -1)
        }
    }

    override fun onItemDeleted(item: CommentVoteModel?) {
        super.onItemDeleted(item)
        if (item != null) {
            if (item.voteType == UP_VOTE) {
                CommentAccess().incrementField(item.commentRef, "upVotes", -1)
            } else if (item.voteType == DOWN_VOTE) {
                CommentAccess().incrementField(item.commentRef, "upVotes")
            }
        }
    }

    fun getCommentVotesForPost(ownerRef: String, postRef: String,
                               listener: OnItemsRetrieved<CommentVoteModel>) {
        getCollectionRef()
            .whereEqualTo("ownerRef", ownerRef)
            .whereEqualTo("postRef", postRef)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get ${getModelClass().simpleName}", it)
                listener.onFailed(it)
            }
    }

    companion object {
        const val UP_VOTE = 1
        const val NO_VOTE = 0
        const val DOWN_VOTE = -1
    }
}