package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.helpers.ViewHelper
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserVoteModel
import org.jetbrains.anko.centerVertically

class VotableTextChoiceView(
    context: Context,
    post: PostModel,
    choiceItem: Triple<String, String, Int>,
    userVote: UserVoteModel?,
    updateParentContainer: UpdateParentContainer
) : VotableContainer(context, post, choiceItem, userVote, updateParentContainer) {

    private val mBullet = ImageView(context)
    private val mChoiceText = TextView(context)
    private val mVotesText = TextView(context)

    init {
        buildViews()
    }

    override fun onItemVotedOn(userVote: UserVoteModel?) {
        // Todo: start animation to show votes
        handleVotedItem(userVote, true)
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
    }

    private fun buildBullet() {
        val bulletParams = LayoutParams(20, 20)
        bulletParams.setMargins(  0,30,0,0)
        mBullet.id = View.generateViewId()
        mBullet.setImageResource(R.drawable.circle_gray)
        mBullet.layoutParams = bulletParams
        addView(mBullet)
    }

    private fun buildVotesText() {
        val votesParams = LayoutParams(
            ViewHelper.convertDpToPixel(30F, context).toInt(),
            ViewHelper.convertDpToPixel(30F, context).toInt())
        votesParams.marginEnd = 20
        votesParams.addRule(START_OF, mBullet.id)
        votesParams.addRule(ALIGN_PARENT_END)
        mVotesText.id = View.generateViewId()
        mVotesText.layoutParams = votesParams
        mVotesText.gravity = Gravity.CENTER
        mVotesText.setBackgroundResource(R.drawable.circle_gray)
        mVotesText.setPadding(2, 5, 5, 5)
        mVotesText.textSize = 15f
        setBackgroundColor(resources.getColor(R.color.white))
        setTotal(choiceItem.third)

        handleVotedItem(userVote)
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
        setChoiceText(choiceItem.second)
        addView(mChoiceText)
    }

    private fun handleVotedItem(userVote: UserVoteModel?, isUpdate: Boolean = false) {
        if (isUsersPost()) {
            mVotesText.visibility = View.VISIBLE
        } else if (userVote == null) {
            mVotesText.visibility = View.GONE
        } else {
            mVotesText.visibility = View.VISIBLE
            if (choiceItem.first == userVote.voteItem) {
                setBackgroundColor(resources.getColor(R.color.green))
                if (isUpdate) {
                    setTotal(choiceItem.third + 1)
                }
            }
        }
    }

    private fun isUsersPost() =
        UserHelper.currentUser != null && UserHelper.currentUser!!.ref == post.ownerRef

    companion object {
        val TAG = VotableTextChoiceView::class.java.simpleName
    }
}