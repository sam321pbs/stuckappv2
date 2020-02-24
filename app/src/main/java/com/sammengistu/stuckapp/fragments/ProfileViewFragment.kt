package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.databinding.FragmentProfileViewBinding
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile_view.*

class ProfileViewFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileViewBinding
    private val args: ProfileViewFragmentArgs by navArgs()

    override fun getLayoutId() = R.layout.fragment_profile_view
    override fun getFragmentTag() = TAG

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
        loadUser()
    }

    private fun loadUser() {
        val ownerRef = args.ownerRef
        val userViewModel: UserViewModel by viewModels {
            InjectorUtils.provideUserFactory(requireContext())
        }
        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                null -> {
                    activity!!.supportFragmentManager.popBackStack()
                    ErrorNotifier.notifyError(activity, "Error loading user")
                }
                else -> {
                    binding.user = user
                    if (user.avatar.isNotBlank()) {
                        avatar_view.loadImage(user.avatar)
                    }
                }
            }
        }
        userViewModel.setUserRef(ownerRef)
    }

    companion object {
        private const val TAG = "ProfileViewFragment"
    }
}