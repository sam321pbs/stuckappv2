package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.UserProfileOptionsAdapter
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.viewmodels.UserViewModel

class UserProfileFragment : BaseFragment() {

    private val userViewModel: UserViewModel by viewModels {
        InjectorUtils.provideUserFactory(context!!)
    }

    override fun getLayoutId() = R.layout.fragment_user_profile

    override fun getFragmentTag() = TAG

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val avatar = view.findViewById<AvatarView>(R.id.avatarView)
        val usernameTV = view.findViewById<TextView>(R.id.username)

        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                avatar.loadImage(user.avatar)
                usernameTV.text = user.username
                setupRecyclerView(view)
            }
        }

        UserRepository.getUserInstance(context!!) {
            if (it != null) {
                userViewModel.setUserRef(it.ref)
            }
        }
    }

    private fun setupRecyclerView(view: View) {
        view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            this.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = UserProfileOptionsAdapter(context, findNavController())

            val dividerItemDecoration = DividerItemDecoration(
                this.context,
                LinearLayoutManager.VERTICAL
            )
            this.addItemDecoration(dividerItemDecoration)
        }
    }

    companion object {
        private const val TAG = "UserProfile"
    }

}