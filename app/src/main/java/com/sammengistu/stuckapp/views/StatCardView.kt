package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.sammengistu.stuckapp.R

class StatCardView(context: Context, attrSet: AttributeSet) : CardView(context, attrSet) {

    private val titleTextView = TextView(context)
    private val statTextView = TextView(context)

    init {
        buildView()
        context.theme.obtainStyledAttributes(
            attrSet,
            R.styleable.StatCardView,
            0, 0
        ).apply {
            try {
                val title = getString(R.styleable.StatCardView_statTitle)
                val stat = getString(R.styleable.StatCardView_statTotal)
                titleTextView.text = title
                statTextView.text = stat
            } finally {
                recycle()
            }
        }
    }

    fun setStat(statTotal: Int) {
        statTextView.text = statTotal.toString()
    }

    private fun buildView() {
        val container = LinearLayout(context)
        val paramsContainer = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        paramsContainer.gravity = Gravity.CENTER
        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER
        container.layoutParams = paramsContainer

        val textParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        textParams.bottomMargin = 10
        textParams.gravity = Gravity.CENTER_HORIZONTAL
        titleTextView.layoutParams = textParams
        statTextView.layoutParams = textParams

        container.addView(titleTextView)
        container.addView(statTextView)

        addView(container)
    }
}