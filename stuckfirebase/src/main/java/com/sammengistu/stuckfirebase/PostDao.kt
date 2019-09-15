package com.sammengistu.stuckapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY postId")
    fun getAllPosts(): LiveData<List<DraftPost>>

    @Query("SELECT * FROM posts WHERE postId = :id")
    fun getPost(id: Long): LiveData<List<DraftPost>>

    // todo: some reason it does not work
//    @Query("SELECT * FROM posts WHERE postId = :id")
//    fun getPost(id: Long): LiveData<DraftPost>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: DraftPost)

    @Delete
    fun deletePost(post: DraftPost)

    @Query("DELETE FROM posts WHERE postId = :postId")
    fun deleteByPostId(postId: Long)
}