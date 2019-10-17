package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sammengistu.stuckapp.R

class VerticalIconToTextView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var iconView: ImageView = ImageView(context)
    var textView: TextView = TextView(context)
    var highlightView: View = View(context)

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
        isSelected(false)
    }

    fun hideText(hide: Boolean) { textView.visibility = if (hide) View.GONE else View.VISIBLE }

    fun setText(text: String) {
        textView.text = text
    }

    fun setIcon(drawableId: Int) {
        iconView.setImageResource(drawableId)
    }

    fun isSelected(selected: Boolean) {
        if (selected) {
            highlightView.visibility = View.VISIBLE
        } else {
            highlightView.visibility = View.INVISIBLE
        }
    }

    private fun buildView() {
        setPadding(10, 10, 10, 10)
        orientation = VERTICAL
        textView.gravity = Gravity.CENTER_VERTICAL

        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER

        val highlightParams = LayoutParams(LayoutParams.MATCH_PARENT, 7)
        highlightParams.gravity = Gravity.CENTER
        highlightParams.bottomMargin = 10
        highlightView.layoutParams = highlightParams
        highlightView.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

        textView.layoutParams = params
        textView.gravity = Gravity.CENTER

        setupIconView()
        addView(textView)
        addView(highlightView)
    }

    fun setupIconView() {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        iconView.setPadding(10, 10, 10, 10)
        iconView.layoutParams = params
        addView(iconView)
    }
}