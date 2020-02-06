package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserViewModel internal constructor(private val repository: UserRepository) : ViewModel() {
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String>
        get() = _userId
    val userLiveData : LiveData<List<UserModel>?> =
        Transformations.switchMap(_userId) { userID ->
            if (userID == null) {
                AbsentLiveData.create()
            } else {
                repository.getUserLiveData(userID)
            }
    }

    fun setUserId(userId: String) {
        _userId.value = userId
    }

    fun updateUser(userModel: UserModel) {
        GlobalScope.launch { repository.updateUser(userModel) }
    }
}