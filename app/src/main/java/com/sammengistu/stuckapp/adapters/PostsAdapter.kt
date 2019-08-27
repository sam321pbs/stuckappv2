package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckapp.constants.PostType
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.views.IconToTextView
import com.sammengistu.stuckapp.views.VotableChoiceView.Companion.createView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import java.io.File


class PostsAdapter(
    val mContext: Context,
    val mBottomSheetMenu: BottomSheetMenu
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
    private var dataset = listOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (viewType) {
            LANDSCAPE_VIEW_TYPE, PORTRAIT_VIEW_TYPE ->
                PostImageViewHolder(
                    createView(
                        parent,
                        com.sammengistu.stuckapp.R.layout.item_post_image
                    )
                )
            else ->
                PostTextViewHolder(
                    createView(
                        parent,
                        com.sammengistu.stuckapp.R.layout.item_post_text_card
                    )
                )
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
        holder.menuIcon.setOnClickListener { mBottomSheetMenu.showMenu(post) }

        if (holder is PostTextViewHolder) {
            buildVotableChoices(holder, post)
        } else if (holder is PostImageViewHolder) {
            holder.imageView1.post { loadImageIntoView(post.image1Loc, holder.imageView1) }
            holder.imageView2.post { loadImageIntoView(post.image2Loc, holder.imageView2) }
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

    private fun loadImageIntoView(imageLoc: String, imageView: ImageView) {
        Picasso.get()
            .load(File(imageLoc))
            .fit()
            .centerCrop()
            .into(imageView)
    }

    private fun createView(parent: ViewGroup, layoutId: Int) =
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

    private fun buildVotableChoices(holder: PostTextViewHolder, post: Post) {
        val container = holder.votableChoiceContainer
        container.removeAllViews()
        for (tripleItem in post.getChoicesToVoteList()) {
            container.addView(createView(mContext, tripleItem))
        }
    }

    companion object {
        const val LANDSCAPE_VIEW_TYPE = 0
        const val PORTRAIT_VIEW_TYPE = 1
        const val TEXT_VIEW_TYPE = 2
    }

    class PostTextViewHolder(parentView: View) : PostViewHolder(parentView) {
        val votableChoiceContainer: LinearLayout =
            parentView.find(com.sammengistu.stuckapp.R.id.votable_choice_container)
    }

    class PostImageViewHolder(parentView: View) : PostViewHolder(parentView) {
        val imageView1: ImageView = parentView.find(com.sammengistu.stuckapp.R.id.image_1)
        val imageView2: ImageView = parentView.find(com.sammengistu.stuckapp.R.id.image_2)
    }

    open class PostViewHolder(var parentView: View) : RecyclerView.ViewHolder(parentView) {
        val questionView: TextView = parentView.find(com.sammengistu.stuckapp.R.id.question)
        val avatarView: ImageView = parentView.find(com.sammengistu.stuckapp.R.id.avatar)
        val username: TextView = parentView.find(com.sammengistu.stuckapp.R.id.username)
        val timeSince: TextView = parentView.find(com.sammengistu.stuckapp.R.id.time_since)
        val categoriesView: IconToTextView =
            parentView.find(com.sammengistu.stuckapp.R.id.category)
        val commentsTotalView: IconToTextView =
            parentView.find(com.sammengistu.stuckapp.R.id.commentsTotal)
        val voteTotalView: IconToTextView =
            parentView.find(com.sammengistu.stuckapp.R.id.votesTotal)
        val starTotalView: IconToTextView =
            parentView.find(com.sammengistu.stuckapp.R.id.starsTotal)
        val menuIcon: ImageView = parentView.find(com.sammengistu.stuckapp.R.id.menu_icon)
    }
}