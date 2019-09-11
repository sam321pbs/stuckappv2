package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.BaseFragment

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
    }

    fun addFragment(fragment: BaseFragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .add(R.id.fragment_container, fragment)
            .addToBackStack(fragment.getFragmentTag())
            .commit()
    }

    fun addFragment(fragment: BaseFragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .add(R.id.fragment_container, fragment)
            .addToBackStack(fragment.getFragmentTag())
            .commit()

        supportActionBar?.title = title
    }

    fun launchSplashScreenActivity() {
        val intent = Intent(this, BaseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object {
        const val RC_SIGN_IN = 0
        val TAG = BaseActivity::class.java.simpleName
    }
}