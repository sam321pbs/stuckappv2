package com.sammengistu.stuckfirebase.access

import android.graphics.Bitmap
import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.FbStorageHelper
import com.sammengistu.stuckfirebase.constants.USERS
import com.sammengistu.stuckfirebase.models.UserModel

class UserAccess : FirebaseItemAccess<UserModel>() {
    override fun getCollectionRef(): CollectionReference {
        return getEnvironmentCollectionRef(USERS)
    }

    override fun getModelClass(): Class<UserModel> {
        return UserModel::class.java
    }

    fun incrementTotalStars(userRef: String) {
        incrementField(userRef, RECEIVED_STARS_TOTAL)
    }

    fun decrementTotalStars(userRef: String) {
        incrementField(userRef, RECEIVED_STARS_TOTAL, -1)
    }

    fun createUser(image: Bitmap, userModel: UserModel, callback: OnItemCreated<UserModel>) {
        FbStorageHelper.uploadAvatar(image,
            object : FbStorageHelper.UploadCompletionCallback {
                override fun onSuccess(url: String) {
                    userModel.avatar = url
                    createItemInFB(userModel, callback)
                }

                override fun onFailed(exception: Exception) {
                    callback.onFailed(exception)
                }

            })
    }

    fun updateUserAndAvatar(image: Bitmap, updateUserModel: UserModel, callback: OnItemUpdated) {
        FbStorageHelper.uploadAvatar(image,
            object : FbStorageHelper.UploadCompletionCallback {
                override fun onSuccess(url: String) {
                    FbStorageHelper.deleteImage(updateUserModel.avatar)
                    updateUserModel.avatar = url
                    updateItemInFB(updateUserModel.ref, updateUserModel.convertUserToMap(), callback)
                }

                override fun onFailed(exception: Exception) {
                    callback.onFailed(exception)
                }
            })
    }

    companion object {
        const val MADE_VOTES = "totalMadeVotes"
        const val RECEIVED_VOTES = "totalReceivedVotes"
        const val RECEIVED_STARS_TOTAL = "totalReceivedStars"
    }
}