package com.sammengistu.stuckapp.activities

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.sammengistu.stuckapp.ErrorNotifier.Companion.notifyError
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserHelper

class SplashScreenActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_splash_screen
    }

    override fun onResume() {
        super.onResume()
        launchNextActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
            }
        }
    }

    private fun launchNextActivity() {
        if (UserHelper.getFirebaseUser() == null) {
            Log.e(TAG, "User is not signed in")
            launchSignInActivity()
        } else {
            UserHelper.getCurrentUser {
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
        startActivity(mainIntent)
    }

    companion object {
        val TAG = SplashScreenActivity::class.java.simpleName
    }
}