package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.BR
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.events.DeletedPostEvent
import com.sammengistu.stuckapp.fragments.DraftListFragmentDirections
import com.sammengistu.stuckapp.handler.PostAdapterEventHandler
import com.sammengistu.stuckapp.helpers.AnimationHelper
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckapp.views.*
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.exceptions.DocNotExistException
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.StarPostModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.models.UserVoteModel
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.find


class PostsAdapter(
    private val context: Context,
    private val navController: NavController,
    private val viewMode: Int
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
    private var dataSet = ArrayList<PostModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.item_post, parent, false)
        return when (viewType) {
            LANDSCAPE_VIEW_TYPE, PORTRAIT_VIEW_TYPE ->
                PostImageViewHolder(binding)
            else ->
                PostTextViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val post = dataSet[position]
        return when (post.type) {
            PostType.LANDSCAPE.toString() -> LANDSCAPE_VIEW_TYPE
            PostType.PORTRAIT.toString() -> PORTRAIT_VIEW_TYPE
            else -> TEXT_VIEW_TYPE
        }
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = dataSet[position]

        val starred = UserStarredCollection.getInstance(context).getStarPost(post) != null
        val isHidden = HiddenItemsHelper.containesRef(post.ref)
        holder.bind(post,
            PostAdapterEventHandler(context, navController, post), viewMode, starred, isHidden)

        if (PrivacyOptions.ANONYMOUS.toString() == post.privacy && viewMode != VIEW_MODE_DRAFTS) {
            holder.username.text = "Anonymous"
            val avatar = AssetImageUtils.getRandomAvatar()
            holder.avatarView.setImageBitmap(avatar)
        } else {
            // Load user
            UserAccess().getItem(post.ownerRef,
                object : FirebaseItemAccess.OnItemRetrieved<UserModel> {
                    override fun onSuccess(item: UserModel) {
                        // TODO: check that views are alive
                        holder.avatarView.loadImage(item.avatar)
                        holder.username.text = item.username
                    }

                    override fun onFailed(e: Exception) {
                        Log.e(TAG, "Failed to load user data from post", e)
                    }
                })
        }

        val userVote = UserVotesCollection.getInstance(context).getVoteForPost(post.ref)

        buildChoices(holder, post, userVote)
        handleDraftPost(holder, post)
        handleRefreshIcon(post, holder)
    }

    fun swapData(dataSet: List<PostModel>, addItems: Boolean) {
        if (!addItems) {
            this.dataSet.clear()
        }
        this.dataSet.addAll(dataSet)
        notifyDataSetChanged()
    }

    fun removePost(ref: String) {
        for (post in dataSet) {
            if (post.ref == ref) {
                dataSet.remove(post)
                notifyDataSetChanged()
                break
            }
        }
    }

    fun updateCommentCountOnPost(ref: String) {
        for (post in dataSet) {
            if (post.ref == ref) {
                post.totalComments += 1
                notifyDataSetChanged()
                break
            }
        }
    }

    fun isEmpty() = dataSet.isEmpty()

    private fun handleRefreshIcon(post: PostModel, holder: PostViewHolder) {
        if (viewMode == VIEW_MODE_FAVORITES && post is StarPostModel) {
            holder.refreshIcon.visibility = View.VISIBLE
            holder.refreshIcon.setOnClickListener {
                AnimationHelper.startRotateAnimation(it)
                PostAccess().getItem(post.postRef,
                    object : FirebaseItemAccess.OnItemRetrieved<PostModel> {
                        override fun onSuccess(item: PostModel) {
                            post.totalComments = item.totalComments
                            post.totalStars = item.totalStars
                            post.choice1 = item.choice1
                            post.choice2 = item.choice2
                            post.choice3 = item.choice3
                            post.choice4 = item.choice4
                            notifyDataSetChanged()
                            StarPostAccess().updateItemInFB(
                                post.ref, item.convertPostUpdatesToMap(), null)
                        }

                        override fun onFailed(e: Exception) {
                            if (e is DocNotExistException) {
                                StarPostAccess().deleteItemInFb(post.ref)
                                EventBus.getDefault().post(DeletedPostEvent(post.ref))
                                ErrorNotifier.notifyError(
                                    context, TAG, "Post has been deleted", e)
                            } else {
                                ErrorNotifier.notifyError(
                                    context, TAG, "Error getting latest data", e)
                            }
                        }
                    })
            }
        } else {
            holder.refreshIcon.visibility = View.GONE
        }
    }

    private fun handleDraftPost(
        holder: PostViewHolder,
        post: PostModel
    ) {
        if (viewMode == VIEW_MODE_DRAFTS) {
            val draftId = post._id
            if (draftId != null) {
                holder.itemView.setOnClickListener {
                    if (post.type == PostType.TEXT.toString()) {
                        val action = DraftListFragmentDirections
                            .actionDraftListFragmentToNewTextPostFragment2()
                            .setDraftId(draftId)
                        navController.navigate(action)
                    } else {
                        val action = DraftListFragmentDirections
                            .actionDraftListFragmentToNewImagePostFragment2()
                            .setDraftId(draftId)
                        navController.navigate(action)
                    }
                }
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    private fun buildChoices(
        holder: PostViewHolder,
        post: PostModel,
        userVote: UserVoteModel?
    ) {
        val container = holder.choiceContainer
        container.removeAllViews()
        val listener = getOnItemVotedOnListener(container) {
            holder.voteTotalView.setText((post.getTotalVotes() + 1).toString())
        }
        for (choice in post.choicesAsList()) {
            val choiceView : VotableContainer =
                when (post.type) {
                    PostType.TEXT.toString() ->
                        ChoiceView(context, post.ref, post.ownerRef, choice, userVote)
                    else ->
                        ChoiceImageView(context, post.ref, post.ownerRef, choice, userVote)
                }

            choiceView.setOnItemVotedListener(listener)
            container.addView(choiceView)
        }
    }

    private fun getOnItemVotedOnListener(
        container: LinearLayout,
        func: (choicePos: String) -> Unit
    ): VotableContainer.OnItemVotedOnListener {
        return object : VotableContainer.OnItemVotedOnListener {
            override fun onItemVotedOn(userVote: UserVoteModel) {
                for (view in container.children) {
                    if (view is VotableContainer) {
                        view.onNewVoteCreated(userVote)
                        view.userVote = userVote
                        func.invoke(userVote.choiceId)
                    }
                }
            }
        }
    }

    private class PostTextViewHolder(binding: ViewDataBinding) : PostViewHolder(binding)

    private class PostImageViewHolder(binding: ViewDataBinding) : PostViewHolder(binding)

    open class PostViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        val username: TextView = binding.root.find(R.id.username)
        val choiceContainer: LinearLayout = binding.root.find(R.id.choice_container)
        val avatarView: AvatarView = binding.root.find(R.id.avatar_view)
        val voteTotalView: HorizontalIconToTextView = binding.root.find(R.id.votesTotal)
        val refreshIcon: ImageView = binding.root.find(R.id.refresh_icon)

        fun bind(result: PostModel, handler: PostAdapterEventHandler,
                 viewMode: Int, starred: Boolean, isHidden: Boolean) {
            binding.setVariable(BR.post, result)
            binding.setVariable(BR.handler, handler)
            binding.setVariable(BR.viewMode, viewMode)
            binding.setVariable(BR.starred, starred)
            binding.setVariable(BR.isHidden, isHidden)
            binding.executePendingBindings()
        }
    }

    companion object {
        private const val TAG = "PostsAdapter"
        private const val LANDSCAPE_VIEW_TYPE = 0
        private const val PORTRAIT_VIEW_TYPE = 1
        private const val TEXT_VIEW_TYPE = 2

        const val VIEW_MODE_FAVORITES = 0
        const val VIEW_MODE_DRAFTS = 1
        const val VIEW_MODE_NORMAL = 2
    }
}