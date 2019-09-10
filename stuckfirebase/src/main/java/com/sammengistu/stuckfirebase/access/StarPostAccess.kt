package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckfirebase.constants.STARRED_POSTS
import com.sammengistu.stuckfirebase.data.PostModel

class StarPostAccess(userRef: String) : FirebaseSubOwnerItemAccess<PostModel>(userRef) {
    override fun getModelClass(): Class<PostModel> {
        return PostModel::class.java
    }

    override fun getCollectionName(): String {
        return STARRED_POSTS
    }
}