package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.CommentsActivity
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.utils.DateUtils
import com.sammengistu.stuckapp.utils.StringUtils
import com.sammengistu.stuckapp.views.VotableImageView
import com.sammengistu.stuckapp.views.VotableTextChoiceView
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserVoteModel
import kotlinx.android.synthetic.main.fragment_post_view.*
import kotlinx.android.synthetic.main.top_portion_post.*

class PostViewFragment : BaseFragment() {
    lateinit var spinner: ProgressBar

    override fun getLayoutId(): Int = R.layout.fragment_post_view
    override fun getFragmentTag(): String = TAG
    override fun getFragmentTitle(): String = TITLE

    override fun onViewCreated(parentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(parentView, savedInstanceState)
        spinner = progress_bar
        val postRef = arguments?.getString(EXTRA_POST_REF)
        if (!postRef.isNullOrEmpty()) {
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
        } else {
            activity!!.finish()
        }
    }

    private fun bindPostDetails(post: PostModel) {
        val choiceContainer = choice_container
        val questionView = question
        val avatarView = avatar_view
        val username = username
        val timeSince = time_since
        val categoriesView = category
        val commentsTotalView = commentsTotal
        val voteTotalView = votesTotal
        val starTotalView = starsTotal
        val menuIcon = menu_icon
        val starIcon = user_star_icon
        val showComments = show_comments

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

        questionView.text = StringUtils.capitilizeFirstLetter(post.question)
        timeSince.text = DateUtils.convertDateToTimeElapsed(post.getDate())
        commentsTotalView.setText(post.totalComments.toString())
        voteTotalView.setText(post.getTotalVotes().toString())
        starTotalView.setText(post.totalStars.toString())
        categoriesView.setText(StringUtils.capitilizeFirstLetter(post.category))
        menuIcon.visibility = View.INVISIBLE

        val userVote = UserVotesCollection.getVoteForPost(context, post.ref)
        if (post.type == PostType.TEXT.toString()) {
            buildTextChoices(choiceContainer, post, userVote)
        } else if (post.type == PostType.LANDSCAPE.toString()) {
            buildImageChoices(choiceContainer, post, userVote)
        }

        updateStarIcon(post, starIcon)

        showComments.setOnClickListener {
            CommentsActivity.startActivity(context!!, post.ref, post.ownerId, post.ownerRef, 0)
        }
    }

    private fun updateStarIcon(
        post: PostModel,
        starIcon: ImageView
    ) {
        val userStar = UserStarredCollection.getStarPost(context!!, post)
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
        val TAG = PostViewFragment::class.java.simpleName
        const val TITLE = "Post View"
        const val EXTRA_POST_REF = "extra_post_ref"

        fun newInstince(postRef: String): PostViewFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_POST_REF, postRef)

            val fragment = PostViewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}