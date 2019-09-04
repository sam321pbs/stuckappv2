package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckfirebase.constants.VOTES
import com.sammengistu.stuckfirebase.data.UserVoteModel

class UserVoteAccess(userId: String) : FirebaseSubOwnerItemAccess<UserVoteModel>(userId) {
    override fun getModelClass(): Class<UserVoteModel> {
        return UserVoteModel::class.java
    }

    override fun getCollectionName(): String {
        return VOTES
    }
}