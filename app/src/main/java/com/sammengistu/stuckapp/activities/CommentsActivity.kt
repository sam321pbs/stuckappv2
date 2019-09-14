package com.sammengistu.stuckapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.CommentsFragment
import kotlinx.android.synthetic.main.activity_main.*

class CommentsActivity : LoggedInActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_base
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        val postId = intent?.getStringExtra(EXTRA_POST_ID) ?: ""
        val choicePos = intent?.getIntExtra(EXTRA_POST_CHOICE_POS, 0) ?: 0
        addFragment(CommentsFragment.newInstance(postId, choicePos), CommentsFragment.TITLE)
    }

    companion object {
        const val EXTRA_POST_ID = "extra_post_id"
        const val EXTRA_POST_CHOICE_POS = "extra_post_choice_pos"

        fun startActivity(context: Context, postId: String, choicePos: Int) {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra(EXTRA_POST_ID, postId)
            intent.putExtra(EXTRA_POST_CHOICE_POS, choicePos)
            context.startActivity(intent)
        }
    }
}