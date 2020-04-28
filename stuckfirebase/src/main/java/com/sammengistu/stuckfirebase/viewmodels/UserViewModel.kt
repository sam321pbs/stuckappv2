package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository

class UserViewModel internal constructor(private val repository: UserRepository) : ViewModel() {
    private val _ownerRef = MutableLiveData<String>()

    val userLiveData : LiveData<UserModel?> =
        Transformations.switchMap(_ownerRef) { ownerRef ->
            if (ownerRef == null) {
                AbsentLiveData.create()
            } else {
                repository.getUserLiveData(ownerRef)
            }
    }

    fun setUserRef(ownerRef: String) {
        _ownerRef.value = ownerRef
    }

    fun updateUser(userModel: UserModel) {
//        GlobalScope.launch { repository.updateUser(userModel) }
    }
}