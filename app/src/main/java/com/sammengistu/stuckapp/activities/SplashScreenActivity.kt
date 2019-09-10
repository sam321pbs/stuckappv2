package com.sammengistu.stuckapp.activities

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.UserModel

class SplashScreenActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_splash_screen
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            launchNextActivity(user)
        } else {
            // No user is signed in
            launchSignInActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    launchNextActivity(user)
                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.d(TAG, "Error signing in: ${response!!.error!!.errorCode}")
            }
        }
    }

    private fun launchNextActivity(user: FirebaseUser) {
        UserAccess().getItemsWhereEqual("userId", user.uid,
            object : FirebaseItemAccess.OnItemRetrieved<UserModel> {
                override fun onSuccess(list: List<UserModel>) {
                    if (list.isEmpty()) {
                        launchProfileActivity()
                    } else {
                        MainActivity.currentUser = list[0]
                        launchMainActivity()
                    }
                }

                override fun onFailed() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
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
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mainIntent)
    }

    companion object {
        val TAG = SplashScreenActivity::class.java.simpleName
    }
}