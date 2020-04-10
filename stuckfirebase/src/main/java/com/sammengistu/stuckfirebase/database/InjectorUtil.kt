package com.sammengistu.stuckfirebase.database

import android.content.Context
import com.sammengistu.stuckfirebase.access.CommentAccess
import com.sammengistu.stuckfirebase.access.CommentsVoteAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.repositories.CommentsRepository
import com.sammengistu.stuckfirebase.repositories.HiddenItemsRepository
import com.sammengistu.stuckfirebase.repositories.PostRepository
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.viewmodels.factories.*

object InjectorUtils {

    fun getPostRepository(context: Context): PostRepository {
        return PostRepository.getInstance(AppDatabase.getInstance(context.applicationContext).postsDao())
    }

    fun providePostListFactory(context: Context, loadType:String): PostListViewModelFactory {
        return PostListViewModelFactory(
            getPostRepository(context),
            loadType
        )
    }

    fun provideDraftPostListFactory(context: Context): DraftPostListViewModelFactory {
        return DraftPostListViewModelFactory(
            getPostRepository(context)
        )
    }

    private fun getHiddenItemsRepository(context: Context): HiddenItemsRepository {
        return HiddenItemsRepository.getInstance(AppDatabase.getInstance(context.applicationContext).hiddenItemsDao())
    }

    fun provideHiddenItemsListFactory(context: Context, ownerRef: String): HiddenItemsViewModelFactory {
        val repository = getHiddenItemsRepository(context)
        return HiddenItemsViewModelFactory(
            repository,
            ownerRef
        )
    }

    fun getUsersRepository(context: Context): UserRepository {
        return UserRepository.getInstance(UserAccess(),
            AppDatabase.getInstance(context.applicationContext).usersDao())
    }

    fun provideUserFactory(context: Context): UsersViewModelFactory {
        val repository = getUsersRepository(context)
        return UsersViewModelFactory(
            repository
        )
    }

    fun getCommentsRepository(): CommentsRepository {
        return CommentsRepository.getInstance(
            CommentAccess(),
            CommentsVoteAccess()
        )
    }

    fun provideCommentFactory(): CommentsViewModelFactory {
        val repository = getCommentsRepository()
        return CommentsViewModelFactory(
            repository
        )
    }
}