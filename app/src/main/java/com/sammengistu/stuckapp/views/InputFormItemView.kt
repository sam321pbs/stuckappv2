package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.sammengistu.stuckapp.R

class InputFormItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    var titleView: TextView = TextView(context)
    var itemEditText: EditText = EditText(context)

    init {
        orientation = VERTICAL
        buildView()
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InputFormItemView,
            0, 0).apply {

            try {
                var itemTitle = getString(R.styleable.InputFormItemView_formItemTitle)
                var itemHint = getString(R.styleable.InputFormItemView_formItemHint)
                if (itemHint.isNullOrBlank()) {
                    itemHint = "Enter Text"
                }
                if (itemTitle.isNullOrBlank()) {
                    itemTitle = "Title"
                }
                itemEditText.hint = itemHint
                titleView.text = itemTitle
            } finally {
                recycle()
            }
        }
    }

    fun setText(text: String) = itemEditText.setText(text)

    fun getText(): String = itemEditText.text.toString()

    fun getTitle(): String = titleView.text.toString()

    private fun buildView() {
//        setPadding(10, 10, 10, 10)


        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = 10
        params.marginStart = 10

        titleView.layoutParams = params
        itemEditText.layoutParams = params
        addView(titleView)
        addView(itemEditText)
    }
}