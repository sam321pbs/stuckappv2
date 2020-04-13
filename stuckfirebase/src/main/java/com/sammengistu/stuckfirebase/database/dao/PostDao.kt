package com.sammengistu.stuckfirebase.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sammengistu.stuckfirebase.models.DraftPostModel

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE ownerRef = :ownerRef ORDER BY _id")
    fun getDraftPosts(ownerRef: String): LiveData<List<DraftPostModel>>

    @Query("SELECT * FROM posts WHERE _id = :id")
    fun getDraftPost(id: Int): LiveData<List<DraftPostModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: DraftPostModel)

    @Delete
    fun deletePost(post: DraftPostModel)

    @Query("DELETE FROM posts WHERE _id = :postId")
    fun deleteByPostId(postId: Int)
}