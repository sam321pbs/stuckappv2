package com.sammengistu.stuckapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.views.IconToCountView
import org.jetbrains.anko.find

class PostsAdapter(private var dataset: List<Post>) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_no_image_card, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = dataset[position]
        holder.questionView.text = post.question
        holder.username.text = post.userName
        holder.timeSince.text = post.createdAt

        holder.commentsTotalView.setText(post.totalComments.toString())
        holder.voteTotalView.setText(getVoteTotal(post).toString())
        holder.starTotalView.setText(post.totalStars.toString())
        holder.categoriesView.setText(post.category)
    }

    override fun getItemCount() = dataset.size

    fun swapData(dataset: List<Post>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }

    private fun getVoteTotal(post: Post): Int {
        return post.vote1.plus(post.vote2).plus(post.vote3).plus(post.vote4)
    }

    class PostViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        val questionView: TextView = parentView.find(R.id.question)
        val avatarView: ImageView = parentView.find(R.id.avatar)
        val username: TextView = parentView.find(R.id.username)
        val timeSince: TextView = parentView.find(R.id.time_since)
        val categoriesView: IconToCountView = parentView.find(R.id.category)
        val commentsTotalView: IconToCountView = parentView.findViewWithTag(IconToCountView.getTag("commentsTotal"))
        val voteTotalView: IconToCountView = parentView.findViewWithTag(IconToCountView.getTag("votesTotal"))
        val starTotalView: IconToCountView = parentView.findViewWithTag(IconToCountView.getTag("starsTotal"))
    }
}