package com.sammengistu.stuckfirebase.access

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sammengistu.stuckfirebase.access.AppStatsAccess.Companion.STATS
import com.sammengistu.stuckfirebase.constants.FirebaseConstants
import com.sammengistu.stuckfirebase.constants.USERS
import com.sammengistu.stuckfirebase.constants.USER_STATS

class UserStatsAccess {

    companion object {

        private val TAG = UserStatsAccess::class.java.simpleName

        const val MADE_VOTES = "madeVotes"
        const val RECEIVED_VOTES = "receivedVotes"
        const val STARS_TOTAL = "starsTotal"

        fun incrementMadeVotes(ownerId: String) {
            incrementStatsField(ownerId, MADE_VOTES)
        }

        fun incrementCollectedVote(ownerId: String) {
            incrementStatsField(ownerId, RECEIVED_VOTES)
        }

        fun incrementTotalStars(ownerId: String) {
            incrementStatsField(ownerId, STARS_TOTAL)
        }

        fun decrementTotalStars(ownerId: String) {
            getUserCollection(ownerId)
                .document(STATS)
                .update(STARS_TOTAL, FieldValue.increment(-1))
        }

        private fun incrementStatsField(ownerId: String, field: String) {
            getUserCollection(ownerId)
                .document(STATS)
                .update(field, FieldValue.increment(1))
                .addOnSuccessListener {
                    Log.d(TAG, "Incremented: $field")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to increment: $field", it)
                }
        }

        private fun getEnvironmentCollectionRef(collection: String): CollectionReference {
            val db = FirebaseFirestore.getInstance()
            return db.collection(FirebaseConstants.ENVIRONMENT_COLLECTION)
                .document(FirebaseConstants.ENVIRONMENT_COLLECTION)
                .collection(collection)
        }

        private fun getUserCollection(ownerId: String): CollectionReference {
            return getEnvironmentCollectionRef(USERS)
                .document(ownerId)
                .collection(USER_STATS)
        }
    }
}