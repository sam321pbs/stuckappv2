package com.sammengistu.stuckfirebase.access

import android.app.Activity
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.sammengistu.stuckfirebase.constants.DEVICE_TOKENS
import com.sammengistu.stuckfirebase.models.DeviceTokenModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository


class DeviceTokenAccess(private val userRef: String) :
    FirebaseSubOwnerItemAccess<DeviceTokenModel>(userRef) {
    override fun getCollectionName() = DEVICE_TOKENS
    override fun getModelClass() = DeviceTokenModel::class.java

    fun checkTokenExists(context: Activity) {
        UserRepository.getUserInstance(context) { user ->
            if (user != null) {
                getToken(context) { token ->
                    getItemsWhereEqual("ownerRef", userRef,
                        object : OnItemsRetrieved<DeviceTokenModel> {
                            override fun onSuccess(list: List<DeviceTokenModel>) {
                                if (list.isEmpty()) {
                                    addDeviceToken(user, token)
                                } else {
                                    for (deviceTokenModel in list) {
                                        if (deviceTokenModel.token == token) {
                                            // if token already exists, we are done
                                            Log.d(TAG, "Token exists")
                                            return
                                        }
                                    }
                                    addDeviceToken(user, token)
                                }
                            }

                            override fun onFailed(e: Exception) {
                                Log.d(TAG, "Error retrieving tokens")
                            }
                        })
                }
            }
        }
    }

    private fun addDeviceToken(user: UserModel, token: String) {
        val deviceTokenModel =
            DeviceTokenModel(user.userId, user.ref, token)
        createItemInFB(deviceTokenModel,
            object : OnItemCreated<DeviceTokenModel>{
                override fun onSuccess(item: DeviceTokenModel) {
                    Log.d(TAG, "Created token")
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Failed to create token")
                }

            })
    }

    private fun getToken(context: Activity, onTokenRetrieved: (token: String) -> Unit) {
        FirebaseInstanceId.getInstance()
            .instanceId.addOnSuccessListener(context) { instanceIdResult ->
            val newToken = instanceIdResult.token
            onTokenRetrieved.invoke(newToken)
        }
    }

    companion object {
        val TAG = DeviceTokenAccess::class.java.simpleName
    }
}