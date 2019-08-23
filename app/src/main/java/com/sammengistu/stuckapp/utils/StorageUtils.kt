package com.sammengistu.stuckapp.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class StorageUtils {
    companion object {
        fun saveToInternalStorage(context: Context, bitmapImage: Bitmap, imageName: String): String {
            val cw = ContextWrapper(context)
            // path to /data/data/yourapp/app_data/imageDir
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val mypath = File(directory, imageName)

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(mypath)
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return directory.absolutePath
        }
    }
}