package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.cardview.widget.CardView
import com.sammengistu.stuckapp.R


class ChoiceCardView(context: Context, attrs: AttributeSet?) :
    CardView(ContextThemeWrapper(context, R.style.new_post_choice), attrs) {

    private val choiceEditText: EditText = EditText(context)

    init {
        if (attrs == null) {
            applyStyleManually(context)
        } else {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ChoiceCardView,
                0, 0
            ).apply {
                try {
                    choiceEditText.hint =
                        getString(R.styleable.ChoiceCardView_hintText)
                } finally {
                    recycle()
                }
            }
        }
        setPadding(5, 5, 5, 5)
        buildEditTextView()
    }

    constructor(context: Context) : this(context, null)

    private fun applyStyleManually(context: Context) {
        val attrs =
            intArrayOf(
                android.R.attr.layout_width, android.R.attr.layout_height,
                android.R.attr.layout_marginTop, android.R.attr.layout_marginStart,
                android.R.attr.layout_marginEnd
            )
        val attrChoices =
            context.theme.obtainStyledAttributes(
                R.style.new_post_choice,
                attrs
            )

        val params = LinearLayout.LayoutParams(
            attrChoices.getLayoutDimension(
                attrChoices.getIndex(0),
                ViewGroup.LayoutParams.MATCH_PARENT
            ),
            attrChoices.getLayoutDimension(
                attrChoices.getIndex(1),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val marginTop = attrChoices.getDimension(attrChoices.getIndex(2), 10f)
        val marginStart = attrChoices.getDimension(attrChoices.getIndex(3), 10f)
        val marginEnd = attrChoices.getDimension(attrChoices.getIndex(4), 10f)

        layoutParams = params

        params.topMargin = marginTop.toInt()
        params.marginStart = marginStart.toInt()
        params.marginEnd = marginEnd.toInt()
    }

    fun setHint(hint: String) {
        choiceEditText.hint = hint
    }

    fun getChoiceText(): String {
        return choiceEditText.text.toString()
    }

    fun setChoiceText(text: String) {
        return choiceEditText.setText(text)
    }

    private fun buildEditTextView() {
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.marginEnd = 10
        params.marginStart = 10
        params.topMargin = 10
        choiceEditText.layoutParams = params
        addView(choiceEditText)
    }
}