package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckapp.data.PostAccess
import com.sammengistu.stuckfirebase.constants.STARRED_POSTS
import com.sammengistu.stuckfirebase.data.PostModel

class StarPostAccess(userId: String) : FirebaseSubOwnerItemAccess<PostModel>(userId) {
    override fun getModelClass(): Class<PostModel> {
        return PostModel::class.java
    }

    override fun getCollectionName(): String {
        return STARRED_POSTS
    }

    override fun onItemCreated(item: PostModel) {

    }
}