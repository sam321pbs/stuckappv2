package com.sammengistu.stuckapp.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckfirebase.constants.PostType
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_new_image_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import java.io.FileNotFoundException


class NewImagePostFragment : BaseNewPostFragment() {

    var mBitmap1: Bitmap? = null
    var mBitmap2: Bitmap? = null

    override fun getLayoutId(): Int {
        return com.sammengistu.stuckapp.R.layout.fragment_new_image_post
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_1_container.setOnClickListener {
            loadImageFromGallery(REQUEST_LOAD_IMG_1)
        }

        image_2_container.setOnClickListener {
            loadImageFromGallery(REQUEST_LOAD_IMG_2)
        }

        submit_button.setOnClickListener {
            createImagePost(PostType.LANDSCAPE, mBitmap1, mBitmap2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOAD_IMG_1 -> mBitmap1 = addImageToView(image_1, data)
            REQUEST_LOAD_IMG_2 -> mBitmap2 = addImageToView(image_2, data)
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

    private fun addImageToView(imageView: ImageView, data: Intent?): Bitmap? {
        try {
            val imageUri = data!!.data
            val imageStream = activity!!.contentResolver.openInputStream(imageUri!!)
            Picasso.get()
                .load(imageUri)
                .fit()
                .centerCrop()
                .into(imageView)
            return BitmapFactory.decodeStream(imageStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun loadImageFromGallery(requestCode: Int) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, requestCode)
    }

    companion object {
        val TAG = NewImagePostFragment::class.java.simpleName
        val REQUEST_LOAD_IMG_1 = 0
        val REQUEST_LOAD_IMG_2 = 1
    }
}