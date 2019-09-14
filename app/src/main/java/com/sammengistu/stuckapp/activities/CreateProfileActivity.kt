package com.sammengistu.stuckapp.activities

import android.os.Bundle
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.ProfileFragment

class CreateProfileActivity : LoggedInActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_base
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(ProfileFragment.newInstance(true), ProfileFragment.TITLE)
    }
}