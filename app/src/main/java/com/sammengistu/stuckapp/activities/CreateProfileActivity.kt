package com.sammengistu.stuckapp.activities

import android.os.Bundle
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.ProfileFragment
import kotlinx.android.synthetic.main.toolbar_layout.*

class CreateProfileActivity : LoggedInActivity() {

    override fun getLayoutId() = R.layout.activity_base

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        addFragment(ProfileFragment.newInstance(true))
    }
}