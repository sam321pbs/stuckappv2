package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.databinding.FragmentProfileViewBinding
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile_view.*


private val TAG = ProfileViewFragment::class.java.simpleName
private const val TITLE = "Profile"
private const val EXTRA_USER_ID = "extra_user_id"

class ProfileViewFragment : BaseFragment() {

    lateinit var binding: FragmentProfileViewBinding

    override fun getLayoutId() = R.layout.fragment_profile_view
    override fun getFragmentTag() = TAG
    override fun getFragmentTitle() = TITLE

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
        val userId = arguments?.getString(EXTRA_USER_ID)
        if (userId != null) {
            val userViewModel: UserViewModel by viewModels {
                InjectorUtils.provideUserFactory(requireContext())
            }
            userViewModel.userLiveData.observe(viewLifecycleOwner) { users ->
                when {
                    users == null -> {
                        activity!!.supportFragmentManager.popBackStack()
                        ErrorNotifier.notifyError(activity, "Error loading user")
                    }
                    users.isEmpty() -> {
                        activity!!.supportFragmentManager.popBackStack()
                        ErrorNotifier.notifyError(activity, "Can't find user")
                    }
                    else -> {
                        val user = users[0]
                        binding.user = user
                        if (user.avatar.isNotBlank()) {
                            avatar_view.loadImage(user.avatar)
                        }
                    }
                }
            }
            userViewModel.setUserId(userId)
        }
    }

    companion object {
        fun newInstance(userId : String): ProfileViewFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_USER_ID, userId)

            val frag = ProfileViewFragment()
            frag.arguments = bundle
            return frag
        }
    }
}