package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.CommentsActivity.Companion.EXTRA_POST_CHOICE_POS
import com.sammengistu.stuckapp.activities.CommentsActivity.Companion.EXTRA_POST_ID
import com.sammengistu.stuckapp.activities.CommentsActivity.Companion.EXTRA_POST_OWNER_ID
import com.sammengistu.stuckapp.activities.CommentsActivity.Companion.EXTRA_POST_OWNER_REF
import com.sammengistu.stuckapp.adapters.CommentsAdapter
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper
import com.sammengistu.stuckapp.views.VerticalIconToTextView
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.access.CommentAccess
import com.sammengistu.stuckfirebase.access.CommentsVoteAccess
import com.sammengistu.stuckfirebase.models.CommentModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.CommentsRepository
import com.sammengistu.stuckfirebase.viewmodels.CommentsViewModel
import kotlinx.android.synthetic.main.compose_area.*
import kotlinx.android.synthetic.main.fragment_comments.*


private val TAG = CommentsFragment::class.java.simpleName
private const val TITLE = "Comments"

class CommentsFragment : BaseFragment() {

    private lateinit var commentET: EditText
    private lateinit var sendButton: ImageView
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var emptyListMessage: VerticalIconToTextView
    private lateinit var progressBar: ProgressBar

    private lateinit var postRef: String
    private lateinit var commentsViewModel: CommentsViewModel

    private var choicePos: Int = 0

    override fun getFragmentTag(): String = TAG
    override fun getLayoutId(): Int = R.layout.fragment_comments
    override fun getFragmentTitle(): String = TITLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentET = new_comment_edit_text
        sendButton = send_button
        emptyListMessage = empty_list_message
        progressBar = progress_bar

        postRef = arguments?.getString(EXTRA_POST_ID) ?: ""
        choicePos = arguments?.getInt(EXTRA_POST_CHOICE_POS) ?: 0

        if (postRef.isBlank()) {
            emptyListMessage.visibility = View.VISIBLE
            ErrorNotifier.notifyError(context, TAG, "Error loading comments.")
        } else {
            commentsAdapter = CommentsAdapter(context!!, ArrayList())
            RecyclerViewHelper.setupWithLinearManager(
                activity!!, recycler_view,
                commentsAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )
            UserHelper.getCurrentUser {
                if (it != null) {
                    commentsViewModel = CommentsViewModel(
                        CommentsRepository(CommentAccess(),
                            CommentsVoteAccess()
                        ), it.ref, postRef)
                    loadComments()
                }
            }
            setupComposeArea()
        }
    }

    private fun loadComments() {
        progressBar.visibility = View.VISIBLE
        commentsViewModel.commentsLiveData.removeObservers(viewLifecycleOwner)
        commentsViewModel.commentVotesLiveData.removeObservers(viewLifecycleOwner)
        commentsViewModel.commentVotesLiveData.observe(viewLifecycleOwner) { map ->
            if (map == null) {
                Toast.makeText(context, "Error getting votes", Toast.LENGTH_SHORT).show()
            } else {
                commentsAdapter.updateCommentVoteMap(map)
            }
        }
        commentsViewModel.commentsLiveData.observe(viewLifecycleOwner) { list ->
            progressBar.visibility = View.GONE
            when {
                list == null -> {
                    Toast.makeText(context, "Error getting comments", Toast.LENGTH_SHORT).show()
                }
                list.isEmpty() -> {
                    emptyListMessage.visibility = View.VISIBLE
                }
                else -> {
                    emptyListMessage.visibility = View.GONE
                    commentsAdapter.swapData(list)
                }
            }
        }
    }

    private fun setupComposeArea() {
        send_button.setOnClickListener {
            UserHelper.getCurrentUser { createComment(it) }
        }
    }

    private fun createComment(user: UserModel?) {
        val postOwnerId = arguments?.getString(EXTRA_POST_OWNER_ID) ?: ""
        val postOwnerRef = arguments?.getString(EXTRA_POST_OWNER_REF) ?: ""
        if (user != null) {
            val commentModel = CommentModel(
                postRef,
                postOwnerRef,
                postOwnerId,
                user.userId,
                user.ref,
                user.username,
                user.avatar,
                commentET.text.toString(),
                choicePos
            )
            commentsViewModel.createComment(commentModel)
            commentET.setText("")
            emptyListMessage.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance(
            postId: String,
            postOwnerId: String,
            postOwnerRef: String,
            choicePos: Int
        ): CommentsFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_POST_ID, postId)
            bundle.putString(EXTRA_POST_OWNER_ID, postOwnerId)
            bundle.putString(EXTRA_POST_OWNER_REF, postOwnerRef)
            bundle.putInt(EXTRA_POST_CHOICE_POS, choicePos)

            val fragment = CommentsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}