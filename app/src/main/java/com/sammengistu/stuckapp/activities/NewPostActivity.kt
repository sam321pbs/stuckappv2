package com.sammengistu.stuckapp.activities

import android.os.Bundle
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.NewPostTypeFragment
import kotlinx.android.synthetic.main.activity_main.*

class NewPostActivity : LoggedInActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_base
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        addFragment(NewPostTypeFragment(), NewPostTypeFragment.TITLE)
    }
}