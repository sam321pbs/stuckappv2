package com.sammengistu.stuckapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.CommentsFragment
import kotlinx.android.synthetic.main.toolbar_layout.*

class CommentsActivity : LoggedInActivity() {

    override fun getLayoutId() = R.layout.activity_base

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        val postId = intent?.getStringExtra(EXTRA_POST_ID) ?: ""
        val postOwnerId = intent?.getStringExtra(EXTRA_POST_OWNER_ID) ?: ""
        val postOwnerRef = intent?.getStringExtra(EXTRA_POST_OWNER_REF) ?: ""
        val choicePos = intent?.getIntExtra(EXTRA_POST_CHOICE_POS, 0) ?: 0
        addFragment(CommentsFragment.newInstance(postId, postOwnerId, postOwnerRef, choicePos))
    }

    companion object {
        const val EXTRA_POST_ID = "extra_post_id"
        const val EXTRA_POST_OWNER_ID = "extra_post_owner_id"
        const val EXTRA_POST_OWNER_REF = "extra_post_owner_ref"
        const val EXTRA_POST_CHOICE_POS = "extra_post_choice_pos"

        fun startActivity(
            context: Context,
            postId: String,
            postOwnerId: String,
            postOwnerRef: String,
            choicePos: Int
        ) {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra(EXTRA_POST_ID, postId)
            intent.putExtra(EXTRA_POST_CHOICE_POS, choicePos)
            intent.putExtra(EXTRA_POST_OWNER_ID, postOwnerId)
            intent.putExtra(EXTRA_POST_OWNER_REF, postOwnerRef)
            context.startActivity(intent)
        }
    }
}