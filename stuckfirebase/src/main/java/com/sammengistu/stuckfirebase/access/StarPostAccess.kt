package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckfirebase.constants.STARRED_POSTS
import com.sammengistu.stuckfirebase.data.StarPostModel

class StarPostAccess : FirebaseItemAccess<StarPostModel>() {
    override fun getCollectionRef() = getEnvironmentCollectionRef(STARRED_POSTS)
    override fun getModelClass() = StarPostModel::class.java

    fun getUsersStarredPosts(userRef: String, listener: OnItemRetrieved<StarPostModel>) {
        getItemsWhereEqual("starPostOwnerRef", userRef, listener)
    }

    fun getUsersStarredPostsBefore(userRef: String, before: Any?, listener: OnItemRetrieved<StarPostModel>) {
        getItemsWhereEqualAndBefore("starPostOwnerRef", userRef, before, listener)
    }
}