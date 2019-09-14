package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.LoggedInActivity

abstract class BaseFragment : Fragment() {
    abstract fun getLayoutId(): Int
    abstract fun getFragmentTag(): String
    abstract fun getFragmentTitle(): String

    var mViewCreated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewCreated = true
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewCreated = false;
    }

    fun getUserId(): String {
        if (activity is LoggedInActivity) {
            return (activity as LoggedInActivity).getFirebaseUserId()
        }
        return ""
    }

    fun addFragment(fragment: BaseFragment) {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction
            .add(R.id.fragment_container, fragment)
            .addToBackStack(fragment.getFragmentTag())
            .commit()
    }
}