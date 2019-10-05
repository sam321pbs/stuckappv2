package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.models.CommentModel
import com.sammengistu.stuckfirebase.models.CommentVoteModel
import com.sammengistu.stuckfirebase.models.UserModel
import kotlinx.android.synthetic.main.compose_area.*
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsFragment : BaseFragment() {

    lateinit var commentET: EditText
    lateinit var sendButton: ImageView
    lateinit var commentsAdapter: CommentsAdapter
    lateinit var emptyListMessage: VerticalIconToTextView
    lateinit var progressBar: ProgressBar

    private var postRef: String = ""
    private var choicePos: Int = 0
    private var listComments = ArrayList<CommentModel>()
    private var commentVotesMap = HashMap<String, CommentVoteModel>()

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
            reloadAdapter()
            setupComposeArea()
        }
    }

    private fun reloadAdapter() {
        progressBar.visibility = View.VISIBLE
        CommentsVoteAccess().getItemsWhereEqual("postRef", postRef, object :
            FirebaseItemAccess.OnItemsRetrieved<CommentVoteModel> {
            override fun onSuccess(list: List<CommentVoteModel>) {
                val map = HashMap<String, CommentVoteModel>()
                for (commentVote in list) {
                    map[commentVote.commentRef] = commentVote
                }
                commentVotesMap = map
                getComments()
            }

            override fun onFailed(e: Exception) {
                progressBar.visibility = View.GONE
                ErrorNotifier.notifyError(context!!, "Error getting comments", TAG, e)
            }
        })
    }

    private fun getComments() {
        CommentAccess().getItemsWhereEqual(
            "postRef",
            postRef,
            object : FirebaseItemAccess.OnItemsRetrieved<CommentModel> {
                override fun onSuccess(list: List<CommentModel>) {
                    progressBar.visibility = View.GONE
                    if (list.isEmpty()) {
                        emptyListMessage.visibility = View.VISIBLE
                    } else {
                        emptyListMessage.visibility = View.GONE
                    }
                    listComments = list as ArrayList<CommentModel>
                    commentsAdapter.swapData(listComments)
                    commentsAdapter.updateCommentVoteMap(commentVotesMap)
                }

                override fun onFailed(e: Exception) {
                    progressBar.visibility = View.GONE
                    ErrorNotifier.notifyError(context!!, "Error getting comments", TAG, e)
                }
            }
        )
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
            CommentAccess().createItemInFB(commentModel)
            // Todo: check that view still exists
            listComments.add(commentModel)
            commentsAdapter.swapData(listComments)
            commentET.setText("")
            emptyListMessage.visibility = View.GONE
        }
    }

    companion object {
        val TAG = CommentsFragment::class.java.simpleName
        const val TITLE = "Comments"

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