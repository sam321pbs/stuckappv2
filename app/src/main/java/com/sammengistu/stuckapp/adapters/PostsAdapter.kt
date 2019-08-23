package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.constants.PostType
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.views.IconToCountView
import com.sammengistu.stuckapp.views.VotableChoiceView.Companion.createView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import java.io.File

class PostsAdapter(val context: Context) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
    private var dataset = listOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (viewType) {
            LANDSCAPE_VIEW_TYPE, PORTRAIT_VIEW_TYPE ->
                PostImageViewHolder(createView(parent, R.layout.item_post_image))
            else ->
                PostTextViewHolder(createView(parent, R.layout.item_post_text_card))
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = dataset[position]
        holder.questionView.text = post.question
        holder.username.text = post.userName
        holder.timeSince.text = post.createdAt

        holder.commentsTotalView.setText(post.totalComments.toString())
        holder.voteTotalView.setText(post.getTotalVotes().toString())
        holder.starTotalView.setText(post.totalStars.toString())
        holder.categoriesView.setText(post.category)

        if (holder is PostTextViewHolder) {
            buildVotableChoices(holder, post)
        } else if (holder is PostImageViewHolder) {
            // Todo: possibly rework this to be async
//            holder.imageView1.setImageBitmap(StorageUtils.loadImageFromStorage(post.image1Loc))
//            holder.imageView2.setImageBitmap(StorageUtils.loadImageFromStorage(post.image2Loc))
            Picasso.get().load(File(post.image1Loc)).into(holder.imageView1)
            Picasso.get().load(File(post.image2Loc)).into(holder.imageView2)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val post = dataset[position]
        return when (post.type) {
            PostType.LANDSCAPE -> LANDSCAPE_VIEW_TYPE
            PostType.PORTRAIT -> PORTRAIT_VIEW_TYPE
            else -> TEXT_VIEW_TYPE
        }
    }

    override fun getItemCount() = dataset.size

    fun swapData(dataset: List<Post>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }

    private fun createView(parent: ViewGroup, layoutId: Int) =
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

    private fun buildVotableChoices(holder: PostTextViewHolder, post: Post) {
        val container = holder.votableChoiceContainer
        container.removeAllViews()
        for (tripleItem in post.getChoicesToVoteList()) {
            container.addView(createView(context, tripleItem))
        }
    }

    companion object {
        const val LANDSCAPE_VIEW_TYPE = 0
        const val PORTRAIT_VIEW_TYPE = 1
        const val TEXT_VIEW_TYPE = 2
    }

    class PostTextViewHolder(parentView: View) : PostViewHolder(parentView) {
        val votableChoiceContainer: LinearLayout = parentView.find(R.id.votable_choice_container)
    }

    class PostImageViewHolder(parentView: View) : PostViewHolder(parentView) {
        val imageView1: ImageView = parentView.find(R.id.image_1)
        val imageView2: ImageView = parentView.find(R.id.image_2)
    }

    open class PostViewHolder(var parentView: View) : RecyclerView.ViewHolder(parentView) {
        val questionView: TextView = parentView.find(R.id.question)
        val avatarView: ImageView = parentView.find(R.id.avatar)
        val username: TextView = parentView.find(R.id.username)
        val timeSince: TextView = parentView.find(R.id.time_since)
        val categoriesView: IconToCountView = parentView.find(R.id.category)
        val commentsTotalView: IconToCountView = parentView.find(R.id.commentsTotal)
        val voteTotalView: IconToCountView = parentView.find(R.id.votesTotal)
        val starTotalView: IconToCountView = parentView.find(R.id.starsTotal)
    }
}