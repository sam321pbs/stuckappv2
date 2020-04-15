package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.CommentsFragmentDirections
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckfirebase.access.CommentsVoteAccess
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.models.CommentModel
import com.sammengistu.stuckfirebase.models.CommentVoteModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.utils.DateUtils
import org.jetbrains.anko.find

const val TAG = "CommentsAdapter"
class CommentsAdapter(private val context: Context,
                      private val navController: NavController,
                      private val commentsList: ArrayList<CommentModel>) :
    RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    private var commentVotesMap: HashMap<String, CommentVoteModel> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val comment = commentsList[position]
        UserAccess().getItem(comment.ownerRef,
            object : FirebaseItemAccess.OnItemRetrieved<UserModel> {
                override fun onSuccess(item: UserModel) {
                    // TODO: check that views are alive
                    holder.avatar.loadImage(item.avatar)
                    holder.username.text = item.username
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Failed to load user data from post", e)
                }
            })
        holder.commentText.text = comment.message
        holder.upVoteText.text = comment.upVotes.toString()
        try {
            holder.timeSince.text = DateUtils.convertDateToTimeElapsed(comment.getDate())
        } catch (e: Exception) {
            holder.timeSince.text = "now"
        }

//        holder.votedOn.text = comment.usersChoice.toString()
//        holder.votedOn.visibility = View.GONE

        addOnClickListener(holder, comment)
        updateVoteUi(holder, comment, null, false)
    }

    private fun addOnClickListener(
        holder: CommentsViewHolder,
        comment: CommentModel
    ) {
        holder.upVote.setOnClickListener {
            val vote = commentVotesMap[comment.ref]
            updateCommentVote(comment, CommentsVoteAccess.UP_VOTE)
            updateVoteUi(holder, comment, vote)
        }

        holder.downVote.setOnClickListener {
            val vote = commentVotesMap[comment.ref]
            updateCommentVote(comment, CommentsVoteAccess.DOWN_VOTE)
            updateVoteUi(holder, comment, vote)
        }

        holder.avatar.setOnClickListener { showProfile(comment.ownerRef) }
        holder.username.setOnClickListener { showProfile(comment.ownerRef) }
    }

    override fun getItemCount() = commentsList.size

    private fun showProfile(ownerRef: String) {
        val action = CommentsFragmentDirections.actionToProfileViewFragment(ownerRef)
        navController.navigate(action)
    }

    private fun updateVoteUi(
        holder: CommentsViewHolder, comment: CommentModel, prevVote: CommentVoteModel?,
        userClick: Boolean = true
    ) {
        val vote = commentVotesMap[comment.ref]
        if (vote != null) {
            if (vote.voteType == CommentsVoteAccess.UP_VOTE) {
                holder.upVote.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_upward_yellow_50_24dp))
                holder.downVote.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_blue_400_24dp))
            } else {
                holder.upVote.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_upward_blue_400_24dp))
                holder.downVote.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_yellow_50_24dp))
            }
        } else {
            holder.upVote.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_upward_blue_400_24dp))
            holder.downVote.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_blue_400_24dp))
        }
        val addAmount = if (userClick) addAmount(vote, prevVote) else 0
        comment.upVotes = comment.upVotes + addAmount
        holder.upVoteText.text = comment.upVotes.toString()
    }

    private fun addAmount(currentVote: CommentVoteModel?, prevVote: CommentVoteModel?): Int {
        val currentVoteType: Int = currentVote?.voteType ?: CommentsVoteAccess.NO_VOTE
        val prevVoteType: Int = prevVote?.voteType ?: CommentsVoteAccess.NO_VOTE

        return if (currentVoteType == CommentsVoteAccess.UP_VOTE &&
            prevVoteType == CommentsVoteAccess.DOWN_VOTE
        ) {
            return 2
        } else if (currentVoteType == CommentsVoteAccess.DOWN_VOTE &&
            prevVoteType == CommentsVoteAccess.UP_VOTE
        ) {
            return -2
        } else if (currentVoteType == CommentsVoteAccess.UP_VOTE &&
            prevVoteType == CommentsVoteAccess.NO_VOTE
        ) {
            return 1
        } else if (currentVoteType == CommentsVoteAccess.DOWN_VOTE &&
            prevVoteType == CommentsVoteAccess.NO_VOTE
        ) {
            return -1
        } else if (currentVoteType == CommentsVoteAccess.NO_VOTE  &&
            prevVoteType == CommentsVoteAccess.UP_VOTE) {
            return -1
        } else if (currentVoteType == CommentsVoteAccess.NO_VOTE  &&
            prevVoteType == CommentsVoteAccess.DOWN_VOTE) {
            return 1
        } else {
            0
        }
    }

    private fun updateCommentVote(comment: CommentModel, commentVoteType: Int) {
        UserRepository.getUserInstance(context) { user ->
            if (user != null) {
                val oldCommentVote = commentVotesMap[comment.ref]
                if (oldCommentVote == null) {
                    createCommentVote(user, comment, oldCommentVote, commentVoteType)
                } else if (oldCommentVote.voteType == commentVoteType) {
                    deleteVote(oldCommentVote)
                } else if (
                    oldCommentVote.voteType == CommentsVoteAccess.DOWN_VOTE ||
                    oldCommentVote.voteType == CommentsVoteAccess.NO_VOTE
                ) {
                    createCommentVote(user, comment, oldCommentVote, commentVoteType)
                } else if (oldCommentVote.voteType == CommentsVoteAccess.UP_VOTE) {
                    createCommentVote(user, comment, oldCommentVote, commentVoteType)
                }
            }
        }
    }

    private fun createCommentVote(
        user: UserModel,
        comment: CommentModel,
        oldCommentVote: CommentVoteModel?,
        voteType: Int
    ) {
        val newCommentVote = CommentVoteModel(
            user.ref,
            comment.ref,
            comment.postRef,
            voteType
        )
        CommentsVoteAccess().createItemInFB(newCommentVote)
        deleteVote(oldCommentVote)
        commentVotesMap[newCommentVote.commentRef] = newCommentVote
    }

    private fun deleteVote(oldCommentVote: CommentVoteModel?) {
        if (oldCommentVote != null) {
            commentVotesMap.remove(oldCommentVote.commentRef)
            CommentsVoteAccess().deleteItemInFb(oldCommentVote)
        }
    }

    fun swapData(data: List<CommentModel>) {
        commentsList.clear()
        commentsList.addAll(data)
        notifyDataSetChanged()
    }

    fun updateCommentVoteMap(map: HashMap<String, CommentVoteModel>) {
        commentVotesMap = map
        notifyDataSetChanged()
    }

    open class CommentsViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        val avatar: AvatarView = parentView.find(R.id.avatar_view)
        val username: TextView = parentView.find(R.id.username)
        val timeSince: TextView = parentView.find(R.id.time_since)
        //        val votedOn: TextView = parentView.find(R.id.voted_on)
        val commentText: TextView = parentView.find(R.id.comment_text)
        val upVote: ImageView = parentView.find(R.id.up_vote)
        val downVote: ImageView = parentView.find(R.id.down_vote)
        val upVoteText: TextView = parentView.find(R.id.up_vote_amount)
    }
}