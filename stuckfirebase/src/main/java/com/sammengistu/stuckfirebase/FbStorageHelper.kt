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
        fun onFailed(exception: Exception)
    }

    interface DownloadCompletionCallback {
        fun onSuccess(byte: ByteArray)
        fun onFailed(exception: Exception)
    }

    companion object {
        fun uploadAvatar(bitmap: Bitmap, callback: UploadCompletionCallback) {
            uploadImage("avatars/${UUID.randomUUID()}.png", bitmap, callback)
        }

        fun uploadImage(bitmap: Bitmap, callback: UploadCompletionCallback) {
            uploadImage("images/${UUID.randomUUID()}.png", bitmap, callback)
        }

       private fun uploadImage(filePath: String, bitmap: Bitmap, callback: UploadCompletionCallback) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val storageRef = FirebaseStorage.getInstance().reference
            val imagesRef = storageRef.child(filePath)
            val uploadTask = imagesRef.putBytes(data)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.e(TAG, "Failed to upload image", it)
                        callback.onFailed(it)
                    }
                }
                return@Continuation imagesRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    callback.onSuccess(downloadUri.toString())
                }
            }.addOnFailureListener {
                callback.onFailed(it)
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
                callback.onFailed(it)
            }
        }
    }
}