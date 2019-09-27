package com.sammengistu.stuckapp.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import java.io.FileNotFoundException

class LoadImageFromGalleryHelper {
    companion object {
        fun addImageToView(activity: Activity?, imageView: ImageView, data: Intent?): Bitmap? {
            try {
                if (data == null) {
                    return null
                }
                val imageUri = data.data
                val imageStream = activity!!.contentResolver.openInputStream(imageUri!!)
                Picasso.get()
                    .load(imageUri)
                    .fit()
//                .centerCrop()
                    .into(imageView)
                return BitmapFactory.decodeStream(imageStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show()
            }
            return null
        }

        fun loadImageFromGallery(fragment: Fragment?, requestCode: Int) {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            fragment!!.startActivityForResult(photoPickerIntent, requestCode)
        }
    }
}