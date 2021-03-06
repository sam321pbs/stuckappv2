package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import com.sammengistu.stuckapp.R

class InputFormItemView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    var titleView: TextView = TextView(context)
    var itemEditText: EditText = EditText(context)
    var containerForm: LinearLayout = LinearLayout(context)
    var addFieldButton: TextView = TextView(context)

    init {
        containerForm.orientation = VERTICAL
        buildView()
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InputFormItemView,
            0, 0).apply {

            try {
                var itemTitle = getString(R.styleable.InputFormItemView_formItemTitle)
                var itemHint = getString(R.styleable.InputFormItemView_formItemHint)
                val itemTag = getString(R.styleable.InputFormItemView_formItemTag)
                val itemOptional = getBoolean(R.styleable.InputFormItemView_formItemOptional, false)

                if (itemHint.isNullOrBlank()) itemHint = "Enter Text"
                if (itemTitle.isNullOrBlank()) itemTitle = "Title"

                itemEditText.hint = itemHint
                titleView.text = itemTitle
                addFieldButton.text = "Add $itemTag"
                showField(itemOptional)
            } finally {
                recycle()
            }
        }
    }

    fun getText(): String = itemEditText.text.toString()
    fun getTitle(): String = titleView.text.toString()

    fun setText(text: String) {
        itemEditText.setText(text)
        if (text.isNotBlank()) {
            showField(false)
        }
    }

    fun showField(optional: Boolean) {
        if (!optional) {
            containerForm.visibility = View.VISIBLE
            addFieldButton.visibility = View.GONE
        } else {
            containerForm.visibility = View.GONE
            addFieldButton.visibility = View.VISIBLE
        }
    }

    private fun buildView() {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = 10
        params.marginStart = 10

        titleView.layoutParams = params
        itemEditText.layoutParams = params
        containerForm.layoutParams = params

        val paramsButton = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        paramsButton.marginEnd = 10
        paramsButton.marginStart = 10
        paramsButton.gravity = Gravity.CENTER

        addFieldButton.gravity = Gravity.CENTER
        addFieldButton.setPadding(20,20,20,20)
        addFieldButton.setTextColor(context.resources.getColor(android.R.color.holo_blue_dark))
        addFieldButton.layoutParams = paramsButton

        addFieldButton.setOnClickListener { showField(false) }
        containerForm.addView(titleView)
        containerForm.addView(itemEditText)
        addView(containerForm)
        addView(addFieldButton)
    }
}