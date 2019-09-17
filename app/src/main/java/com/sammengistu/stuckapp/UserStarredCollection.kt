package com.sammengistu.stuckapp

import com.sammengistu.stuckapp.events.UserStarsLoadedEvent
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.StarPost
import org.greenrobot.eventbus.EventBus

class UserStarredCollection {
    companion object {
        private var isInitialized = false
        private val starMap = HashMap<String, StarPost>()

        fun loadUserStars(userRef: String) {
            StarPostAccess(userRef).getItems(
                object : FirebaseItemAccess.OnItemRetrieved<StarPost> {
                    override fun onSuccess(list: List<StarPost>) {
                        convertStarredPostsToMap(list)
                        isInitialized = true
                        EventBus.getDefault().post(UserStarsLoadedEvent())
                    }

                    override fun onFailed(e: Exception) {
                        TODO("Failed to get user votes")
                    }
                })
        }

        fun addNewList(list: List<StarPost>) {
            starMap.clear()
            convertStarredPostsToMap(list)
            EventBus.getDefault().post(UserStarsLoadedEvent())
        }

        fun addStarPostToMap(starPost: StarPost) {
            starMap[starPost.postRef] = starPost
            EventBus.getDefault().post(UserStarsLoadedEvent())
        }

        fun removeStarPostFromMap(post: PostModel) {
            starMap.remove(getRef(post))
            EventBus.getDefault().post(UserStarsLoadedEvent())
        }

        fun getStarPost(post: PostModel) = starMap[getRef(post)]

        private fun getRef(post: PostModel) = if (post is StarPost) post.postRef else post.ref

        private fun convertStarredPostsToMap(list: List<StarPost>) {
            for (starPost in list) {
                // tag is the ref of the post and ref id the actual star post ref
                starMap[starPost.postRef] = starPost
            }
        }
    }
}