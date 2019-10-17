package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.constants.USER_VOTES
import com.sammengistu.stuckfirebase.models.UserVoteModel

class UserVoteAccess : FirebaseItemAccess<UserVoteModel>() {

    override fun getModelClass(): Class<UserVoteModel> {
        return UserVoteModel::class.java
    }

    override fun getCollectionRef(): CollectionReference {
        return getEnvironmentCollectionRef(USER_VOTES)
    }

    override fun onItemCreated(item: UserVoteModel) {
        PostAccess().incrementVote(item.postRef, item.voteItem)
    }
}