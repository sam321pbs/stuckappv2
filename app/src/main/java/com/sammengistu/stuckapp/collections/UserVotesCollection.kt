package com.sammengistu.stuckapp.collections

import android.content.Context
import android.util.Log
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserVoteAccess
import com.sammengistu.stuckfirebase.models.UserVoteModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import org.greenrobot.eventbus.EventBus

class UserVotesCollection {

    fun addVoteToMap(vote: UserVoteModel) {
        voteMap[vote.postRef] = vote
    }

    fun getVoteForPost(postId: String) = voteMap[postId]

    fun clearList() {
        voteMap.clear()
    }

    companion object {
        private val TAG = UserVotesCollection::class.java.simpleName
        private val voteMap = HashMap<String, UserVoteModel>()

        @Volatile private var INSTANCE: UserVotesCollection? = null

        fun getInstance(context: Context): UserVotesCollection =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserVotesCollection().also {
                    INSTANCE = it
                    loadUserVotes(context)
                }
            }

        private fun loadUserVotes(context: Context) {
            UserRepository.getUserInstance(context) { user ->
                if (user != null) {
                    UserVoteAccess().getItemsWhereEqual("ownerRef", user.ref,
                        object : FirebaseItemAccess.OnItemsRetrieved<UserVoteModel> {
                            override fun onSuccess(list: List<UserVoteModel>) {
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

        private fun convertVotesToMap(list: List<UserVoteModel>) {
            for (userVote in list) {
                voteMap[userVote.postRef] = userVote
            }
        }
    }
}