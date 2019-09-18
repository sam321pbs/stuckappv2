package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckfirebase.constants.STARRED_POSTS
import com.sammengistu.stuckfirebase.data.StarPostModel

class StarPostAccess(userRef: String) : FirebaseSubOwnerItemAccess<StarPostModel>(userRef) {
    override fun getModelClass() = StarPostModel::class.java
    override fun getCollectionName() = STARRED_POSTS
}