package com.sammengistu.stuckapp.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.events.SaveDraftEvent
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper
import com.sammengistu.stuckfirebase.constants.PostType
import kotlinx.android.synthetic.main.fragment_new_image_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.greenrobot.eventbus.Subscribe


class NewImagePostFragment : BaseNewPostFragment() {

    var mBitmap1: Bitmap? = null
    var mBitmap2: Bitmap? = null

    @Subscribe
    fun onSaveDraft(event: SaveDraftEvent) {
        saveAsDraft(PostType.LANDSCAPE)
    }

    override fun getFragmentTag(): String = TAG

    override fun getLayoutId(): Int = R.layout.fragment_new_image_post

    override fun getFragmentTitle(): String = TITLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_1_container.setOnClickListener {
            LoadImageFromGalleryHelper.loadImageFromGallery(this, REQUEST_LOAD_IMG_1)
        }

        image_2_container.setOnClickListener {
            LoadImageFromGalleryHelper.loadImageFromGallery(this, REQUEST_LOAD_IMG_2)
        }

        submit_button.setOnClickListener {
            createImagePost(PostType.LANDSCAPE, mBitmap1, mBitmap2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOAD_IMG_1 -> mBitmap1 = LoadImageFromGalleryHelper.addImageToView(activity, image_1, data)
            REQUEST_LOAD_IMG_2 -> mBitmap2 = LoadImageFromGalleryHelper.addImageToView(activity, image_2, data)
        }
    }

    override fun fieldsValidated(): Boolean {
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

    companion object {
        const val TITLE = "New Image Post"
        val TAG = NewImagePostFragment::class.java.simpleName
        val REQUEST_LOAD_IMG_1 = 0
        val REQUEST_LOAD_IMG_2 = 1
    }
}