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

}