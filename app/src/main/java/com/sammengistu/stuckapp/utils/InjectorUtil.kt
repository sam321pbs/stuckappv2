package com.sammengistu.stuckapp.utils

import android.content.Context
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckapp.data.PostRepository
import com.sammengistu.stuckapp.viewmodels.PostListViewModelFactory

object InjectorUtils {

    private fun getPostRepository(context: Context): PostRepository {
        return PostRepository.getInstance(AppDatabase.getInstance(context.applicationContext).postsDao())
    }

    fun providePostListViewModelFactory(context: Context): PostListViewModelFactory {
        val repository = getPostRepository(context)
        return PostListViewModelFactory(repository)
    }
}