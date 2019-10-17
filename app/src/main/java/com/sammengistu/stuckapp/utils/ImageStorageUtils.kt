package com.sammengistu.stuckapp.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*


class ImageStorageUtils {
    companion object {
        fun saveToInternalStorage(
            context: Context,
            bitmapImage: Bitmap,
            imageName: String
        ): String {
            val cw = ContextWrapper(context)
            // path to /data/data/yourapp/app_data/imageDir
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val mypath = File(directory, "$imageName.png")

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
            return mypath.absolutePath
        }

        fun loadImageFromStorage(path: String): Bitmap? {
            try {
                val f = File(path)
                return BitmapFactory.decodeStream(FileInputStream(f))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }
    }
}