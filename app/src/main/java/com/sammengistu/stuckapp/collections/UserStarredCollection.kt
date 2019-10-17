package com.sammengistu.stuckapp.collections

import android.util.Log
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.StarPostModel
import org.greenrobot.eventbus.EventBus

class UserStarredCollection {
    companion object {
        const val UNLOADED = -1
        const val LOADING = 0
        const val LOADED = 1
        private val TAG = UserStarredCollection::class.java.simpleName
        private val starMap = HashMap<String, StarPostModel>()
        private var state = UNLOADED

        fun loadUserStars(userRef: String) {
            if (state == LOADING || state == LOADED) return

            state = LOADING
            StarPostAccess().getUsersStarredPosts(userRef,
                object : FirebaseItemAccess.OnItemsRetrieved<StarPostModel> {
                    override fun onSuccess(list: List<StarPostModel>) {
                        Log.d(TAG, "Finished star load")
                        convertStarredPostsToMap(list)
                        state = LOADED
                        EventBus.getDefault().post(DataChangedEvent())
                    }

                    override fun onFailed(e: Exception) {
                        Log.e(TAG, "Error loading stars")
                        state = UNLOADED
                    }
                })
        }

        fun addStarPostToMap(starPost: StarPostModel) {
            starMap[starPost.postRef] = starPost
            EventBus.getDefault().post(DataChangedEvent())
        }

        fun removeStarPostFromMap(post: PostModel) {
            starMap.remove(getRef(post))
            EventBus.getDefault().post(DataChangedEvent())
        }

        fun getStarPost(post: PostModel): StarPostModel? {
            reloadStars()
            return starMap[getRef(post)]
        }

        fun clearList() { starMap.clear() }

        fun reloadStars() {
            if (state == UNLOADED) {
                Log.d(TAG, "Reloading stars")
                UserHelper.getCurrentUser {
                    if (it != null) {
                        loadUserStars(it.ref)
                    }
                }
            }
        }

        private fun getRef(post: PostModel) = if (post is StarPostModel) post.postRef else post.ref

        private fun convertStarredPostsToMap(list: List<StarPostModel>) {
            for (starPost in list) {
                // tag is the ref of the post and ref id the actual star post ref
                starMap[starPost.postRef] = starPost
            }
        }
    }
}