package com.sammengistu.stuckapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.NewPostActivity
import com.sammengistu.stuckapp.events.SaveDraftEvent
import com.sammengistu.stuckapp.helpers.KeyboardStateHelper
import com.sammengistu.stuckapp.utils.KeyboardUtils
import com.sammengistu.stuckapp.views.ChoiceCardView
import com.sammengistu.stuckfirebase.database.DraftPostModel
import com.sammengistu.stuckfirebase.database.InjectorUtils
import kotlinx.android.synthetic.main.fragment_new_text_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.greenrobot.eventbus.Subscribe


class NewTextPostFragment : BaseNewPostFragment(), ChoiceCardView.OnClearClicked {

    lateinit var choicesContainer: LinearLayout
    lateinit var composeAreaContainer: LinearLayout
    lateinit var composeHeader: TextView
    lateinit var editText: EditText
    lateinit var doneButton: TextView
    lateinit var submitButton: Button
    lateinit var keyboardHelper: KeyboardStateHelper
    private var selectedChoice: Int = 0

    @Subscribe
    fun onSaveDraft(event: SaveDraftEvent) {
        draftTextPost(choicesContainer)
    }

    override fun getFragmentTitle(): String = TITLE
    override fun getFragmentTag(): String = TAG
    override fun getLayoutId(): Int = R.layout.fragment_new_text_post

    override fun onClearClicked(tag: Int) {
        for (view in choicesContainer.children) {
            if (view.tag == tag) {
                choicesContainer.removeView(view)
                updateSubmitButton()
                break
            }
        }

        var pos = 1
        for (view in choicesContainer.children) {
            view.tag = pos++
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        choicesContainer = choices_container
        composeAreaContainer = compose_area_container
        composeHeader = choice_header
        editText = new_choice_edit_text
        doneButton = send_button
        submitButton = submit_button
        updateSubmitButton()

        add_choice_button.setOnClickListener {
            addChoiceView()
        }

        submitButton.setOnClickListener {
            createTextPost(choicesContainer)
        }

        doneButton.setOnClickListener {
            setChoice()
            updateSubmitButton()
            KeyboardUtils.hideKeyboard(activity!!, editText)
        }

        if (arguments != null) {
            val postId = arguments!!.getLong(NewPostActivity.EXTRA_POST_ID)
            val liveDraftPost = InjectorUtils.getDraftPostRepository(activity as Context).getPost(postId)

            liveDraftPost.observe(viewLifecycleOwner) { draftList ->
                if (draftList.isNotEmpty()) {
                    draft = draftList[0]
                    updateViewFromDraftItem(draft!!)
                }
            }
        } else {
            addChoiceView()
            addChoiceView()
        }

        keyboardHelper = KeyboardStateHelper(view) { open ->
            if (open) {
                submitButton.visibility = View.GONE
            } else {
                updateSubmitButton()
            }
        }
    }

    private fun updateSubmitButton() {
        if (!fieldsValidated(false)){
            submitButton.visibility = View.GONE
            return
        }
        for (view in choicesContainer.children) {
            if (view is ChoiceCardView) {
                if (view.getChoiceText().isBlank()) {
                    submitButton.visibility = View.GONE
                    return
                }
            }
        }

        if (question.text.isBlank()) {
            submitButton.visibility = View.GONE
            return
        }
        submitButton.visibility = View.VISIBLE
    }

    private fun setChoice() {
        val view = choicesContainer.findViewWithTag<ChoiceCardView>(selectedChoice)
        view.setChoiceText(editText.text.toString().trim())
        editText.setText("")
        composeAreaContainer.visibility = View.GONE
    }

    private fun updateViewFromDraftItem(draftPost: DraftPostModel) {
        choicesContainer.removeAllViews()
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
        if (choicesContainer.childCount < 4) {
            val newChild = ChoiceCardView(activity!!.applicationContext, this)
            newChild.tag = choicesContainer.childCount + 1
            newChild.setHint("Create Choice")
            newChild.setChoiceText(text)
            choicesContainer.addView(newChild)
            if (choicesContainer.childCount >= MAX_NUMBER_OF_CHOICES) {
                add_choice_button.visibility = View.GONE
            }
            setOnClickListenerOnChoice(newChild)
            if (choicesContainer.childCount >= MAX_NUMBER_OF_CHOICES) {
                add_choice_button.visibility = View.GONE
            }
        }
        updateSubmitButton()
    }

    private fun addChoiceView() {
        addChoiceView("")
    }

    private fun setOnClickListenerOnChoice(newChild: ChoiceCardView) {
        newChild.setOnClickListener {
            selectedChoice = it.tag as Int
            if (it is ChoiceCardView) {
                submitButton.visibility = View.GONE
                composeAreaContainer.visibility = View.VISIBLE
                composeHeader.text = "Choice ${selectedChoice.toString()}"
                editText.setText(newChild.getChoiceText())
                editText.hint = "Type here"
                editText.requestFocus()
                KeyboardUtils.showKeyboard(activity!!, editText)
            }
        }
    }

    override fun fieldsValidated(showSnack: Boolean): Boolean {
        if (question.text.toString().isEmpty()) {
            if (showSnack)
                Snackbar.make(view!!, "Question is empty", Snackbar.LENGTH_SHORT).show()
            return false
        }

        for (choiceView in choicesContainer.children) {
            if (choiceView is ChoiceCardView && choiceView.getChoiceText().isEmpty()) {
                if (showSnack)
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
        const val MAX_NUMBER_OF_CHOICES = 4

        fun newInstance(postId: Long): NewTextPostFragment {
            val bundle = Bundle()
            bundle.putLong(NewPostActivity.EXTRA_POST_ID, postId)

            val frag = NewTextPostFragment()
            frag.arguments = bundle
            return frag
        }
    }
}