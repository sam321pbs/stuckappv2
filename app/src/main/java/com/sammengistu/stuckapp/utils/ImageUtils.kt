package com.sammengistu.stuckapp.utils


import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.IOException


class ImageUtils {
    companion object {
        fun getCameraPhotoOrientation(imageFilePath: String): Int {
            var rotate = 0
            try {
                val exif = ExifInterface(imageFilePath)
                val exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                Log.d("exifOrientation", exifOrientation)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return rotate
        }

        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height,
                matrix, true
            )
        }
    }
}