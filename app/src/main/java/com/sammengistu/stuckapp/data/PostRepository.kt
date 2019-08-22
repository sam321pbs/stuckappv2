package com.sammengistu.stuckapp.data

class PostRepository(val dao: PostDao) {

    fun getAllPosts() = dao.getAllPosts()

    fun insertPost(post: Post) = dao.insertPost(post)

    companion object {
        @Volatile
        private var instance: PostRepository? = null

        fun getInstance(postDao: PostDao) =
            instance ?: synchronized(this) {
                instance ?: PostRepository(postDao).also { instance = it }
            }
    }
}