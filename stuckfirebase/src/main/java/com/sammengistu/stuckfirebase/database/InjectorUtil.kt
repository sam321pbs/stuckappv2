package com.sammengistu.stuckfirebase.database

import android.content.Context
import com.sammengistu.stuckfirebase.access.CommentAccess
import com.sammengistu.stuckfirebase.access.CommentsVoteAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.repositories.CommentsRepository
import com.sammengistu.stuckfirebase.repositories.HiddenItemsRepository
import com.sammengistu.stuckfirebase.repositories.PostRepository
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.viewmodels.CommentsViewModelFactory
import com.sammengistu.stuckfirebase.viewmodels.HiddenItemsViewModelFactory
import com.sammengistu.stuckfirebase.viewmodels.PostListViewModelFactory
import com.sammengistu.stuckfirebase.viewmodels.UsersViewModelFactory

object InjectorUtils {

    fun getDraftPostRepository(context: Context): PostRepository {
        return PostRepository.getInstance(AppDatabase.getInstance(context.applicationContext).postsDao())
    }

    fun provideDraftPostListFactory(context: Context, ownerId: String): PostListViewModelFactory {
        val repository = getDraftPostRepository(context)
        return PostListViewModelFactory(repository, ownerId)
    }

    private fun getHiddenItemsRepository(context: Context): HiddenItemsRepository {
        return HiddenItemsRepository.getInstance(AppDatabase.getInstance(context.applicationContext).hiddenItemsDao())
    }

    fun provideHiddenItemsListFactory(context: Context, ownerId: String): HiddenItemsViewModelFactory {
        val repository = getHiddenItemsRepository(context)
        return HiddenItemsViewModelFactory(repository, ownerId)
    }

    fun getUsersRepository(context: Context): UserRepository {
        return UserRepository.getInstance(UserAccess(),
            AppDatabase.getInstance(context.applicationContext).usersDao())
    }

    fun provideUserFactory(context: Context): UsersViewModelFactory {
        val repository = getUsersRepository(context)
        return UsersViewModelFactory(repository)
    }

    fun getCommentsRepository(): CommentsRepository {
        return CommentsRepository.getInstance(
            CommentAccess(),
            CommentsVoteAccess()
        )
    }

    fun provideCommentFactory(): CommentsViewModelFactory {
        val repository = getCommentsRepository()
        return CommentsViewModelFactory(repository)
    }
}