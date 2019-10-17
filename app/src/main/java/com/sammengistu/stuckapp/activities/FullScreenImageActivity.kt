package com.sammengistu.stuckapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_full_screen_image.*


class FullScreenImageActivity: LoggedInActivity() {
    override fun getLayoutId(): Int {
        return com.sammengistu.stuckapp.R.layout.activity_full_screen_image
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val byteArray = intent.getByteArrayExtra(EXTRA_IMAGE_ARRAY)
        if (sBitmapArray == null) {
            finish()
        } else {
            val bmp = BitmapFactory.decodeByteArray(sBitmapArray, 0, sBitmapArray!!.size)
            val image = image_view
            image.setImageBitmap(bmp)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sBitmapArray = null
    }

    companion object {
        const val EXTRA_IMAGE_ARRAY = "extra_image_array"
        var sBitmapArray: ByteArray? = null

        fun startActivity(context: Context, imageArray: ByteArray) {
            val intent = Intent(context, FullScreenImageActivity::class.java)
            sBitmapArray = imageArray
//            intent.putExtra(EXTRA_IMAGE_ARRAY, imageArray)
            context.startActivity(intent)
        }
    }

}