package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.BaseFragment

abstract class BaseActivity : AppCompatActivity() {

    private val mapFragmentTagsToCommits = HashMap<String, Int>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        supportFragmentManager.addOnBackStackChangedListener {
            val manager = supportFragmentManager
            val frag = manager.findFragmentById(R.id.fragment_container)
            if (frag != null) {
                onFragmentChanged(frag)
                updateTitle(frag)
            }
        }
    }

    override fun onBackPressed() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    open fun onFragmentChanged(topFragment: Fragment) {
        // Override me
    }

    fun addFragment(fragment: BaseFragment) {
        if (popToFragment(fragment.getFragmentTag())) {
            return
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val commitId = fragmentTransaction
            .add(R.id.fragment_container, fragment, fragment.getFragmentTag())
            .addToBackStack(fragment.getFragmentTag())
            .commit()

        mapFragmentTagsToCommits[fragment.getFragmentTag()] = commitId

        if (fragment.getFragmentTitle().isNotBlank()) {
            supportActionBar?.title = fragment.getFragmentTitle()
        }
    }

    fun launchSplashScreenActivity() {
        val intent = Intent(this, SplashScreenActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun updateTitle(title: String) {
        supportActionBar?.title = title
    }

    private fun popToFragment(fragmentTag: String): Boolean {
        val backStackFrag = supportFragmentManager.findFragmentByTag(fragmentTag)
        val commitId = mapFragmentTagsToCommits[fragmentTag]
        if (backStackFrag != null && commitId != null) {
            supportFragmentManager.popBackStack(commitId, 0)
            return true
        }
        return false
    }

    private fun updateTitle(frag: Fragment) {
        if (frag is BaseFragment) {
            supportActionBar?.title = frag.getFragmentTitle()
        }
    }

    companion object {
        const val RC_SIGN_IN = 0
        val TAG = BaseActivity::class.java.simpleName
    }
}