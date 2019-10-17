package com.sammengistu.stuckfirebase.functions

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions

class CloudFunctions {
    companion object {
        val TAG = CloudFunctions::class.java.simpleName

        fun incrementVote(refPost: String): Task<String> {
            val functions = FirebaseFunctions.getInstance()
            // Create the arguments to the callable function.
            val data = hashMapOf(
                "text" to refPost,
                "push" to true
            )

            return functions
                .getHttpsCallable("increase_vote_1")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then result will throw an Exception which will be
                    // propagated down.
                    val result = task.result?.data as String
                    Log.d(TAG, "Result $result")
                    result
                }
                .addOnSuccessListener {
                    Log.d(TAG, "Success")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to increase count", it)
                }
        }
    }
}