package com.sammengistu.stuckapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.NewPostActivity
import com.sammengistu.stuckfirebase.database.DraftPostModel
import com.sammengistu.stuckapp.events.SaveDraftEvent
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckapp.views.ChoiceCardView
import kotlinx.android.synthetic.main.fragment_new_text_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.greenrobot.eventbus.Subscribe


class NewTextPostFragment : BaseNewPostFragment() {

    lateinit var mChoicesContainer: LinearLayout
    val MAX_NUMBER_OF_CHOICES = 4

    @Subscribe
    fun onSaveDraft(event: SaveDraftEvent) {
        draftTextPost(mChoicesContainer)
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

        if (arguments != null) {
            val postId = arguments!!.getLong(NewPostActivity.EXTRA_POST_ID)

//            doAsync {
//                val draftPost = PostAccess.getPost(activity!!, postId)
//                uiThread {
//                    if (draftPost != null) {
//                        updateViewFromDraftItem(draftPost)
//                    }
//                }
//            }

            val liveDraftPost = InjectorUtils.getDraftPostRepository(activity as Context).getPost(postId)

            liveDraftPost.observe(viewLifecycleOwner) { draftList ->
                if (draftList.isNotEmpty()) {
                    draft = draftList[0]
                    updateViewFromDraftItem(draft!!)
                }
            }
        }
    }

    private fun updateViewFromDraftItem(draftPost: DraftPostModel) {
        updateViewFromDraft(draftPost)
        if (draftPost.choice1.isNotBlank()) {
            addChoiceView(draftPost.choice1)
        }
        if (draftPost.choice2.isNotBlank()) {
            addChoiceView(draftPost.choice2)
        }
        if (draftPost.choice3.isNotBlank()) {
            addChoiceView(draftPost.choice3)
        }
        if (draftPost.choice4.isNotBlank()) {
            addChoiceView(draftPost.choice4)
        }
    }

    private fun addChoiceView(text: String) {
        if (mChoicesContainer.childCount < 4) {
            val newChild = ChoiceCardView(this@NewTextPostFragment.activity!!.applicationContext)
            newChild.setChoiceText(text)
            mChoicesContainer.addView(newChild)
            if (mChoicesContainer.childCount >= MAX_NUMBER_OF_CHOICES) {
                add_choice_fab.visibility = View.GONE
            }
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

        fun newInstance(postId: Long): NewTextPostFragment {
            val bundle = Bundle()
            bundle.putLong(NewPostActivity.EXTRA_POST_ID, postId)

            val frag = NewTextPostFragment()
            frag.arguments = bundle
            return frag
        }
    }
}