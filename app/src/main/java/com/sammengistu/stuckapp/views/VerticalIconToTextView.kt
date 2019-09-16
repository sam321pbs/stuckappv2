package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sammengistu.stuckapp.R

class VerticalIconToTextView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var iconView: ImageView = ImageView(context)
    var textView: TextView = TextView(context)

    init {
        buildView()
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VerticalIconToTextView,
            0, 0).apply {

            if (attrs == null) {
                return@apply
            }
            try {
                val iconId = getResourceId(R.styleable.VerticalIconToTextView_verticalIconSrc, 0)
                val iconText = getString(R.styleable.VerticalIconToTextView_verticalIconText)
                if (iconId != 0) {
                    iconView.setImageDrawable(context.getDrawable(iconId))
                }
                textView.text = iconText
            } finally {
                recycle()
            }
        }
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setIcon(drawableId: Int) {
        iconView.setImageResource(drawableId)
    }

    private fun buildView() {
        setPadding(10, 10, 10, 10)
        orientation = VERTICAL
        textView.gravity = Gravity.CENTER_VERTICAL

        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER

        iconView.layoutParams = params
        textView.layoutParams = params

        textView.gravity = Gravity.CENTER
        addView(iconView)
        addView(textView)
    }
}