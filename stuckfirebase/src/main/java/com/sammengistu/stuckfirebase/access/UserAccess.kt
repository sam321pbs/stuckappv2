package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.constants.USERS
import com.sammengistu.stuckfirebase.data.UserModel

class UserAccess : FirebaseItemAccess<UserModel>() {
    override fun getCollectionRef(): CollectionReference {
        return getEnvironmentCollectionRef(USERS)
    }

    override fun getModelClass(): Class<UserModel> {
        return UserModel::class.java
    }

    fun incrementMadeVotes(userRef: String) {
        incrementField(userRef, MADE_VOTES)
    }

    fun incrementCollectedVote(userRef: String) {
        incrementField(userRef, RECEIVED_VOTES)
    }

    fun incrementTotalStars(userRef: String) {
        incrementField(userRef, RECEIVED_STARS_TOTAL)
    }

    fun decrementTotalStars(userRef: String) {
        incrementField(userRef, RECEIVED_STARS_TOTAL, -1)
    }

    companion object {
        const val MADE_VOTES = "totalMadeVotes"
        const val RECEIVED_VOTES = "totalReceivedVotes"
        const val RECEIVED_STARS_TOTAL = "totalReceivedStars"
    }
}