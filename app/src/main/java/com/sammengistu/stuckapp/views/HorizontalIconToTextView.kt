package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sammengistu.stuckapp.R

class HorizontalIconToTextView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    var iconView: ImageView = ImageView(context)
    var countView: TextView = TextView(context)

    init {
        buildView()
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HorizontalIconToTextView,
            0, 0).apply {

            try {
                val iconId = getResourceId(R.styleable.HorizontalIconToTextView_iconSrc, 0)
                val iconText = getString(R.styleable.HorizontalIconToTextView_iconText)
                val textColor = getResourceId(R.styleable.HorizontalIconToTextView_textColor,
                    context.resources.getColor(R.color.colorDefault))
                if (iconId != 0) {
                    iconView.setImageDrawable(context.getDrawable(iconId))
                }
                countView.text = iconText
                countView.setTextColor(context.resources.getColor(textColor))
            } finally {
                recycle()
            }
        }
    }

    fun setText(text: String) {
        countView.text = text
    }

    private fun buildView() {
        setPadding(15, 12, 15, 12)
        orientation = HORIZONTAL
        countView.gravity = Gravity.CENTER_VERTICAL

        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = 10

        iconView.layoutParams = params
        addView(iconView)
        addView(countView)
    }
}