package com.sammengistu.stuckapp.collections

import android.util.Log
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserVoteAccess
import com.sammengistu.stuckfirebase.models.UserVoteModel
import org.greenrobot.eventbus.EventBus

class UserVotesCollection {
    companion object {
        const val UNLOADED = -1
        const val LOADING = 0
        const val LOADED = 1
        private val TAG = UserVotesCollection::class.java.simpleName
        private val voteMap = HashMap<String, UserVoteModel>()
        private var state = UNLOADED

        fun loadUserVotes(userId: String) {
            if (state == LOADING || state == LOADED) return
            state = LOADING
            UserVoteAccess().getItemsWhereEqual("ownerId", userId,
                object : FirebaseItemAccess.OnItemsRetrieved<UserVoteModel> {
                    override fun onSuccess(list: List<UserVoteModel>) {
                        Log.d(TAG, "Finished votes load")
                        convertVotesToMap(list)
                        state = LOADED
                        EventBus.getDefault().post(DataChangedEvent())
                    }

                    override fun onFailed(e: Exception) {
                        Log.e(TAG, "Error loading user votes")
                        state = UNLOADED
                    }
                })
        }

        fun addVoteToMap(vote: UserVoteModel) { voteMap[vote.postRef] = vote }

        fun getVoteForPost(postId: String): UserVoteModel? {
            reloadVotes()
            return voteMap[postId]
        }

        fun reloadVotes() {
            if (state == UNLOADED) {
                Log.d(TAG, "Reloading votes")
                UserHelper.getCurrentUser {
                    if (it != null)
                        loadUserVotes(it.userId)
                }
            }
        }

        fun clearList() {
            voteMap.clear()
        }

        private fun convertVotesToMap(list: List<UserVoteModel>) {
            for (userVote in list) {
                voteMap[userVote.postRef] = userVote
            }
        }
    }
}