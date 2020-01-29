package com.sammengistu.stuckfirebase.database

import android.content.Context
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckfirebase.repositories.HiddenItemsRepository
import com.sammengistu.stuckfirebase.repositories.PostRepository
import com.sammengistu.stuckfirebase.viewmodels.HiddenItemsViewModelFactory
import com.sammengistu.stuckfirebase.viewmodels.PostListViewModelFactory

object InjectorUtils {
    fun getDraftPostRepository(context: Context): PostRepository {
        return PostRepository.getInstance(AppDatabase.getInstance(context.applicationContext).postsDao())
    }

    fun provideDraftPostListFactory(context: Context, ownerId: String): PostListViewModelFactory {
        val repository = getDraftPostRepository(context)
        return PostListViewModelFactory(repository, ownerId)
    }

    fun getHiddenItemsRepository(context: Context): HiddenItemsRepository {
        return HiddenItemsRepository.getInstance(AppDatabase.getInstance(context.applicationContext).hiddenItemsDao())
    }

    fun provideHiddenItemsListFactory(context: Context, ownerId: String): HiddenItemsViewModelFactory {
        val repository = getHiddenItemsRepository(context)
        return HiddenItemsViewModelFactory(repository, ownerId)
    }
}