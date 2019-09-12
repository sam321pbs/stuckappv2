package com.sammengistu.stuckapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.utils.DateUtils
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckfirebase.data.CommentModel
import org.jetbrains.anko.find


class CommentsAdapter(private val dataset: ArrayList<CommentModel>) :
    RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

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
        val comment = dataset[position]
        holder.avatar.loadImage(comment.avatar)
        holder.username.text = comment.username
        holder.commentText.text = comment.message
        holder.upVoteText.text = comment.upVotes.toString()
        holder.timeSince.text = DateUtils.convertDateToTimeElapsed(comment.getDate())
        holder.votedOn.text = comment.usersChoice.toString()

        holder.upVote.setOnClickListener {
            TODO("Do something")
        }

        holder.downVote.setOnClickListener {
            TODO("Do something")
        }
    }

    override fun getItemCount() = dataset.size

    fun swapData(data: List<CommentModel>) {
        dataset.clear()
        dataset.addAll(data)
        notifyDataSetChanged()
    }

    open class CommentsViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        val avatar: AvatarView = parentView.find(R.id.avatar_view)
        val username: TextView = parentView.find(R.id.username)
        val timeSince: TextView = parentView.find(R.id.time_since)
        val votedOn: TextView = parentView.find(R.id.voted_on)
        val commentText: TextView = parentView.find(R.id.comment_text)
        val upVote: ImageView = parentView.find(R.id.up_vote)
        val downVote: ImageView = parentView.find(R.id.down_vote)
        val upVoteText: TextView = parentView.find(R.id.up_vote_amount)
    }
}