package com.sammengistu.stuckapp.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.NewPostActivity
import com.sammengistu.stuckapp.activities.NewPostActivity.Companion.EXTRA_POST_ID
import com.sammengistu.stuckapp.events.SaveDraftEvent
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.models.DraftPostModel
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_new_image_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.greenrobot.eventbus.Subscribe
import java.io.File


class NewImagePostFragment : BaseNewPostFragment() {

    var mBitmap1: Bitmap? = null
    var mBitmap2: Bitmap? = null
    lateinit var image1Select: TextView
    lateinit var image2Select: TextView

    @Subscribe
    fun onSaveDraft(event: SaveDraftEvent) {
        draftImagePost(PostType.LANDSCAPE, mBitmap1, mBitmap2)
    }

    override fun getFragmentTag(): String = TAG
    override fun getLayoutId(): Int = R.layout.fragment_new_image_post
    override fun getFragmentTitle(): String = TITLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image1Select = image_1_select
        image2Select = image_2_select

        image_1_container.setOnClickListener {
            LoadImageFromGalleryHelper.loadImageFromGallery(this, REQUEST_LOAD_IMG_1)
        }

        image_2_container.setOnClickListener {
            LoadImageFromGalleryHelper.loadImageFromGallery(this, REQUEST_LOAD_IMG_2)
        }

        image1Select.setOnClickListener {
            LoadImageFromGalleryHelper.loadImageFromGallery(this, REQUEST_LOAD_IMG_1)
        }

        image2Select.setOnClickListener {
            LoadImageFromGalleryHelper.loadImageFromGallery(this, REQUEST_LOAD_IMG_2)
        }

        submit_button.setOnClickListener {
            createImagePost(PostType.LANDSCAPE, mBitmap1, mBitmap2)
        }

        if (arguments != null) {
            val postId = arguments!!.getLong(EXTRA_POST_ID)
            val liveDraftPost = InjectorUtils.getDraftPostRepository(activity as Context).getDraftPost(postId)

            liveDraftPost.observe(viewLifecycleOwner) { draftList ->
                if (draftList.isNotEmpty()) {
                    draft = draftList[0]
                    updateViewFromDraft(draftList[0])
                    updateImagesFromDraft(draft!!)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOAD_IMG_1 -> { mBitmap1 =
                LoadImageFromGalleryHelper.addImageToView(activity, image_1, data)
                updateSelectImageText()
            }
            REQUEST_LOAD_IMG_2 -> { mBitmap2 =
                LoadImageFromGalleryHelper.addImageToView(activity, image_2, data)
                updateSelectImageText()
            }
        }
    }

    private fun updateSelectImageText() {
        if (mBitmap1 != null) {
            image1Select.visibility = View.GONE
        }
        if (mBitmap2 != null) {
            image2Select.visibility = View.GONE
        }
    }

    override fun fieldsValidated(showSnack: Boolean): Boolean {
        if (question.text.toString().isEmpty()) {
            Snackbar.make(view!!, "Question is empty", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (mBitmap1 == null || mBitmap2 == null) {
            Snackbar.make(view!!, "Images are empty", Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updateImagesFromDraft(draftPost: DraftPostModel) {
        if (draftPost.image1Loc.isNotBlank()) {
            val file1 = File(draftPost.image1Loc)
            mBitmap1 = BitmapFactory.decodeFile(file1.path)
            Picasso.get()
                .load(file1)
                .fit()
                .into(image_1)
        }

        if (draftPost.image2Loc.isNotBlank()) {
            val file2 = File(draftPost.image2Loc)
            mBitmap2 = BitmapFactory.decodeFile(file2.path)
            Picasso.get()
                .load(file2)
                .fit()
                .into(image_2)
        }
        updateSelectImageText()
    }

    companion object {
        const val TITLE = "New Image Post"
        val TAG = NewImagePostFragment::class.java.simpleName
        val REQUEST_LOAD_IMG_1 = 0
        val REQUEST_LOAD_IMG_2 = 1

        fun newInstance(postId: Long): NewImagePostFragment {
            val bundle = Bundle()
            bundle.putLong(NewPostActivity.EXTRA_POST_ID, postId)

            val frag = NewImagePostFragment()
            frag.arguments = bundle
            return frag
        }
    }
}