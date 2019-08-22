package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import kotlinx.android.synthetic.main.fragment_new_post_type.*

class NewPostTypeFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_new_post_type
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_post_choice_container.setOnClickListener { view -> Snackbar.make(new_post_container, "Selected image post", Snackbar.LENGTH_SHORT).show() }
        text_post_choice_container.setOnClickListener { view ->
            addFragment(NewTextPostFragment())
            Snackbar.make(new_post_container, "Selected text post", Snackbar.LENGTH_SHORT).show()
        }
    }
}