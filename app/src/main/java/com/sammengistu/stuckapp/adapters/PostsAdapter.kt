package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserStarredCollection
import com.sammengistu.stuckapp.UserVotesCollection
import com.sammengistu.stuckapp.activities.NewPostActivity
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.utils.DateUtils
import com.sammengistu.stuckapp.views.*
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.UserVoteModel
import org.jetbrains.anko.find


class PostsAdapter(
    private val context: Context,
    private val isDraft: Boolean,
    private val bottomSheetMenu: BottomSheetMenu
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

        if (PrivacyOptions.ANONYMOUS.toString() == post.privacy && !isDraft){
            val avatar = AssetImageUtils.getAvatar(post.avatar)
            Log.d(TAG, "Avatar is null ${avatar == null}")
            holder.avatarView.setImageBitmap(avatar)
            holder.username.text = "Anonymous"
        } else {
            holder.avatarView.loadImage(post.avatar)
            holder.username.text = post.userName
        }

        holder.questionView.text = post.question
        holder.timeSince.text = if (isDraft) "" else DateUtils.convertDateToTimeElapsed(post.getDate())
        holder.commentsTotalView.setText(post.totalComments.toString())
        holder.voteTotalView.setText(post.getTotalVotes().toString())
        holder.starTotalView.setText(post.totalStars.toString())
        holder.categoriesView.setText(post.category)
        holder.menuIcon.setOnClickListener { bottomSheetMenu.showMenu(post) }

        val userVote = UserVotesCollection.getVoteForPost(post.ref)
        if (holder is PostTextViewHolder) {
            buildTextChoices(holder, post, userVote)
        } else if (holder is PostImageViewHolder) {
            buildImageChoices(holder, post, userVote)
        }

        val userStar = UserStarredCollection.getStarPost(post)
        if (userStar == null) {
            holder.starIcon.visibility = View.GONE
        } else {
            holder.starIcon.visibility = View.VISIBLE
        }

        if (isDraft) {
            holder.itemView.setOnClickListener {
                val intent = Intent(context, NewPostActivity::class.java)
                intent.putExtra(NewPostActivity.EXTRA_POST_TYPE, post.type)
                intent.putExtra(NewPostActivity.EXTRA_POST_ID, post.draftId)
                context.startActivity(intent)
            }
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

    private fun buildTextChoices(
        holder: PostViewHolder,
        post: PostModel,
        userVote: UserVoteModel?
    ) {
        val container = holder.choiceContainer
        container.removeAllViews()
        val updateParentContainer = getUpdateParentContainer(container) {
            holder.voteTotalView.setText((post.getTotalVotes() + 1).toString())
        }
        for (tripleItem in post.getChoicesToVoteList()) {
            container.addView(
                VotableChoiceView(context, post, tripleItem, userVote, updateParentContainer)
            )
        }
    }

    private fun buildImageChoices(
        holder: PostViewHolder,
        post: PostModel,
        userVote: UserVoteModel?
    ) {
        val container = holder.choiceContainer
        container.removeAllViews()
        val updateParentContainer = getUpdateParentContainer(container) {
            holder.voteTotalView.setText((post.getTotalVotes() + 1).toString())
        }
        for (tripleItem in post.getImagesToVoteList()) {
            container.addView(
                VotableImageView(context, post, tripleItem, userVote, updateParentContainer)
            )
        }
    }

    private fun getUpdateParentContainer(
        container: LinearLayout,
        func: (choicePos: String) -> Unit
    ): VotableContainer.UpdateParentContainer {
        return object : VotableContainer.UpdateParentContainer {
            override fun updateContainer(userVote: UserVoteModel?) {
                if (userVote != null) {
                    for (view in container.children) {
                        if (view is VotableContainer) {
                            view.onItemVotedOn(userVote)
                            view.userVote = userVote
                            func.invoke(userVote.voteItem)
                        }
                    }
                }
            }
        }
    }

    class PostTextViewHolder(parentView: View) : PostViewHolder(parentView)

    class PostImageViewHolder(parentView: View) : PostViewHolder(parentView)

    open class PostViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        val choiceContainer: LinearLayout =
            parentView.find(R.id.choice_container)
        val questionView: TextView = parentView.find(R.id.question)
        val avatarView: AvatarView = parentView.find(R.id.avatar_view)
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
        val starIcon: ImageView = parentView.find(R.id.user_star_icon)
    }

    companion object {
        val TAG = PostsAdapter::class.java.simpleName
        const val LANDSCAPE_VIEW_TYPE = 0
        const val PORTRAIT_VIEW_TYPE = 1
        const val TEXT_VIEW_TYPE = 2
    }
}