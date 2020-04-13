package com.sammengistu.stuckapp.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.models.DraftPostModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_new_image_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import java.io.File


class NewImagePostFragment : BaseNewPostFragment() {

    private var bitmap1: Bitmap? = null
    private var bitmap2: Bitmap? = null
    private lateinit var image1Select: TextView
    private lateinit var image2Select: TextView

    private val args: NewImagePostFragmentArgs by navArgs()

    override fun getFragmentTag(): String = TAG

    override fun getLayoutId(): Int = R.layout.fragment_new_image_post

    override fun saveAsDraft() {
        val data = mapOf(Pair(IMAGE_1, bitmap1), Pair(IMAGE_2, bitmap2))
        saveDraft(PostType.LANDSCAPE, data)
    }

    override fun createPost() {
        createImagePost(PostType.LANDSCAPE, bitmap1, bitmap2)
    }

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

        val draftId = args.draftId
        if (draftId != -1) {
            val liveDraftPost = InjectorUtils
                .getPostRepository(activity as Context)
                .getDraftPost(draftId)

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
            REQUEST_LOAD_IMG_1 -> { bitmap1 =
                LoadImageFromGalleryHelper.addImageToView(activity, image_1, data)
                updateSelectImageText()
            }
            REQUEST_LOAD_IMG_2 -> { bitmap2 =
                LoadImageFromGalleryHelper.addImageToView(activity, image_2, data)
                updateSelectImageText()
            }
        }
    }

    private fun updateSelectImageText() {
        if (bitmap1 != null) {
            image1Select.visibility = View.GONE
        }
        if (bitmap2 != null) {
            image2Select.visibility = View.GONE
        }
    }

    override fun fieldsValidated(showSnack: Boolean): Boolean {
        if (question.text.toString().isEmpty()) {
            Snackbar.make(view!!, "Question is empty", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (bitmap1 == null || bitmap2 == null) {
            Snackbar.make(view!!, "Images are empty", Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updateImagesFromDraft(draftPost: DraftPostModel) {
        if (draftPost.image1Loc.isNotBlank()) {
            val file1 = File(draftPost.image1Loc)
            bitmap1 = BitmapFactory.decodeFile(file1.path)
            Picasso.get()
                .load(file1)
                .fit()
                .into(image_1)
        }

        if (draftPost.image2Loc.isNotBlank()) {
            val file2 = File(draftPost.image2Loc)
            bitmap2 = BitmapFactory.decodeFile(file2.path)
            Picasso.get()
                .load(file2)
                .fit()
                .into(image_2)
        }
        updateSelectImageText()
    }

    companion object {
        private const val TAG = "NewImagePostFragment"
        private const val REQUEST_LOAD_IMG_1 = 0
        private const val REQUEST_LOAD_IMG_2 = 1
    }
}