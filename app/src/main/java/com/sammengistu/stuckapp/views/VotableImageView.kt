package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.squareup.picasso.Picasso

class VotableImageView(context: Context, owner: String, postId: String, votePos: Int)
    : VotableContainer(context, owner, postId, votePos), DoubleTapGesture.DoubleTapListener {

    private val imageView = ImageView(context)
    private val textView = TextView(context)
    private val progressBar = ProgressBar(context)

    init {
        buildView()
    }

    override fun onItemVotedOn() {
        // Todo: start animation to show votes
    }

    fun setTotal(amount: Int) {
        textView.text = amount.toString()
    }

    fun loadImage(imageLoc: String) {
        post {
            Picasso.get()
                .load(imageLoc)
                .fit()
                .centerCrop()
                .into(imageView)
        }
    }

    private fun buildView() {
        val params = getParams()
        layoutParams = params
        imageView.layoutParams = params

        val params2 = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        params2.addRule(CENTER_IN_PARENT)
        progressBar.layoutParams = params2

        addView(progressBar)
        addView(imageView)
    }

    private fun getParams(): ViewGroup.LayoutParams =
        ViewGroup.LayoutParams(MATCH_PARENT, 300)

    companion object {
        val TAG = VotableImageView::class.java.simpleName

        fun createView(context: Context, owner: String, postId: String,
                       imageItem: Triple<String, String, Int>): VotableImageView {
            val votableImage = VotableImageView(context, owner, postId, imageItem.first.toInt())
            votableImage.loadImage(imageItem.second)
            votableImage.setTotal(imageItem.third)
            return votableImage
        }
    }

}