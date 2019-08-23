package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.sammengistu.stuckapp.R
import org.jetbrains.anko.centerVertically

class VotableChoiceView(context: Context, voteId: Int) :
    RelativeLayout(context) {

    val mBullet = ImageView(context)
    val mChoiceText = TextView(context)
    val mVotesText = TextView(context)
    var mGestureDetector = GestureDetector(context, GestureListener())

    init {
        buildViews()
        applyStyleManually(context)
    }

    companion object {
        fun createView(context: Context, choiceItem: Triple<Int, String, Int>): VotableChoiceView {
            val votableChoice = VotableChoiceView(context, choiceItem.first)
            votableChoice.setChoiceText(choiceItem.second)
            votableChoice.setTotal(choiceItem.third)
            return votableChoice
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    fun setChoiceText(choiceText: String) {
        mChoiceText.text = choiceText
    }

    fun setTotal(total: Int) {
        mVotesText.text = total.toString()
    }

    private fun buildViews() {
        buildBullet()
        buildVotesText()
        buildChoiceText()
        addOnDoubleClickListener()
    }

    private fun addOnDoubleClickListener() {

    }

    private fun buildBullet() {
        val bulletParams = LayoutParams(20, 20)
        bulletParams.centerVertically()
        bulletParams.marginEnd = 6
        bulletParams.marginStart = 6
        bulletParams.topMargin = 6
        bulletParams.bottomMargin = 6
        mBullet.id = View.generateViewId()
        mBullet.setImageResource(R.drawable.circle)
        mBullet.layoutParams = bulletParams
        addView(mBullet)
    }

    private fun buildVotesText() {
        val votesParams = LayoutParams(80, 80)
        votesParams.centerVertically()
        votesParams.marginEnd = 20
        votesParams.addRule(START_OF, mBullet.id)
        votesParams.addRule(ALIGN_PARENT_END)
        mVotesText.layoutParams = votesParams
        mVotesText.gravity = Gravity.CENTER
        mVotesText.setBackgroundResource(R.drawable.circle)
        mVotesText.setPadding(5, 5, 5, 5)
        mVotesText.textSize = 15f
        addView(mVotesText)
    }

    private fun buildChoiceText() {
        val choiceTextParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        choiceTextParams.centerVertically()
        choiceTextParams.addRule(END_OF, mBullet.id)
        choiceTextParams.addRule(START_OF, mVotesText.id)
        choiceTextParams.marginStart = 10
        mChoiceText.layoutParams = choiceTextParams
        mChoiceText.setTextColor(resources.getColor(android.R.color.black))
        mChoiceText.textSize = 21f
        addView(mChoiceText)
    }

    private fun applyStyleManually(context: Context) {
        val attrs =
            intArrayOf(
                android.R.attr.layout_width, android.R.attr.layout_height,
                android.R.attr.layout_margin, android.R.attr.padding
            )
        val attrChoices =
            context.theme.obtainStyledAttributes(
                R.style.votable_choice_style,
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

        val marginAndPadding = attrChoices.getDimension(attrChoices.getIndex(2), 10f)

        layoutParams = params

        params.topMargin = marginAndPadding.toInt()
        params.marginStart = marginAndPadding.toInt()
        params.marginEnd = marginAndPadding.toInt()
        params.bottomMargin = marginAndPadding.toInt()
        setPadding(marginAndPadding.toInt(), marginAndPadding.toInt(), marginAndPadding.toInt(), marginAndPadding.toInt())
        setBackgroundColor(resources.getColor(R.color.white))
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            this@VotableChoiceView.setBackgroundColor(resources.getColor(R.color.green))
            return true
        }
    }
}