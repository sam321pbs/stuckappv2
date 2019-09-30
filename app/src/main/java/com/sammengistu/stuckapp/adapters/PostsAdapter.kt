package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.BaseActivity
import com.sammengistu.stuckapp.activities.NewPostActivity
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.fragments.ProfileViewFragment
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckapp.utils.DateUtils
import com.sammengistu.stuckapp.utils.StringUtils
import com.sammengistu.stuckapp.views.*
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.database.access.HiddenItemsAccess
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.StarPostModel
import com.sammengistu.stuckfirebase.models.UserVoteModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find


class PostsAdapter(
    private val context: Context,
    private val viewMode: Int,
    private val bottomSheetMenu: BottomSheetMenu
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
    private var dataset = listOf<PostModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (viewType) {
            LANDSCAPE_VIEW_TYPE, PORTRAIT_VIEW_TYPE ->
                PostImageViewHolder(createView(parent, R.layout.item_post))
            else ->
                PostTextViewHolder(createView(parent, R.layout.item_post))
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = dataset[position]

        if (PrivacyOptions.ANONYMOUS.toString() == post.privacy &&
            viewMode != VIEW_MODE_DRAFTS) {
            val avatar = AssetImageUtils.getAvatar(post.avatar)
            holder.avatarView.setImageBitmap(avatar)
            holder.username.text = "Anonymous"
            holder.avatarView.setOnClickListener(null)
            holder.username.setOnClickListener(null)
        } else {
            holder.avatarView.loadImage(post.avatar)
            holder.username.text = post.userName
            holder.avatarView.setOnClickListener { showProfile(context, post) }
            holder.username.setOnClickListener { showProfile(context, post) }
        }

        holder.questionView.text = StringUtils.capitilizeFirstLetter(post.question)
        holder.timeSince.text =
            if (viewMode == VIEW_MODE_DRAFTS) "Draft" else DateUtils.convertDateToTimeElapsed(post.getDate())
        holder.commentsTotalView.setText(post.totalComments.toString())
        holder.voteTotalView.setText(post.getTotalVotes().toString())
        holder.starTotalView.setText(post.totalStars.toString())
        holder.categoriesView.setText(StringUtils.capitilizeFirstLetter(post.category))
        holder.menuIcon.setOnClickListener { bottomSheetMenu.showMenu(post) }

        val userVote = UserVotesCollection.getVoteForPost(post.ref)
        if (holder is PostTextViewHolder) {
            buildTextChoices(holder, post, userVote)
        } else if (holder is PostImageViewHolder) {
            buildImageChoices(holder, post, userVote)
        }

        updateStarIcon(post, holder)
        handleDraftPost(holder, post)
        handleHiddenPosts(post, holder)
        handleRefreshIcon(post, holder)
    }

    private fun handleRefreshIcon(post: PostModel, holder: PostViewHolder) {
        if (viewMode == VIEW_MODE_FAVORITES && post is StarPostModel) {
            holder.refreshIcon.visibility = View.VISIBLE
            holder.refreshIcon.setOnClickListener {
                PostAccess().getItem(post.postRef,
                    object : FirebaseItemAccess.OnItemRetrieved<PostModel> {
                        override fun onSuccess(item: PostModel) {
                            post.totalComments = item.totalComments
                            post.totalStars = item.totalStars
                            post.choices = item.choices
                            notifyDataSetChanged()
                            StarPostAccess().updateItemInFB(post.ref, item.convertPostUpdatesToMap(), null)
                        }

                        override fun onFailed(e: Exception) {
                            ErrorNotifier.notifyError(context, TAG, "Error get latest data", e)
                        }

                    })
            }
        } else {
            holder.refreshIcon.visibility = View.GONE
        }
    }

    private fun updateStarIcon(
        post: PostModel,
        holder: PostViewHolder
    ) {
        val userStar = UserStarredCollection.getStarPost(post)
        if (userStar == null) {
            holder.starIcon.visibility = View.GONE
        } else {
            holder.starIcon.visibility = View.VISIBLE
        }
    }

    private fun handleDraftPost(
        holder: PostViewHolder,
        post: PostModel
    ) {
        if (viewMode == VIEW_MODE_DRAFTS) {
            holder.itemView.setOnClickListener {
                val intent = Intent(context, NewPostActivity::class.java)
                intent.putExtra(NewPostActivity.EXTRA_POST_TYPE, post.type)
                intent.putExtra(NewPostActivity.EXTRA_POST_ID, post.draftId)
                context.startActivity(intent)
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    private fun handleHiddenPosts(
        post: PostModel,
        holder: PostViewHolder
    ) {
        if (HiddenItemsHelper.containesRef(post.ref)) {
            holder.questionView.visibility = View.GONE
            holder.choiceContainer.visibility = View.GONE
            holder.postInfo.visibility = View.GONE
            holder.unhideButton.visibility = View.VISIBLE
            holder.unhideButton.setOnClickListener {
                doAsync {
                    val itemId = HiddenItemsHelper.getItem(post.ref)?._id
                    if (itemId != null)
                        HiddenItemsAccess(context).deleteItem(itemId)
                }
            }
        } else {
            holder.questionView.visibility = View.VISIBLE
            holder.choiceContainer.visibility = View.VISIBLE
            holder.postInfo.visibility = View.VISIBLE
            holder.unhideButton.visibility = View.GONE
            holder.unhideButton.setOnClickListener(null)
        }
    }

    private fun showProfile(context: Context, post: PostModel) {
        if (context is BaseActivity) {
            context.addFragment(ProfileViewFragment.newInstance(post.ownerId))
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
        val choiceContainer: LinearLayout = parentView.find(R.id.choice_container)
        val questionView: TextView = parentView.find(R.id.question)
        val avatarView: AvatarView = parentView.find(R.id.avatar_view)
        val username: TextView = parentView.find(R.id.username)
        val timeSince: TextView = parentView.find(R.id.time_since)
        val categoriesView: HorizontalIconToTextView = parentView.find(R.id.category)
        val commentsTotalView: HorizontalIconToTextView = parentView.find(R.id.commentsTotal)
        val voteTotalView: HorizontalIconToTextView = parentView.find(R.id.votesTotal)
        val starTotalView: HorizontalIconToTextView = parentView.find(R.id.starsTotal)
        val postInfo: LinearLayout = parentView.find(R.id.post_info)
        val unhideButton: Button = parentView.find(R.id.unhide_button)
        val menuIcon: ImageView = parentView.find(R.id.menu_icon)
        val starIcon: ImageView = parentView.find(R.id.user_star_icon)
        val refreshIcon: ImageView = parentView.find(R.id.refresh_icon)
    }

    companion object {
        val TAG = PostsAdapter::class.java.simpleName
        const val LANDSCAPE_VIEW_TYPE = 0
        const val PORTRAIT_VIEW_TYPE = 1
        const val TEXT_VIEW_TYPE = 2

        const val VIEW_MODE_FAVORITES = 0
        const val VIEW_MODE_DRAFTS = 1
        const val VIEW_MODE_NORMAL = 2
    }
}