package com.sammengistu.stuckfirebase

import android.graphics.Bitmap
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class FbStorageHelper {

    interface StorageCompletionCallback {
        fun onSuccess()
        fun onFailed()
    }

    companion object {
        fun uploadImage(bitmap: Bitmap, callback: StorageCompletionCallback): String {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            // Todo: add owner id
            val imagesRef = storageRef.child("images/${UUID.randomUUID()}.png")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            imagesRef.putBytes(data)
                .addOnSuccessListener {
                    callback.onSuccess()
                }.addOnFailureListener {
                    callback.onFailed()
                }

            return imagesRef.path
        }

        fun downloadImage(ref: String) {
            val storage = FirebaseStorage.getInstance()
            var storageRef = storage.reference
            var islandRef = storageRef.child(ref)

            val ONE_MEGABYTE: Long = 1024 * 1024
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                // Data for "images/island.jpg" is returned, use this as needed
            }.addOnFailureListener {
                // Handle any errors
            }
        }
    }
}