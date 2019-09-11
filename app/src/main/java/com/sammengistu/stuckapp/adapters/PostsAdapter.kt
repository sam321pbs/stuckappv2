package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserVotesCollection
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckapp.utils.DateUtils
import com.sammengistu.stuckapp.views.HorizontalIconToTextView
import com.sammengistu.stuckapp.views.VotableChoiceView
import com.sammengistu.stuckapp.views.VotableContainer
import com.sammengistu.stuckapp.views.VotableImageView
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.UserVoteModel
import org.jetbrains.anko.find


class PostsAdapter(
    private val mContext: Context,
    private val userId: String,
    private val mBottomSheetMenu: BottomSheetMenu
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
    private var dataset = listOf<PostModel>()

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
        holder.timeSince.text = DateUtils.convertDateToTimeElapsed(post.getDate())

        holder.commentsTotalView.setText(post.totalComments.toString())
        holder.voteTotalView.setText(post.getTotalVotes().toString())
        holder.starTotalView.setText(post.totalStars.toString())
        holder.categoriesView.setText(post.category)
        holder.menuIcon.setOnClickListener { mBottomSheetMenu.showMenu(post) }

        val userVote = UserVotesCollection.getVoteForPost(post.ref)
        if (holder is PostTextViewHolder) {
            buildTextChoices(holder, post, userVote)
        } else if (holder is PostImageViewHolder) {
            buildImageChoices(holder, post, userVote)
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

    fun swapData(dataset: List<PostModel>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }

    private fun createView(parent: ViewGroup, layoutId: Int) =
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

    private fun buildTextChoices(holder: PostTextViewHolder, post: PostModel, userVote: UserVoteModel?) {
        val container = holder.votableChoiceContainer
        container.removeAllViews()
        val updateParentContainer = getUpdateParentContainer(container)
        for (tripleItem in post.getChoicesToVoteList()) {
            container.addView(
                VotableChoiceView(mContext, post, tripleItem, userVote, updateParentContainer))
        }
    }

    private fun buildImageChoices(holder: PostImageViewHolder, post: PostModel, userVote: UserVoteModel?) {
        val container = holder.imageContainer
        container.removeAllViews()
        val updateParentContainer = getUpdateParentContainer(container)
        for (tripleItem in post.getImagesToVoteList()) {
            container.addView(
                VotableImageView(mContext, post, tripleItem, userVote, updateParentContainer))
        }
    }

    private fun getUpdateParentContainer(container: LinearLayout): VotableContainer.UpdateParentContainer {
        return object : VotableContainer.UpdateParentContainer {
            override fun updateContainer(userVote: UserVoteModel?) {
                for (view in container.children) {
                    if (view is VotableContainer) {
                        view.onItemVotedOn(userVote)
                        view.userVote = userVote
                    }
                }
            }
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

    open class PostViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
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