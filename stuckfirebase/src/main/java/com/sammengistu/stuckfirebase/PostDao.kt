package com.sammengistu.stuckapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY postId")
    fun getAllPosts(): LiveData<List<DraftPostModel>>

    @Query("SELECT * FROM posts WHERE postId = :id")
    fun getPost(id: Long): LiveData<List<DraftPostModel>>

    // todo: some reason it does not work
//    @Query("SELECT * FROM posts WHERE postId = :id")
//    fun getPost(id: Long): LiveData<DraftPostModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: DraftPostModel)

    @Delete
    fun deletePost(post: DraftPostModel)

    @Query("DELETE FROM posts WHERE postId = :postId")
    fun deleteByPostId(postId: Long)
}