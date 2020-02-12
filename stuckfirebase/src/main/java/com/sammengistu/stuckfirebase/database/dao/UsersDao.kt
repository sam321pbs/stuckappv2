package com.sammengistu.stuckfirebase.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sammengistu.stuckfirebase.models.UserModel

@Dao
interface UsersDao {

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<UserModel>>

    @Query("SELECT * FROM users WHERE ref = :ownerRef")
    fun getUser(ownerRef: String): LiveData<List<UserModel>>

    @Query("SELECT * FROM users WHERE ref = :ownerRef")
    fun getUserSingle(ownerRef: String): LiveData<UserModel>

    @Query("SELECT * FROM users WHERE ref = :ownerRef")
    suspend fun getUserAsList(ownerRef: String): List<UserModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(user: UserModel): Long

    @Delete
    fun deleteItem(user: UserModel): Int

    @Query("DELETE FROM users WHERE ref = :ownerRef")
    suspend fun deleteByUserRef(ownerRef: String)

    @Update
    suspend fun updateItem(user: UserModel): Int
}