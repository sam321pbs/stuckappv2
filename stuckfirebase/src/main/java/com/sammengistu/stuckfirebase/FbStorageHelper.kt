package com.sammengistu.stuckfirebase

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*

val TAG = FbStorageHelper::class.java.simpleName

class FbStorageHelper {

    interface UploadCompletionCallback {
        fun onSuccess(url: String)
        fun onFailed()
    }

    interface DownloadCompletionCallback {
        fun onSuccess(byte: ByteArray)
        fun onFailed()
    }

    companion object {
//        fun uploadImage(bitmap: Bitmap, callback: UploadCompletionCallback): String {
//            val storageRef = FirebaseStorage.getInstance().reference
//            // Todo: add owner id
//            val imagesRef = storageRef.child("images/${UUID.randomUUID()}.png")
//
//            val baos = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//            val data = baos.toByteArray()
//
//            imagesRef.putBytes(data)
//                .addOnSuccessListener {
//                    callback.onSuccess()
//                }.addOnFailureListener {
//                    callback.onFailed()
//                }
//
//            return imagesRef.path
//        }

        fun uploadImage(bitmap: Bitmap, callback: UploadCompletionCallback) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val storageRef = FirebaseStorage.getInstance().reference
            val imagesRef = storageRef.child("images/${UUID.randomUUID()}.png")
            val uploadTask = imagesRef.putBytes(data)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.e(TAG, "Failed to up load", it)
                        callback.onFailed()
                    }
                }
                return@Continuation imagesRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    callback.onSuccess(downloadUri.toString())
                } else {
                    callback.onFailed()
                }
            }
        }

        fun downloadImage(ref: String, callback: DownloadCompletionCallback) {
            val storageRef = FirebaseStorage.getInstance().reference
            val islandRef = storageRef.child(ref)

            val ONE_MEGABYTE: Long = 1024 * 1024
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                callback.onSuccess(it)
            }.addOnFailureListener {
                Log.e(TAG, "Failed to download image", it)
                callback.onFailed()
            }
        }
    }
}