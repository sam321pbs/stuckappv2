package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.MainActivity
import com.sammengistu.stuckapp.events.SaveDraftEvent
import com.sammengistu.stuckapp.views.ChoiceCardView
import com.sammengistu.stuckfirebase.constants.PostType
import kotlinx.android.synthetic.main.fragment_new_text_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.greenrobot.eventbus.Subscribe


class NewTextPostFragment : BaseNewPostFragment() {

    lateinit var mChoicesContainer: LinearLayout
    val MAX_NUMBER_OF_CHOICES = 4

    @Subscribe
    fun onSaveDraft(event: SaveDraftEvent) {
        saveAsDraft(PostType.TEXT)
    }

    override fun getFragmentTitle(): String = TITLE

    override fun getFragmentTag(): String = TAG

    override fun getLayoutId(): Int = R.layout.fragment_new_text_post

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mChoicesContainer = choices_container
        add_choice_fab.setOnClickListener {
            addChoiceView()
        }

        submit_button.setOnClickListener {
            createTextPost(mChoicesContainer)
        }
    }

    private fun addChoiceView() {
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

    companion object {
        val TAG = NewTextPostFragment::class.java.simpleName
        const val TITLE = "Text Post"
    }
}