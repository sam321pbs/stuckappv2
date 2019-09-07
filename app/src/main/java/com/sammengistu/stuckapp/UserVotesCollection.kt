package com.sammengistu.stuckapp

import com.sammengistu.stuckapp.events.UserVotesLoadedEvent
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserVoteAccess
import com.sammengistu.stuckfirebase.data.UserVoteModel
import org.greenrobot.eventbus.EventBus

class UserVotesCollection {
    companion object {
        private var isInitialized = false
        private val voteMap = HashMap<String, UserVoteModel>()

        fun loadUserVotes(userId: String) {
            UserVoteAccess().getItemsWhereEqual("ownerId", userId,
                object : FirebaseItemAccess.OnItemRetrieved<UserVoteModel> {
                    override fun onSuccess(list: List<UserVoteModel>) {
                        convertVotesToMap(list)
                        isInitialized = true
                        EventBus.getDefault().post(UserVotesLoadedEvent())
                    }

                    override fun onFailed() {
                        TODO("Failed to get user votes")
                    }

                })
        }

        fun isInitialized(): Boolean = isInitialized

        fun addVoteToMap(vote: UserVoteModel) {
            voteMap[vote.postRef] = vote
//            EventBus.getDefault().post(UserVotesLoadedEvent())
        }

        fun getVoteForPost(postId: String): UserVoteModel? {
            if (isInitialized) {
                return voteMap[postId]
            }
            return null
        }

        private fun convertVotesToMap(list: List<UserVoteModel>) {
            for (userVote in list) {
                voteMap[userVote.postRef] = userVote
            }
        }
    }
}