package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import com.sammengistu.stuckapp.R
import kotlinx.android.synthetic.main.fragment_new_post_type.*

class NewPostTypeFragment : BaseFragment() {

    companion object {
        val TAG: String = NewPostTypeFragment::class.java.simpleName
    }

    override fun getFragmentTag(): String {
        return TAG
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_new_post_type
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_post_choice_container.setOnClickListener {
            addFragment(NewImagePostFragment())
        }

        text_post_choice_container.setOnClickListener {
            addFragment(NewTextPostFragment())
        }
    }
}