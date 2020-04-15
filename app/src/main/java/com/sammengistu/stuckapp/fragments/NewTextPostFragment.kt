package com.sammengistu.stuckapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.utils.KeyboardUtils
import com.sammengistu.stuckapp.views.ChoiceCardView
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.models.PostModel
import kotlinx.android.synthetic.main.fragment_new_text_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*


class NewTextPostFragment : BaseNewPostFragment(), ChoiceCardView.OnClearClicked {

    private lateinit var choicesContainer: LinearLayout
    private lateinit var composeAreaContainer: LinearLayout
    private lateinit var composeHeader: TextView
    private lateinit var editText: EditText
    private lateinit var doneButton: TextView
    private var selectedChoice: Int = 0

    private val args: NewTextPostFragmentArgs by navArgs()

    override fun getFragmentTag(): String = TAG

    override fun getLayoutId(): Int = R.layout.fragment_new_text_post

    override fun onClearClicked(tag: Int) {
        for (view in choicesContainer.children) {
            if (view.tag == tag) {
                choicesContainer.removeView(view)
                add_choice_button.visibility = View.VISIBLE
                break
            }
        }

        var pos = 1
        for (view in choicesContainer.children) {
            view.tag = pos++
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

    override fun saveAsDraft() {
        saveDraft(PostType.TEXT, emptyMap())
    }

    override fun createPost() {
        createTextPost()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        choicesContainer = choices_container
        composeAreaContainer = compose_area_container
        composeHeader = choice_header
        editText = new_choice_edit_text
        doneButton = send_button

        add_choice_button.setOnClickListener {
            addChoiceView()
        }

        doneButton.setOnClickListener {
            setChoice()
            KeyboardUtils.hideKeyboard(activity!!, editText)
        }

        val draftId = args.draftId
        if (draftId != -1) {
            val liveDraftPost = InjectorUtils
                .getPostRepository(activity as Context)
                .getDraftPost(draftId)

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
    }

    private fun setChoice() {
        val view = choicesContainer.findViewWithTag<ChoiceCardView>(selectedChoice)
        view.setChoiceText(editText.text.toString().trim())
        editText.setText("")
        composeAreaContainer.visibility = View.GONE
    }

    private fun updateViewFromDraftItem(draftPost: PostModel) {
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
    }

    private fun addChoiceView() {
        addChoiceView("")
    }

    private fun setOnClickListenerOnChoice(newChild: ChoiceCardView) {
        newChild.setOnClickListener {
            selectedChoice = it.tag as Int
            if (it is ChoiceCardView) {
                composeAreaContainer.visibility = View.VISIBLE
                composeHeader.text = "Choice ${selectedChoice.toString()}"
                editText.setText(newChild.getChoiceText())
                editText.hint = "Type here"
                editText.requestFocus()
                KeyboardUtils.showKeyboard(activity!!, editText)
            }
        }
    }

    companion object {
        private const val TAG = "NewTextPostFragment"
        private const val MAX_NUMBER_OF_CHOICES = 4
    }
}