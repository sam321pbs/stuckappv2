package com.sammengistu.stuckapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.fragments.BaseFragment

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
    }

    abstract fun getLayoutId(): Int

    fun addFragment(fragment: BaseFragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .add(R.id.fragment_container, fragment)
            .addToBackStack(fragment.getFragmentTag())
            .commit()
    }
}