package com.sammengistu.stuckapp.collections

import com.sammengistu.stuckapp.events.UserStarsLoadedEvent
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.StarPostModel
import org.greenrobot.eventbus.EventBus

class UserStarredCollection {
    companion object {
        private var isInitialized = false
        private val starMap = HashMap<String, StarPostModel>()

        fun loadUserStars(userRef: String) {
            StarPostAccess().getUsersStarredPosts(userRef,
                object : FirebaseItemAccess.OnItemRetrieved<StarPostModel> {
                    override fun onSuccess(list: List<StarPostModel>) {
                        convertStarredPostsToMap(
                            list
                        )
                        isInitialized = true
                        EventBus.getDefault().post(UserStarsLoadedEvent())
                    }

                    override fun onFailed(e: Exception) {
                        TODO("Failed to get user votes")
                    }
                })
        }

        fun addNewList(list: List<StarPostModel>) {
            starMap.clear()
            convertStarredPostsToMap(list)
            EventBus.getDefault().post(UserStarsLoadedEvent())
        }

        fun addStarPostToMap(starPost: StarPostModel) {
            starMap[starPost.postRef] = starPost
            EventBus.getDefault().post(UserStarsLoadedEvent())
        }

        fun removeStarPostFromMap(post: PostModel) {
            starMap.remove(
                getRef(
                    post
                )
            )
            EventBus.getDefault().post(UserStarsLoadedEvent())
        }

        fun getStarPost(post: PostModel) = starMap[getRef(
            post
        )]

        private fun getRef(post: PostModel) = if (post is StarPostModel) post.postRef else post.ref

        private fun convertStarredPostsToMap(list: List<StarPostModel>) {
            for (starPost in list) {
                // tag is the ref of the post and ref id the actual star post ref
                starMap[starPost.postRef] = starPost
            }
        }
    }
}