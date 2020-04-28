package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.CommentsAdapter
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper
import com.sammengistu.stuckapp.views.VerticalIconToTextView
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.models.CommentModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.viewmodels.CommentsViewModel
import kotlinx.android.synthetic.main.compose_area.*
import kotlinx.android.synthetic.main.fragment_comments.*


class CommentsFragment : BaseFragment() {

    private lateinit var commentET: EditText
    private lateinit var sendButton: ImageView
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var emptyListMessage: VerticalIconToTextView
    private lateinit var progressBar: ProgressBar
    private val args: CommentsFragmentArgs by navArgs()

    private lateinit var postRef: String
    private lateinit var postOwnerRef: String
    private val commentsViewModel: CommentsViewModel by viewModels {
        InjectorUtils.provideCommentFactory()
    }

    private var choicePos: Int = 0

    override fun getFragmentTag(): String = TAG
    override fun getLayoutId(): Int = R.layout.fragment_comments

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentET = new_comment_edit_text
        sendButton = send_button
        emptyListMessage = empty_list_message
        progressBar = progress_bar

        postRef = args.postRef
        postOwnerRef = args.postOwnerRef
        choicePos = args.userChoice

        if (postRef.isBlank()) {
            emptyListMessage.visibility = View.VISIBLE
            ErrorNotifier.notifyError(context, TAG, "Error loading comments.")
        } else {
            commentsAdapter = CommentsAdapter(context!!, findNavController(), ArrayList())
            RecyclerViewHelper.setupWithLinearManager(
                activity!!, recycler_view,
                commentsAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )
            UserRepository.getUserInstance(context!!) {
                if (it != null) {
                    loadComments(it)
                }
            }
            setupComposeArea()
        }
    }

    private fun loadComments(user: UserModel) {
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

        commentsViewModel.setUserAndPostRef(user.ref, postRef)
    }

    private fun setupComposeArea() {
        sendButton.setOnClickListener {
            UserRepository.getUserInstance(context!!) { createComment(it) }
        }
    }

    private fun createComment(user: UserModel?) {
        if (user != null) {
            val commentModel = CommentModel(
                postRef,
                postOwnerRef,
                user.ref,
                commentET.text.toString(),
                choicePos
            )
            commentModel.owner = user
            commentsViewModel.createComment(commentModel)
            commentET.setText("")
            emptyListMessage.visibility = View.GONE
        }
    }
    companion object {
        private const val TAG = "CommentsFragment"
    }
}