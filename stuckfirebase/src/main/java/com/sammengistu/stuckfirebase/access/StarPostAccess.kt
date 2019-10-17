package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckfirebase.constants.STARRED_POSTS
import com.sammengistu.stuckfirebase.models.StarPostModel

class StarPostAccess : FirebaseItemAccess<StarPostModel>() {
    override fun getCollectionRef() = getEnvironmentCollectionRef(STARRED_POSTS)
    override fun getModelClass() = StarPostModel::class.java

    override fun onItemCreated(item: StarPostModel) {
        super.onItemCreated(item)
        UserAccess().incrementTotalStars(item.ownerRef)
    }

    override fun onItemDeleted(item: StarPostModel?) {
        super.onItemDeleted(item)
        if (item != null) {
            UserAccess().decrementTotalStars(item.ownerRef)
        }
    }

    fun getUsersStarredPosts(userRef: String, listener: OnItemsRetrieved<StarPostModel>) {
        getItemsWhereEqual("starPostOwnerRef", userRef, listener)
    }

    fun getUsersStarredPostsBefore(userRef: String, before: Any?, listener: OnItemsRetrieved<StarPostModel>) {
        getItemsWhereEqualAndBefore("starPostOwnerRef", userRef, before, listener)
    }
}