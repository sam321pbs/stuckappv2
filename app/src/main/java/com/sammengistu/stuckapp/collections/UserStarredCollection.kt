package com.sammengistu.stuckapp.collections

import android.content.Context
import android.util.Log
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.StarPostModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.utils.DateUtils
import org.greenrobot.eventbus.EventBus

class UserStarredCollection {

    fun addStarPostToMap(starPost: StarPostModel) {
        starMap[starPost.postRef] = starPost
        EventBus.getDefault().post(DataChangedEvent())
    }

    fun removeStarPostFromMap(post: PostModel) {
        starMap.remove(getRef(post))
        EventBus.getDefault().post(DataChangedEvent())
    }

    fun getStarPost(post: PostModel) = starMap[getRef(post)]

    fun clearList() {
        starMap.clear()
    }

    private fun getRef(post: PostModel) = if (post is StarPostModel) post.postRef else post.ref

    companion object {

        private val TAG = UserStarredCollection::class.java.simpleName
        private val starMap = HashMap<String, StarPostModel>()

        @Volatile private var INSTANCE: UserStarredCollection? = null

        fun getInstance(context: Context): UserStarredCollection =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserStarredCollection().also {
                    INSTANCE = it
                    loadUserStars(context)
                }
            }

        private fun loadUserStars(context: Context) {
            UserRepository.getUserInstance(context) { user ->
                if (user != null) {
                    StarPostAccess().getUsersStarredPostsBefore(
                        user.ref,
                        DateUtils.getMaxDate(),
                        object : FirebaseItemAccess.OnItemsRetrieved<StarPostModel> {
                            override fun onSuccess(list: List<StarPostModel>) {
                                Log.d(TAG, "Finished star load")
                                convertStarredPostsToMap(list)
                                EventBus.getDefault().post(DataChangedEvent())
                            }

                            override fun onFailed(e: Exception) {
                                Log.e(TAG, "Error loading stars")
                            }
                        })
                }
            }
        }

        private fun convertStarredPostsToMap(list: List<StarPostModel>) {
            for (starPost in list) {
                // tag is the ref of the post and ref id the actual star post ref
                starMap[starPost.postRef] = starPost
            }
        }
    }
}