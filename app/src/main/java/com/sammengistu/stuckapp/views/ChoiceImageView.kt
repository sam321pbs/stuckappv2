package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.collections.PostVotesCollection
import com.sammengistu.stuckapp.helpers.ViewHelper
import com.sammengistu.stuckfirebase.models.ChoiceModel
import com.sammengistu.stuckfirebase.models.PostVoteModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.squareup.picasso.Picasso
import java.io.File

class ChoiceImageView(
    context: Context,
    postRef: String,
    postOwnerRef: String,
    choice: ChoiceModel,
    postVote: PostVoteModel?
) : VotableContainer(context, postRef, postOwnerRef, choice, postVote) {

    private val imageView = ImageView(context)
    private val votesTextView = TextView(context)
    private val progressBar = ProgressBar(context)

    init {
        buildView()
        buildVotesText()
        if (postRef.isBlank()) {
            loadImageFromFile(choice.data)
        } else {
            loadImage(choice.data)
        }
    }

    override fun onNewVoteCreated(postVote: PostVoteModel?) {
        // Todo: start animation to show votes
        handleVotedItem(postVote, true)
    }

    fun setTotal(amount: Int) {
        votesTextView.text = amount.toString()
    }

    private fun loadImage(imageLoc: String) {
        if (imageLoc.isNotBlank()) {
            post {
                Picasso.get()
                    .load(imageLoc)
                    .fit()
//                .centerCrop()
                    .into(imageView)
            }
        } else {
            Log.e(TAG, "Image URL is blank")
        }
    }

    private fun loadImageFromFile(imageLoc: String) {
        if (imageLoc.isNotBlank()) {
            post {
                val file = File(imageLoc)
                Picasso.get()
                    .load(file)
                    .fit()
//                .centerCrop()
                    .into(imageView)
            }
        } else {
            Log.e(TAG, "Image file path is blank")
        }
    }

    private fun isUsersPost() =
        UserRepository.getCurrentUser() != null &&
                UserRepository.getCurrentUser()!!.ref == postOwnerRef

    private fun buildView() {
        val params = getParams()
        layoutParams = params
        imageView.layoutParams = params

        val params2 = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        params2.addRule(CENTER_IN_PARENT)
        progressBar.layoutParams = params2

//        imageView.setOnClickListener {
//            val drawable = imageView.drawable as BitmapDrawable
//            val bitmap = drawable.bitmap
//                FullScreenImageActivity.startActivity(
//                    context,
//                    ViewHelper.convertDrawableToBitmap(bitmap))
//        }

        val userVote = PostVotesCollection.getInstance(context).getVoteForPost(postRef)
        if (userVote == null) {
            votesTextView.visibility = View.GONE
        } else {
            votesTextView.visibility = View.VISIBLE
        }

        addView(progressBar)
        addView(imageView)
    }

    private fun buildVotesText() {
        val votesParams = LayoutParams(ViewHelper.convertDpToPixel(30F, context).toInt(),
            ViewHelper.convertDpToPixel(30F, context).toInt())
        votesParams.marginEnd = 20
        votesParams.topMargin = 20
        votesParams.addRule(ALIGN_PARENT_END)
        votesTextView.layoutParams = votesParams
        votesTextView.gravity = Gravity.CENTER
        votesTextView.setBackgroundResource(R.drawable.circle_gray)
        votesTextView.setPadding(5, 5, 5, 5)
        votesTextView.textSize = 15f
        setTotal(choice.votes)
        handleVotedItem(postVote)

        addView(votesTextView)
    }

    private fun handleVotedItem(postVote: PostVoteModel?, isUpdate: Boolean = false) {
        if (isUsersPost()) {
            votesTextView.visibility = View.VISIBLE
        } else if (postVote == null) {
            votesTextView.visibility = View.GONE
        } else {
            votesTextView.visibility = View.VISIBLE
            if (choice.id == postVote.choiceId) {
                votesTextView.setBackgroundResource(R.drawable.circle_gold)
                if (isUpdate) {
                    setTotal(choice.votes + 1)
                }
            }
        }
    }

    private fun getParams(): ViewGroup.LayoutParams =
        ViewGroup.LayoutParams(MATCH_PARENT, ViewHelper.convertDpToPixel(200F, context).toInt())

    companion object {
        val TAG = ChoiceImageView::class.java.simpleName
    }
}