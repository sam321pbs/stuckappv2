package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.DummyDataStuck
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckapp.views.HorizontalIconToTextView
import com.sammengistu.stuckapp.views.VotableChoiceView
import com.sammengistu.stuckapp.views.VotableImageView
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.data.Post
import org.jetbrains.anko.find


class PostsAdapter(
    private val mContext: Context,
    private val mBottomSheetMenu: BottomSheetMenu
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
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
//        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
//            .format(Date(post.createdAt!!.seconds * 1000))
        holder.questionView.text = post.question
        holder.username.text = post.userName
//        holder.timeSince.text = simpleDateFormat

        holder.commentsTotalView.setText(post.totalComments.toString())
        holder.voteTotalView.setText(post.getTotalVotes().toString())
        holder.starTotalView.setText(post.totalStars.toString())
        holder.categoriesView.setText(post.category)
        holder.menuIcon.setOnClickListener { mBottomSheetMenu.showMenu(post) }

        if (holder is PostTextViewHolder) {
            buildVotableTextChoices(holder, post)
        } else if (holder is PostImageViewHolder) {
            buildVotableImageChoices(holder, post)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val post = dataset[position]
        return when (post.type) {
            PostType.LANDSCAPE.toString() -> LANDSCAPE_VIEW_TYPE
            PostType.PORTRAIT.toString() -> PORTRAIT_VIEW_TYPE
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

    private fun buildVotableTextChoices(holder: PostTextViewHolder, post: Post) {
        val container = holder.votableChoiceContainer
        container.removeAllViews()
        for (tripleItem in post.getChoicesToVoteList()) {
            container.addView(
                VotableChoiceView.createView(mContext, DummyDataStuck.ownerId, post.ref, tripleItem))
        }
    }

    private fun buildVotableImageChoices(holder: PostImageViewHolder, post: Post) {
        val container = holder.imageContainer
        container.removeAllViews()
        for (tripleItem in post.getImagesToVoteList()) {
            container.addView(
                VotableImageView.createView(mContext, DummyDataStuck.ownerId, post.ref, tripleItem))
        }
    }

    companion object {
        const val LANDSCAPE_VIEW_TYPE = 0
        const val PORTRAIT_VIEW_TYPE = 1
        const val TEXT_VIEW_TYPE = 2
    }

    class PostTextViewHolder(parentView: View) : PostViewHolder(parentView) {
        val votableChoiceContainer: LinearLayout =
            parentView.find(R.id.votable_choice_container)
    }

    class PostImageViewHolder(parentView: View) : PostViewHolder(parentView) {
        val imageContainer: LinearLayout = parentView.find(R.id.image_container)
    }

    open class PostViewHolder(var parentView: View) : RecyclerView.ViewHolder(parentView) {
        val questionView: TextView = parentView.find(R.id.question)
        val avatarView: ImageView = parentView.find(R.id.avatar)
        val username: TextView = parentView.find(R.id.username)
        val timeSince: TextView = parentView.find(R.id.time_since)
        val categoriesView: HorizontalIconToTextView =
            parentView.find(R.id.category)
        val commentsTotalView: HorizontalIconToTextView =
            parentView.find(R.id.commentsTotal)
        val voteTotalView: HorizontalIconToTextView =
            parentView.find(R.id.votesTotal)
        val starTotalView: HorizontalIconToTextView =
            parentView.find(R.id.starsTotal)
        val menuIcon: ImageView = parentView.find(R.id.menu_icon)
    }
}