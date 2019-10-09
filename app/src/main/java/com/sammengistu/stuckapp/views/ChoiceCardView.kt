package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.cardview.widget.CardView
import com.sammengistu.stuckapp.R


class ChoiceCardView(context: Context, attrs: AttributeSet?, val clearClicked: OnClearClicked) :
    CardView(ContextThemeWrapper(context, R.style.new_post_choice), attrs) {

    private val choiceText = TextView(context)
    private val container = RelativeLayout(context)

    interface OnClearClicked {
        fun onClearClicked(tag: Int)
    }

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
                    choiceText.hint =
                        getString(R.styleable.ChoiceCardView_hintText)
                } finally {
                    recycle()
                }
            }
        }
        setPadding(15, 15, 15, 15)
        buildViews()
    }

    constructor(context: Context, clearClicked: OnClearClicked) : this(context, null, clearClicked)

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

        radius = 10f
        choiceText.textSize = 17f
        choiceText.setPadding(10, 10, 10, 10)
        choiceText.setBackgroundColor(context.resources.getColor(R.color.white))
        choiceText.setTextColor(context.resources.getColor(R.color.black))
        setBackgroundColor(context.resources.getColor(R.color.white))
    }

    fun setHint(hint: String) {
        choiceText.hint = hint
    }

    fun getChoiceText(): String {
        return choiceText.text.toString()
    }

    fun setChoiceText(text: String) {
        return choiceText.setText(text)
    }

    private fun buildViews() {
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        container.layoutParams = params
        params.marginEnd = 10
        params.marginStart = 10
        params.topMargin = 10
        choiceText.layoutParams = params

        val iconParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        val clearIcon = ImageView(context)

        iconParams.addRule(RelativeLayout.ALIGN_PARENT_END)
        clearIcon.layoutParams = iconParams
        clearIcon.setImageDrawable(context.getDrawable(R.drawable.ic_clear_blue_400_24dp))
        clearIcon.setOnClickListener {
            clearClicked.onClearClicked(tag as Int)
        }
        container.setPadding(15,15,15,15)
        container.addView(choiceText)
        container.addView(clearIcon)
        addView(container)
    }
}