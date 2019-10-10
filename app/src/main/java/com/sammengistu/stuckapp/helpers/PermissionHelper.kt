package com.sammengistu.stuckapp.helpers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sammengistu.stuckfirebase.ErrorNotifier

class PermissionHelper {
    companion object {
        val TAG = PermissionHelper::class.java.simpleName
        fun isGrantedReadExternalStorage(activity: Activity): Boolean {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                val showError = true
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 10)
                }

                if (showError)
                    ErrorNotifier.notifyError(activity, TAG, "Permission required")

                return false
            } else {
                // Permission has already been granted
                return true
            }
        }
    }
}