package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.sammengistu.stuckapp.R

class DisplayFormItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    var titleView: TextView = TextView(context)
    var detailTextView: TextView = TextView(context)

    init {
        orientation = VERTICAL
        buildView()
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DisplayFormItemView,
            0, 0
        ).apply {

            try {
                val itemTitle = getString(R.styleable.DisplayFormItemView_displayTitle) ?: ""
                titleView.text = itemTitle
                titleView.textSize = 13f
                detailTextView.textSize = 20f
            } finally {
                recycle()
            }
        }
    }

    fun setText(text: String) = detailTextView.setText(text)

    private fun buildView() {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = 10
        params.marginStart = 10

        titleView.layoutParams = params
        detailTextView.layoutParams = params
        addView(titleView)
        addView(detailTextView)
    }
}