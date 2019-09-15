package com.sammengistu.stuckapp.utils

import android.content.Context
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckapp.data.PostRepository
import com.sammengistu.stuckfirebase.viewmodels.PostListViewModelFactory
import com.sammengistu.stuckfirebase.viewmodels.PostViewModelFactory

object InjectorUtils {

    fun getPostRepository(context: Context): PostRepository {
        return PostRepository.getInstance(AppDatabase.getInstance(context.applicationContext).postsDao())
    }

    fun providePostListViewModelFactory(context: Context): PostListViewModelFactory {
        val repository = getPostRepository(context)
        return PostListViewModelFactory(repository)
    }

    fun providePostViewModelFactory(context: Context, postId: Long): PostViewModelFactory {
        val repository = getPostRepository(context)
        return PostViewModelFactory(postId, repository)
    }
}