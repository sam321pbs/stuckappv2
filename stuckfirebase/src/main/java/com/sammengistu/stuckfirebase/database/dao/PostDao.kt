package com.sammengistu.stuckfirebase.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sammengistu.stuckfirebase.models.PostModel

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE ownerRef = :ownerRef ORDER BY _id")
    fun getDraftPosts(ownerRef: String): LiveData<List<PostModel>>

    @Query("SELECT * FROM posts WHERE _id = :id")
    fun getDraftPost(id: Int): LiveData<List<PostModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: PostModel)

    @Delete
    fun deletePost(post: PostModel)

    @Query("DELETE FROM posts WHERE _id = :postId")
    fun deleteByPostId(postId: Int)
}