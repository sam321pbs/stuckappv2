package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.utils.StringUtils
import com.sammengistu.stuckapp.views.VotableImageView
import com.sammengistu.stuckapp.views.VotableTextChoiceView
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserVoteModel
import com.sammengistu.stuckfirebase.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_post_view.*
import kotlinx.android.synthetic.main.top_portion_post.*

class PostViewFragment : BaseFragment() {
    lateinit var spinner: ProgressBar

    private val args: PostViewFragmentArgs by navArgs()

    override fun getLayoutId(): Int = R.layout.fragment_post_view
    override fun getFragmentTag(): String = TAG

    override fun onViewCreated(parentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(parentView, savedInstanceState)
        spinner = progress_bar
        val postRef = args.postRef

        PostAccess().getItem(postRef,
            object : FirebaseItemAccess.OnItemRetrieved<PostModel> {
                override fun onSuccess(item: PostModel) {
                    bindPostDetails(item)
                    spinner.visibility = View.GONE
                }

                override fun onFailed(e: Exception) {
                    ErrorNotifier.notifyError(activity!!, "Error loading post", TAG, e)
                    activity!!.finish()
                }
            })
    }

    private fun bindPostDetails(post: PostModel) {
        val choiceContainer = choice_container
        val avatarView = avatar_view
        val username = username

        if (PrivacyOptions.ANONYMOUS.toString() == post.privacy) {
            val avatar = AssetImageUtils.getAvatar(post.avatar)
            avatarView.setImageBitmap(avatar)
            username.text = "Anonymous"
            avatarView.setOnClickListener(null)
            username.setOnClickListener(null)
        } else {
            avatarView.loadImage(post.avatar)
            username.text = post.userName
        }

        question.text = StringUtils.capitilizeFirstLetter(post.question)
        time_since.text = DateUtils.convertDateToTimeElapsed(post.getDate())
        commentsTotal.setText(post.totalComments.toString())
        votesTotal.setText(post.getTotalVotes().toString())
        starsTotal.setText(post.totalStars.toString())
        category.setText(StringUtils.capitilizeFirstLetter(post.category))
        menu_icon.visibility = View.INVISIBLE

        val userVote = UserVotesCollection.getInstance(context!!).getVoteForPost(post.ref)
        if (post.type == PostType.TEXT.toString()) {
            buildTextChoices(choiceContainer, post, userVote)
        } else if (post.type == PostType.LANDSCAPE.toString()) {
            buildImageChoices(choiceContainer, post, userVote)
        }

        updateStarIcon(post, user_star_icon)

        show_comments.setOnClickListener {
            val action = HomeListFragmentDirections.actionNavToCommentsFragment(post.ref, 0)
            findNavController().navigate(action)
        }
    }

    private fun updateStarIcon(
        post: PostModel,
        starIcon: ImageView
    ) {
        val userStar = UserStarredCollection.getInstance(context!!).getStarPost(post)
        if (userStar == null) {
            starIcon.visibility = View.GONE
        } else {
            starIcon.visibility = View.VISIBLE
        }
    }

    private fun buildTextChoices(
        choiceContainer: LinearLayout,
        post: PostModel,
        userVote: UserVoteModel?
    ) {
        choiceContainer.removeAllViews()
        for (tripleItem in post.getChoicesToVoteList()) {
            choiceContainer.addView(
                VotableTextChoiceView(activity!!, post, tripleItem, userVote, null)
            )
        }
    }

    private fun buildImageChoices(
        choiceContainer: LinearLayout, post: PostModel, userVote: UserVoteModel?
    ) {
        choiceContainer.removeAllViews()
        for (tripleItem in post.getImagesToVoteList()) {
            choiceContainer.addView(
                VotableImageView(context!!, post, tripleItem, userVote, null)
            )
        }
    }

    companion object {
        private const val TAG = "PostViewFragment"
    }
}