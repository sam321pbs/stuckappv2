package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.databinding.FragmentStatsBinding
import com.sammengistu.stuckfirebase.repositories.UserRepository

private const val TITLE = "Stats"
private val TAG = StatsFragment::class.java.simpleName

class StatsFragment: BaseFragment() {

    lateinit var binding: FragmentStatsBinding

    override fun getLayoutId(): Int = R.layout.fragment_stats
    override fun getFragmentTag(): String = TAG
    override fun getFragmentTitle(): String = TITLE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UserRepository.getUserInstance(context!!) { user ->
            if (user != null) {
                binding.user = user
            }
        }
    }
}