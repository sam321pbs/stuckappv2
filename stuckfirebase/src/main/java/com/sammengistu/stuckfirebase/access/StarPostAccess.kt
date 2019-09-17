package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckfirebase.constants.STARRED_POSTS
import com.sammengistu.stuckfirebase.data.StarPost

class StarPostAccess(userRef: String) : FirebaseSubOwnerItemAccess<StarPost>(userRef) {
    override fun getModelClass(): Class<StarPost> {
        return StarPost::class.java
    }

    override fun getCollectionName(): String {
        return STARRED_POSTS
    }
}