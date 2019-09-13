package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.constants.COMMENT_VOTES
import com.sammengistu.stuckfirebase.data.CommentVoteModel

class CommentsVoteAccess : FirebaseItemAccess<CommentVoteModel>() {

    override fun getModelClass(): Class<CommentVoteModel> {
        return CommentVoteModel::class.java
    }

    override fun getCollectionRef(): CollectionReference {
        return getEnvironmentCollectionRef(COMMENT_VOTES)
    }

    companion object {
        const val UP_VOTE = 1
        const val NO_VOTE = 0
        const val DOWN_VOTE = -1
    }
}