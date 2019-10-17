package com.sammengistu.stuckapp.activities

import android.os.Bundle
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.PostViewFragment
import com.sammengistu.stuckapp.notification.StuckNotificationFactory
import kotlinx.android.synthetic.main.toolbar_layout.*

class PostViewActivity: BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_base

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        if (intent != null &&
            intent.extras != null &&
            !intent.getStringExtra(StuckNotificationFactory.EXTRA_POST_REF).isNullOrBlank()) {
            val postRef = intent.getStringExtra(StuckNotificationFactory.EXTRA_POST_REF)
            addFragment(PostViewFragment.newInstince(postRef))
            intent = null
        } else {
            finish()
        }
    }
}