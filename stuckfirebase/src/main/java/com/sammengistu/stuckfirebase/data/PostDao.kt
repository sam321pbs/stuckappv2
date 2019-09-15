package com.sammengistu.stuckapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY postId")
    fun getAllPosts(): LiveData<List<DraftPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: DraftPost)

    @Delete
    fun deletePost(post: DraftPost)
}