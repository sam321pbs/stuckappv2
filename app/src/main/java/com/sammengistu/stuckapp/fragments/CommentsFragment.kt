package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.ErrorNotifier
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.activities.CommentsActivity.Companion.EXTRA_POST_CHOICE_POS
import com.sammengistu.stuckapp.activities.CommentsActivity.Companion.EXTRA_POST_ID
import com.sammengistu.stuckapp.adapters.CommentsAdapter
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper
import com.sammengistu.stuckfirebase.access.CommentAccess
import com.sammengistu.stuckfirebase.access.CommentsVoteAccess
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.data.CommentModel
import com.sammengistu.stuckfirebase.data.CommentVoteModel
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.compose_area.*
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsFragment : BaseFragment() {

    lateinit var commentET: EditText
    lateinit var sendButton: ImageButton
    lateinit var commentsAdapter: CommentsAdapter
    private var postRef: String = ""
    private var choicePos: Int = 0
    private var listComments = ArrayList<CommentModel>()
    private var commentVotesMap = HashMap<String, CommentVoteModel>()

    override fun getFragmentTag(): String = TAG

    override fun getLayoutId(): Int =  R.layout.fragment_comments

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentET = new_comment_edit_text
        sendButton = send_button

        postRef = arguments?.getString(EXTRA_POST_ID) ?: ""
        choicePos = arguments?.getInt(EXTRA_POST_CHOICE_POS) ?: 0

        if (postRef.isNullOrBlank()) {
            Log.d(TAG, "Empty id for comments")
        } else {

            commentsAdapter = CommentsAdapter(context!!, ArrayList())
            RecyclerViewHelper.setupRecyclerView(
                activity!!, recycler_view,
                commentsAdapter  as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )

            reloadAdapter()
            setupComposeArea()
        }
    }

    private fun reloadAdapter() {
        CommentsVoteAccess().getItemsWhereEqual("postRef", postRef, object :
            FirebaseItemAccess.OnItemRetrieved<CommentVoteModel> {
            override fun onSuccess(list: List<CommentVoteModel>) {
                val map = HashMap<String, CommentVoteModel>()
                for (commentVote in list) {
                    map[commentVote.commentRef] = commentVote
                }
                commentVotesMap = map
                getComments()
            }

            override fun onFailed() {
                ErrorNotifier.notifyError(context!!, "Error getting comments")
            }
        })
    }

    private fun getComments() {
        CommentAccess().getItemsWhereEqual(
            "postRef",
            postRef,
            object : FirebaseItemAccess.OnItemRetrieved<CommentModel> {
                override fun onSuccess(list: List<CommentModel>) {
                    listComments = list as ArrayList<CommentModel>
                    commentsAdapter.swapData(listComments)
                    commentsAdapter.updateCommentVoteMap(commentVotesMap)
                }

                override fun onFailed() {
                    ErrorNotifier.notifyError(context!!, "Error getting comments")
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
        if (user != null) {
            val commentModel = CommentModel(
                postRef,
                user.userId,
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
        }
    }

    companion object {
        val TAG = CommentsFragment::class.java.simpleName

        fun newInstance(postId: String, choicePos: Int): CommentsFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_POST_ID, postId)
            bundle.putInt(EXTRA_POST_CHOICE_POS, choicePos)

            val fragment = CommentsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}