package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.data.PostAccess
import com.sammengistu.stuckapp.views.ChoiceCardView
import kotlinx.android.synthetic.main.fragment_new_post_text.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class NewTextPostFragment : BaseFragment() {

    lateinit var mChoicesContainer: LinearLayout
    val MAX_NUMBER_OF_CHOICES = 4

    override fun getLayoutId(): Int {
        return com.sammengistu.stuckapp.R.layout.fragment_new_post_text
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
            sendPost(view)
        }
    }

    private fun sendPost(view: View) {
        if (fieldsAreValid()) {
            doAsync {
                uiThread {
                    progress_bar.visibility = View.VISIBLE
                }

                val post = Post(
                    "Sam_1",
                    username.text.toString(),
                    question.text.toString(),
                    "Tech",
                    getChoiceAt(0), getChoiceAt(1), getChoiceAt(2), getChoiceAt(3)
                )

                PostAccess.insertPost(activity!!.applicationContext, post)

                uiThread {
                    progress_bar.visibility = View.GONE
                    Snackbar.make(view, "Post has been created", Snackbar.LENGTH_SHORT).show()
                    this@NewTextPostFragment.activity!!.finish()
                }
            }
        }
    }

    private fun fieldsAreValid(): Boolean {
        if (question.text.toString().isEmpty()) {
            Snackbar.make(view!!, "Question is empty", Snackbar.LENGTH_SHORT).show()
            return false
        }

        for (choiceView in mChoicesContainer.children) {
            if (choiceView is ChoiceCardView && choiceView.getChoiceText().isEmpty()) {
                Snackbar.make(view!!, "Make sure choices are filled in", Snackbar.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun getChoiceAt(pos: Int): String {
        val child = mChoicesContainer.getChildAt(pos)
        return if (child is ChoiceCardView) child.getChoiceText() else ""
    }
}