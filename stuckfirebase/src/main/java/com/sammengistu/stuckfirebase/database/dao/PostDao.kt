package com.sammengistu.stuckfirebase.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sammengistu.stuckfirebase.models.DraftPostModel

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE ownerId = :ownerId ORDER BY postId")
    fun getAllPosts(ownerId: String): LiveData<List<DraftPostModel>>

    @Query("SELECT * FROM posts WHERE postId = :id")
    fun getPost(id: Long): LiveData<List<DraftPostModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: DraftPostModel)

    @Delete
    fun deletePost(post: DraftPostModel)

    @Query("DELETE FROM posts WHERE postId = :postId")
    fun deleteByPostId(postId: Long)
}