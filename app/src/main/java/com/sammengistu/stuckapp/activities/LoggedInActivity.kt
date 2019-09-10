package com.sammengistu.stuckapp.activities

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

abstract class LoggedInActivity : BaseActivity(), FirebaseAuth.AuthStateListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        if (auth.currentUser == null) {
            launchSplashScreenActivity()
        }
    }

    fun getFirebaseUserId(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid ?: ""
    }
}