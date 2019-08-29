package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.views.ChoiceCardView
import com.sammengistu.stuckfirebase.FirestoreHelper
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.data.Post
import kotlinx.android.synthetic.main.fragment_new_text_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class NewTextPostFragment : BaseNewPostFragment() {

    lateinit var mChoicesContainer: LinearLayout
    val MAX_NUMBER_OF_CHOICES = 4

    override fun getLayoutId(): Int {
        return R.layout.fragment_new_text_post
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mChoicesContainer = choices_container
        add_choice_fab.setOnClickListener {
            if (mChoicesContainer.childCount < 4) {
                val newChild =
                    ChoiceCardView(this@NewTextPostFragment.activity!!.applicationContext)
                newChild.setHint("Choice ${mChoicesContainer.childCount + 1}")
                mChoicesContainer.addView(newChild)

                if (mChoicesContainer.childCount >= MAX_NUMBER_OF_CHOICES) {
                    add_choice_fab.visibility = View.GONE
                }
            }
        }

        submit_button.setOnClickListener {
            createTextPost(mChoicesContainer)
        }
    }

    override fun fieldsValidated(): Boolean {
        if (question.text.toString().isEmpty()) {
            Snackbar.make(view!!, "Question is empty", Snackbar.LENGTH_SHORT).show()
            return false
        }

        for (choiceView in mChoicesContainer.children) {
            if (choiceView is ChoiceCardView && choiceView.getChoiceText().isEmpty()) {
                Snackbar.make(view!!, "Make sure choices are filled in", Snackbar.LENGTH_SHORT)
                    .show()
                return false
            }
        }
        return true
    }
}