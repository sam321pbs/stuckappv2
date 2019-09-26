package com.sammengistu.stuckfirebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess.OnItemRetrieved
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.UserModel

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
                object : OnItemRetrieved<UserModel> {
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
                    UserAccess().deleteItemInFb(
                        user.ref,
                        object : FirebaseItemAccess.OnItemDeleted {
                            override fun onSuccess() {
                                // Todo: delete all posts, votes, avatar
                                val fbUser = FirebaseAuth.getInstance().currentUser
                                fbUser?.delete()
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Account deleted", Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
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
    }
}