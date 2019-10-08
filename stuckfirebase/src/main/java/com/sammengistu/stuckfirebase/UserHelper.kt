package com.sammengistu.stuckfirebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess.OnItemsRetrieved
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.models.UserModel

class UserHelper {
    companion object {
        var currentUser: UserModel? = null

        fun getFirebaseUser() = FirebaseAuth.getInstance().currentUser

        fun getFirebaseUserId() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        fun getCurrentUser(callback: (m: UserModel?) -> Unit) {
            if (getFirebaseUser() == null) {
                callback.invoke(null)
                return
            }
            if (currentUser == null) {
                reloadUser(callback)
            } else {
                callback.invoke(currentUser)
            }
        }

        fun reloadUser(callback: (m: UserModel?) -> Unit) {
            UserAccess().getItemsWhereEqual(
                "userId",
                getFirebaseUserId(),
                1L,
                object : OnItemsRetrieved<UserModel> {
                    override fun onSuccess(list: List<UserModel>) {
                        if (list.isNotEmpty()) {
                            currentUser = list[0]
                            callback(list[0])
                        } else {
                            callback(null)
                        }
                    }

                    override fun onFailed(e: Exception) {
                        callback(null)
                    }
                })
        }

        fun logUserOut() {
            currentUser = null
            FirebaseAuth.getInstance().signOut()
        }

        fun deleteUserAccount(context: Context) {
            getCurrentUser { user ->
                if (user != null) {
                    FirebaseAuth.getInstance().currentUser?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                deleteUserInDb(user, context)
                            } else {
                                if (task.exception != null) {
                                    ErrorNotifier.notifyError(
                                        context,
                                        TAG,
                                        "Error deleting account, sign in again to delete account.",
                                        task.exception!!
                                    )
                                } else {
                                    ErrorNotifier.notifyError(
                                        context,
                                        TAG,
                                        "Error deleting account, sign in again to delete account."
                                    )
                                }
                                logUserOut()
                            }
                        }
                }
            }
        }

        private fun deleteUserInDb(
            user: UserModel,
            context: Context
        ) {
            UserAccess().deleteItemInFb(
                user.ref,
                object : FirebaseItemAccess.OnItemDeleted {
                    override fun onSuccess() {
                        // Todo: delete all posts, votes, avatar
                        currentUser = null
                    }

                    override fun onFailed(e: Exception) {
                        ErrorNotifier.notifyError(
                            context, "Error deleting account",
                            TAG, e
                        )
                    }
                })
        }
    }
}