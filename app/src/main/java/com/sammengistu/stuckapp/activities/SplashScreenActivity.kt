package com.sammengistu.stuckapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.notification.StuckNotificationFactory
import com.sammengistu.stuckfirebase.ErrorNotifier.Companion.notifyError
import com.sammengistu.stuckfirebase.repositories.UserRepository
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_splash_screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try_again_container.visibility = View.GONE
        try_again_button.setOnClickListener {
            launchNextActivity()
        }

        launchNextActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try_again_container.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                launchNextActivity()
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                notifyError(this, "Error signing in")
                Log.d(TAG, "Error signing in: ${response!!.error!!.errorCode}")
                try_again_container.visibility = View.VISIBLE
                progress_bar.visibility = View.GONE
            }
        }
    }

    private fun launchNextActivity() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Log.e(TAG, "User is not signed in")
            launchSignInActivity()
        } else {
            UserRepository.getUserInstance(this) {
                if (it == null) {
                    launchProfileActivity()
                } else {
                    launchMainActivity()
                }
            }
        }
    }

    private fun launchSignInActivity() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher_1_round)
//                .setTheme(R.style.LoginTheme)
                .build(),
            RC_SIGN_IN
        )
    }

    fun launchProfileActivity() {
        val mainIntent = Intent(this, CreateProfileActivity::class.java)
        startActivity(mainIntent)
    }

    fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (intent != null &&
            intent.extras != null &&
            !intent.getStringExtra(StuckNotificationFactory.EXTRA_POST_REF).isNullOrBlank()) {
            val postRef = intent.getStringExtra(StuckNotificationFactory.EXTRA_POST_REF)
            mainIntent.putExtra(
                StuckNotificationFactory.EXTRA_POST_REF,
                postRef)
            Log.d(TAG, "Found ref $postRef")
            intent = null
        }
        startActivity(mainIntent)
    }

    companion object {
        val TAG = SplashScreenActivity::class.java.simpleName
    }
}