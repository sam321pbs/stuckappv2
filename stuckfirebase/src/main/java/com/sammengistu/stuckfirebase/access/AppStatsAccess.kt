package com.sammengistu.stuckfirebase.access

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sammengistu.stuckfirebase.constants.APP_STATS
import com.sammengistu.stuckfirebase.constants.FirebaseConstants

class AppStatsAccess {

    companion object {

        val TAG = AppStatsAccess::class.java.simpleName

        const val STATS = "stats"
        const val POSTS_TOTAL = "postsTotal"
        const val VOTES_TOTAL = "votesTotal"

        fun incrementVotesTotal() {
            incrementStatsField(POSTS_TOTAL)
        }

        fun incrementPostsTotal() {
            incrementStatsField(VOTES_TOTAL)
        }

        fun decrementPostsTotal() {
            getEnvironmentCollectionRef()
                .document(STATS)
                .update(VOTES_TOTAL, FieldValue.increment(-1))
        }

        private fun incrementStatsField(field: String) {
            getEnvironmentCollectionRef()
                .document(STATS)
                .update(field, FieldValue.increment(1))
                .addOnSuccessListener {
                    Log.d(TAG, "Incremented: $field")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to increment: $field", it)
                }
        }

        private fun getEnvironmentCollectionRef(): CollectionReference {
            val db = FirebaseFirestore.getInstance()
            return db.collection(FirebaseConstants.ENVIRONMENT_COLLECTION)
                .document(FirebaseConstants.ENVIRONMENT_COLLECTION)
                .collection(APP_STATS)
        }
    }
}