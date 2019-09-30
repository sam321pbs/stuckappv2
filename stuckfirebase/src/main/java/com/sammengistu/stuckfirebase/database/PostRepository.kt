package com.sammengistu.stuckfirebase.database

class PostRepository(val dao: PostDao) {

    fun getAllPosts() = dao.getAllPosts()

    fun getPost(id: Long) = dao.getPost(id)

    fun insertPost(post: DraftPostModel) = dao.insertPost(post)

    fun deletePost(post: DraftPostModel) = dao.deletePost(post)

    fun deletePost(postId: Long) = dao.deleteByPostId(postId)

    companion object {
        @Volatile
        private var instance: PostRepository? = null

        fun getInstance(postDao: PostDao) =
            instance ?: synchronized(this) {
                instance
                    ?: PostRepository(postDao).also { instance = it }
            }
    }
}