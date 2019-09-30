package com.sammengistu.stuckfirebase.database

import android.content.Context
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckfirebase.database.viewmodels.HiddenItemsViewModelFactory
import com.sammengistu.stuckfirebase.database.viewmodels.PostListViewModelFactory

object InjectorUtils {
    fun getDraftPostRepository(context: Context): PostRepository {
        return PostRepository.getInstance(AppDatabase.getInstance(context.applicationContext).postsDao())
    }

    fun provideDraftPostListFactory(context: Context): PostListViewModelFactory {
        val repository = getDraftPostRepository(context)
        return PostListViewModelFactory(repository)
    }

    fun getHiddenItemsRepository(context: Context): HiddenItemsRepository {
        return HiddenItemsRepository.getInstance(AppDatabase.getInstance(context.applicationContext).hiddenItemsDao())
    }

    fun provideHiddenItemsListFactory(context: Context, ownerId: String): HiddenItemsViewModelFactory {
        val repository = getHiddenItemsRepository(context)
        return HiddenItemsViewModelFactory(repository, ownerId)
    }
}