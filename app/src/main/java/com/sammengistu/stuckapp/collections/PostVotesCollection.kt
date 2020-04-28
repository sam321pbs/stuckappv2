package com.sammengistu.stuckapp.collections

import android.content.Context
import android.util.Log
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostVoteAccess
import com.sammengistu.stuckfirebase.models.PostVoteModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import org.greenrobot.eventbus.EventBus

class PostVotesCollection {

    fun addVoteToMap(vote: PostVoteModel) {
        voteMap[vote.postRef] = vote
    }

    fun getVoteForPost(postId: String) = voteMap[postId]

    fun clearList() {
        voteMap.clear()
    }

    companion object {
        private val TAG = PostVotesCollection::class.java.simpleName
        private val voteMap = HashMap<String, PostVoteModel>()

        @Volatile private var INSTANCE: PostVotesCollection? = null

        fun getInstance(context: Context): PostVotesCollection =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PostVotesCollection().also {
                    INSTANCE = it
                    loadUserVotes(context)
                }
            }

        private fun loadUserVotes(context: Context) {
            UserRepository.getUserInstance(context) { user ->
                if (user != null) {
                    PostVoteAccess().getItemsWhereEqual("ownerRef", user.ref,
                        object : FirebaseItemAccess.OnItemsRetrieved<PostVoteModel> {
                            override fun onSuccess(list: List<PostVoteModel>) {
                                Log.d(TAG, "Finished votes load")
                                convertVotesToMap(list)
                                EventBus.getDefault().post(DataChangedEvent())
                            }

                            override fun onFailed(e: Exception) {
                                Log.e(TAG, "Error loading user votes")
                            }
                        })
                }
            }
        }

        private fun convertVotesToMap(list: List<PostVoteModel>) {
            for (postVote in list) {
                voteMap[postVote.postRef] = postVote
            }
        }
    }
}