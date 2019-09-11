package com.sammengistu.stuckapp

import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess.OnItemRetrieved
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.UserModel

class UserHelper(userId: String) {

    interface OnUserLoadedCallback {
        fun onUserLoaded(user: UserModel?)
    }

    companion object {
        var currentUser: UserModel? = null

        fun getFirebaseUser() = FirebaseAuth.getInstance().currentUser

        fun getFirebaseUserId() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        fun getCurrentUser(callback: (m: UserModel?) -> Unit) {
            if (currentUser == null) {
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

                        override fun onFailed() {
                            callback(null)
                        }

                    })
            } else {
                callback(currentUser!!)
            }
        }
    }
}