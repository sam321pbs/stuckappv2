package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.constants.POST_VOTES
import com.sammengistu.stuckfirebase.models.PostVoteModel

class PostVoteAccess : FirebaseItemAccess<PostVoteModel>() {

    override fun getModelClass(): Class<PostVoteModel> {
        return PostVoteModel::class.java
    }

    override fun getCollectionRef(): CollectionReference {
        return getEnvironmentCollectionRef(POST_VOTES)
    }
}