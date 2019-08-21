package com.sammengistu.stuckapp.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import kotlinx.android.synthetic.main.activity_new_post.*

class NewPostActivity : BaseActivity() {
    override fun getViewId(): Int {
        return R.layout.activity_new_post
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        image_post_choice_container.setOnClickListener { view -> Snackbar.make(new_post_container, "Selected image post", Snackbar.LENGTH_SHORT).show() }
        text_post_choice_container.setOnClickListener { view -> Snackbar.make(new_post_container, "Selected text post", Snackbar.LENGTH_SHORT).show() }
    }
}