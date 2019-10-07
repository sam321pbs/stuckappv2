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
    updateParentContainer: UpdateParentContainer?
) : VotableContainer(context, post, choiceItem, userVote, updateParentContainer) {

    private val bullet = ImageView(context)
    private val choiceText = TextView(context)
    private val votesText = TextView(context)

    init {
        buildViews()
    }

    override fun onItemVotedOn(userVote: UserVoteModel?) {
        // Todo: start animation to show votes
        handleVotedItem(userVote, true)
    }

    fun setChoiceText(choiceText: String) {
        this.choiceText.text = choiceText
    }

    fun setTotal(total: Int) {
        votesText.text = total.toString()
    }

    private fun buildViews() {
        buildBullet()
        buildVotesText()
        buildChoiceText()
    }

    private fun buildBullet() {
        val bulletParams = LayoutParams(20, 20)
        bulletParams.setMargins(  0,30,0,0)
        bullet.id = View.generateViewId()
        bullet.setImageResource(R.drawable.circle_gray)
        bullet.layoutParams = bulletParams
        addView(bullet)
    }

    private fun buildVotesText() {
        val votesParams = LayoutParams(
            ViewHelper.convertDpToPixel(30F, context).toInt(),
            ViewHelper.convertDpToPixel(30F, context).toInt())
        votesParams.marginEnd = 20
        votesParams.addRule(START_OF, bullet.id)
        votesParams.addRule(ALIGN_PARENT_END)
        votesText.id = View.generateViewId()
        votesText.layoutParams = votesParams
        votesText.gravity = Gravity.CENTER
        votesText.setBackgroundResource(R.drawable.circle_gray)
        votesText.setPadding(2, 5, 5, 5)
        votesText.textSize = 15f
        setBackgroundColor(resources.getColor(R.color.white))
        setTotal(choiceItem.third)

        handleVotedItem(userVote)
        addView(votesText)
    }

    private fun buildChoiceText() {
        val choiceTextParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        choiceTextParams.centerVertically()
        choiceTextParams.addRule(END_OF, bullet.id)
        choiceTextParams.addRule(START_OF, votesText.id)
        choiceTextParams.marginStart = 10
        choiceText.layoutParams = choiceTextParams
        choiceText.setTextColor(resources.getColor(android.R.color.black))
        choiceText.textSize = 21f
        setChoiceText(choiceItem.second)
        addView(choiceText)
    }

    private fun handleVotedItem(userVote: UserVoteModel?, isUpdate: Boolean = false) {
        if (isUsersPost()) {
            votesText.visibility = View.VISIBLE
        } else if (userVote == null) {
            votesText.visibility = View.INVISIBLE
        } else {
            votesText.visibility = View.VISIBLE
            if (choiceItem.first == userVote.voteItem) {
                votesText.setBackgroundResource(R.drawable.circle_gold)
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