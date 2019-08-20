package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sammengistu.stuckapp.R

class IconToCountView(context: Context, attrs: AttributeSet) : LinearLayout(context) {
    var iconView: ImageView = ImageView(context)
    var countView: TextView = TextView(context)

    init {
        buildView()
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.IconToCountView,
            0, 0).apply {

            try {
                val iconId = getResourceId(R.styleable.IconToCountView_iconSrc, 0)
                var textCount = getString(R.styleable.IconToCountView_textCount)
                iconView.setImageDrawable(context.getDrawable(iconId))
                countView.text = textCount
            } finally {
                recycle()
            }
        }
    }

    fun setCount(countTotal: Int) {
        countView.text = countTotal.toString()
    }

    private fun buildView() {
        setPadding(10, 10, 10, 10)
        orientation = HORIZONTAL

        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = 10

        iconView.layoutParams = params
        addView(iconView)
        addView(countView)
    }
}