package com.sammengistu.stuckfirebase.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.database.dao.UsersDao
import com.sammengistu.stuckfirebase.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val TAG = UserRepository::class.java.simpleName

class UserRepository private constructor(
    private val userAccess: UserAccess,
    private val usersDao: UsersDao
) {

    fun getUserLiveData(
        ownerRef: String
    ): LiveData<UserModel> {
        val liveData = MutableLiveData<UserModel>()

//        if (currentUser?.ref == ownerRef) {
//            // we will retrieve current user from db
//            return usersDao.getUserSingle(ownerRef)
//        } else {
            userAccess.getItem(ownerRef,
                object : FirebaseItemAccess.OnItemRetrieved<UserModel> {
                    override fun onSuccess(item: UserModel) {
                        liveData.value = item
                        if (currentUser?.ref == ownerRef) {
                            // To set latest data
                            currentUser = item
                        }
                    }

                    override fun onFailed(e: Exception) {
                        Log.e(TAG, "Failed to get user", e)
                        liveData.value = null
                    }
                }
            )
//        }
        return liveData
    }

    suspend fun getUser(
        firebaseUserId: String, isCurrentUser: Boolean = false,
        callback: (m: UserModel?) -> Unit
    ) {
        // For now disabling getting and adding user from db
        val users : List<UserModel>? = null //usersDao.getUserAsList(firebaseUserId)
        if (users.isNullOrEmpty()) {
            Log.d(TAG, "users is empty")
            // get from webservice
            userAccess.getItemsWhereEqual("userId", firebaseUserId,
                object : FirebaseItemAccess.OnItemsRetrieved<UserModel> {
                    override fun onSuccess(list: List<UserModel>) {
                        if (list.isNullOrEmpty()) {
                            callback.invoke(null)
                        } else {
                            callback.invoke(list[0])
                            // For now disabling getting and adding user from db
//                            if (isCurrentUser) {
//                                CoroutineScope(Dispatchers.IO).launch {
//                                    usersDao.deleteByUserRef(firebaseUserId)
//                                    usersDao.insertItem(list[0])
//                                }
//                            }
                        }
                    }

                    override fun onFailed(e: Exception) {
                        callback.invoke(null)
                        Log.e(TAG, "Failed to get user", e)
                    }
                }
            )
        } else {
            Log.d(TAG, "Retrieved ${users.size} users from db")
            callback.invoke(users[0])
        }
    }

    suspend fun updateUser(user: UserModel) {
        val updates = usersDao.updateItem(user)
        Log.d(TAG, "updated $updates users in db")
    }

    suspend fun deleteUser(user: UserModel) {
        Log.d(TAG, "deleting user in db")
        usersDao.deleteByUserRef(user.ref)
    }

    companion object {

        var currentUser: UserModel? = null
        private val firebaseUser = FirebaseAuth.getInstance().currentUser
        val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        @Volatile private var instance: UserRepository? = null

        fun getInstance(userAccess: UserAccess, usersDao: UsersDao) =
            instance
                ?: synchronized(this) {
                    instance
                        ?: UserRepository(
                            userAccess,
                            usersDao
                        ).also { instance = it }
                }

        fun getUserInstance(context: Context, callback: (m: UserModel?) -> Unit) {
            when {
                firebaseUser == null -> {
                    callback.invoke(null)
                }
                currentUser == null -> {
                    val userId = firebaseUserId
                    val repo = InjectorUtils.getUsersRepository(context)
                    CoroutineScope(Dispatchers.Main).launch {
                        repo.getUser(userId, true) { user ->
                            currentUser = user
                            callback.invoke(user)
                        }
                    }
                }
                else -> {
                    callback.invoke(currentUser)
                }
            }
        }

        fun logUserOut() {
            currentUser = null
            FirebaseAuth.getInstance().signOut()
        }

        fun deleteUserAccount(context: Context) {
            getUserInstance(context) { user ->
                if (user != null) {
                    FirebaseAuth.getInstance().currentUser?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                deleteUserInDb(user, context)
                            } else {
                                if (task.exception != null) {
                                    ErrorNotifier.notifyError(
                                        context,
                                        com.sammengistu.stuckfirebase.TAG,
                                        "Error deleting account, sign in again to delete account.",
                                        task.exception!!
                                    )
                                } else {
                                    ErrorNotifier.notifyError(
                                        context,
                                        com.sammengistu.stuckfirebase.TAG,
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
                        GlobalScope.launch {
                            InjectorUtils.getUsersRepository(context).deleteUser(user)
                        }
                    }

                    override fun onFailed(e: Exception) {
                        ErrorNotifier.notifyError(
                            context, "Error deleting account", TAG, e
                        )
                    }
                })
        }
    }
}