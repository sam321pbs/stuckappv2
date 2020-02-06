package com.sammengistu.stuckfirebase.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sammengistu.stuckfirebase.models.UserModel

@Dao
interface UsersDao {

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<UserModel>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUser(userId: String): LiveData<List<UserModel>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserSingle(userId: String): LiveData<UserModel>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserAsList(userId: String): List<UserModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(user: UserModel): Long

    @Delete
    fun deleteItem(user: UserModel): Int

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Update
    suspend fun updateItem(user: UserModel): Int
}