package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckfirebase.constants.COMMENTS
import com.sammengistu.stuckfirebase.data.CommentModel

class CommentAccess(postId: String) : FirebaseSubPostItemAccess<CommentModel>(postId) {
    override fun getCollectionName(): String {
        return COMMENTS
    }

    override fun getModelClass(): Class<CommentModel> {
        return CommentModel::class.java
    }
}