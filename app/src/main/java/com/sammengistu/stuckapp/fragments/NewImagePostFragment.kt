package com.sammengistu.stuckapp.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.constants.PostType
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.data.PostAccess
import com.sammengistu.stuckapp.utils.CreatePostItem
import com.sammengistu.stuckapp.utils.StorageUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_new_image_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.FileNotFoundException
import java.util.*


class NewImagePostFragment : BaseNewPostFragment(), CreatePostItem {

    var mImage1Bitmap: Bitmap? = null
    var mImage2Bitmap: Bitmap? = null

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
            createPost(view)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOAD_IMG_1 -> mImage1Bitmap = addImageToView(image_1, data)
            REQUEST_LOAD_IMG_2 -> mImage2Bitmap = addImageToView(image_2, data)
        }
    }

    override fun createPost(view: View) {
        if (fieldsValidated()) {
            doAsync {
                uiThread {
                    progress_bar.visibility = View.VISIBLE
                }

                var success = false
                try {
                    val post = Post(
                        "Sam_1",
                        username.text.toString(),
                        question.text.toString(),
                        mSelectedPrivacy,
                        mSelectedCategory,
                        PostType.LANDSCAPE,
                        StorageUtils.saveToInternalStorage(activity!!, mImage1Bitmap!!, getTimeAsString()),
                        StorageUtils.saveToInternalStorage(activity!!, mImage2Bitmap!!, getTimeAsString())
                    )

                    PostAccess.insertPost(activity!!.applicationContext, post)
                    success = true
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create post", e)
                }

                uiThread {
                    progress_bar.visibility = View.GONE
                    if (success) {
                        Toast.makeText(activity!!, "Post has been created", Toast.LENGTH_SHORT).show()
                        activity!!.finish()
                    } else {
                        Toast.makeText(activity!!, "Post failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun fieldsValidated(): Boolean {
        if (question.text.toString().isEmpty()) {
            Snackbar.make(view!!, "Question is empty", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (mImage1Bitmap == null || mImage2Bitmap == null) {
            Snackbar.make(view!!, "Images are empty", Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun getTimeAsString(): String {
        return Calendar.getInstance().timeInMillis.toString()
    }

    private fun addImageToView(imageView: ImageView, data: Intent?): Bitmap? {
        try {
            val imageUri = data!!.getData()
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