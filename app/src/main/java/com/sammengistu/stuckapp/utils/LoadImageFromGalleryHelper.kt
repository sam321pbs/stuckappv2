package com.sammengistu.stuckapp.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sammengistu.stuckapp.ImageFilePath
import com.sammengistu.stuckapp.helpers.PermissionHelper
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
                val path = ImageFilePath.getPath(activity, imageUri)
                val rotateBy = ImageUtils.getCameraPhotoOrientation(path).toFloat()
                Picasso.get()
                    .load(imageUri)
                    .rotate(rotateBy)
                    .fit()
//                .centerCrop()
                    .into(imageView)
                return ImageUtils.rotateImage(BitmapFactory.decodeStream(imageStream), rotateBy)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show()
            }
            return null
        }

        fun loadImageFromGallery(fragment: Fragment?, requestCode: Int) {
            if (PermissionHelper.isGrantedReadExternalStorage(fragment!!.activity!!)) {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                fragment.startActivityForResult(photoPickerIntent, requestCode)
            }
        }
    }
}