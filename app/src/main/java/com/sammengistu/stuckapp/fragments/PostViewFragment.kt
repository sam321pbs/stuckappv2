package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.utils.StringUtils
import com.sammengistu.stuckapp.views.ChoiceImageView
import com.sammengistu.stuckapp.views.ChoiceView
import com.sammengistu.stuckapp.views.VotableContainer
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.models.UserVoteModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.utils.DateUtils
import com.sammengistu.stuckfirebase.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_post_view.*
import kotlinx.android.synthetic.main.top_portion_post.*

class PostViewFragment : BaseFragment() {
    lateinit var spinner: ProgressBar

    private val args: PostViewFragmentArgs by navArgs()

    private val userViewModel: UserViewModel by viewModels {
        InjectorUtils.provideUserFactory(requireContext())
    }

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
            val avatar = AssetImageUtils.getRandomAvatar()
            avatarView.setImageBitmap(avatar)
            username.text = "Anonymous"
            avatarView.setOnClickListener(null)
            username.setOnClickListener(null)
        } else {
            userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    avatarView.loadImage(user.avatar)
                    username.text = user.username
                }
            }
            userViewModel.setUserRef(post.ownerRef)
        }

        question.text = StringUtils.capitilizeFirstLetter(post.question)
        time_since.text = DateUtils.convertDateToTimeElapsed(post.getDate())
        commentsTotal.setText(post.totalComments.toString())
        votesTotal.setText(post.getTotalVotes().toString())
        starsTotal.setText(post.totalStars.toString())
        category.setText(StringUtils.capitilizeFirstLetter(post.category))
        menu_icon.visibility = View.INVISIBLE

        val userVote = UserVotesCollection.getInstance(context!!).getVoteForPost(post.ref)
        buildChoices(choiceContainer, post, userVote)
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

    private fun buildChoices(
        choiceContainer: LinearLayout,
        post: PostModel,
        userVote: UserVoteModel?
    ) {
        choiceContainer.removeAllViews()

        for (choice in post.choicesAsList()) {
            val choiceView : VotableContainer =
                when (post.type) {
                    PostType.TEXT.toString() ->
                        ChoiceView(context!!, post.ref, post.ownerRef, choice, userVote)
                    else ->
                        ChoiceImageView(context!!, post.ref, post.ownerRef, choice, userVote)
                }

            choiceContainer.addView(choiceView)
        }
    }

    companion object {
        private const val TAG = "PostViewFragment"
    }
}