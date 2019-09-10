package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.CommentsActivity.Companion.EXTRA_POST_CHOICE_POS
import com.sammengistu.stuckapp.activities.CommentsActivity.Companion.EXTRA_POST_ID
import com.sammengistu.stuckapp.adapters.CommentsAdapter
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper
import com.sammengistu.stuckfirebase.access.CommentAccess
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.data.CommentModel
import kotlinx.android.synthetic.main.compose_area.*
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsFragment : BaseFragment() {

    lateinit var mCommentET: EditText
    lateinit var mSendButton: ImageButton
    lateinit var mCommentsAdapter: CommentsAdapter
    private var mPostId: String = ""
    private var mChoicePos: Int = 0
    private var mListComments = ArrayList<CommentModel>()

    override fun getFragmentTag(): String {
        return TAG
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_comments
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCommentET = new_comment_edit_text
        mSendButton = send_button

        mPostId = arguments?.getString(EXTRA_POST_ID) ?: ""
        mChoicePos = arguments?.getInt(EXTRA_POST_CHOICE_POS) ?: 0

        if (mPostId.isNullOrBlank()) {
            Log.d(TAG, "Empty id for comments")
        } else {

            mCommentsAdapter = CommentsAdapter(ArrayList())
            RecyclerViewHelper.setupRecyclerView(
                activity!!, recycler_view,
                mCommentsAdapter  as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )

            reloadAdapter()
            setupComposeArea()
        }
    }

    private fun reloadAdapter() {
        CommentAccess().getItemsWhereEqual(
            "postRef",
            mPostId,
            object : FirebaseItemAccess.OnItemRetrieved<CommentModel> {
                override fun onSuccess(list: List<CommentModel>) {
                    mListComments = list as ArrayList<CommentModel>
                    mCommentsAdapter.swapData(mListComments)
                }

                override fun onFailed() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
        )
    }

    private fun setupComposeArea() {
        send_button.setOnClickListener {
            val commentModel = CommentModel(
                mPostId,
                getUserId(),
                "samtheman",
                "ava_1",
                mCommentET.text.toString(),
                mChoicePos)
            mListComments.add(commentModel)
            mCommentsAdapter.swapData(mListComments)
            mCommentET.setText("")
            CommentAccess().createItemInFB(commentModel)
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