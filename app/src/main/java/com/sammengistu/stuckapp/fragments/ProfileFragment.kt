package com.sammengistu.stuckapp.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckapp.ErrorNotifier
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.activities.BaseActivity
import com.sammengistu.stuckapp.activities.MainActivity
import com.sammengistu.stuckapp.events.UserUpdatedEvent
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper.Companion.loadImageFromGallery
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckapp.views.FormItemView
import com.sammengistu.stuckfirebase.FbStorageHelper
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.greenrobot.eventbus.EventBus

class ProfileFragment : BaseFragment() {

    lateinit var avatarView: AvatarView
    lateinit var usernameField: FormItemView
    lateinit var bioField: FormItemView
    lateinit var nameField: FormItemView
    lateinit var occupationField: FormItemView
    lateinit var educationField: FormItemView
    lateinit var ageGroupField: FormItemView
    lateinit var genderField: FormItemView
    lateinit var createProfileButton: Button
    var avatarImage: Bitmap? = null
    private val formFieldsList = ArrayList<FormItemView>()
    private var createMode = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun getFragmentTag(): String {
        return TAG
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createMode = arguments?.getBoolean(EXTRA_CREATE_MODE) ?: false
        initViews()
        if (!createMode) {
            populateFields()
        }
        avatarView.setOnClickListener {
            loadImageFromGallery(this, REQUEST_LOAD_IMG_3)
        }
        createProfileButton.setOnClickListener {
            if (createMode) {
                createProfile()
            } else {
                handleProfileUpdate()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOAD_IMG_3 -> avatarImage =
                LoadImageFromGalleryHelper.addImageToView(activity, avatarView, data)
        }
    }

    private fun allFieldsValid(): Boolean {
        for (fieldItem in formFieldsList) {
            if (fieldItem.getText().isEmpty()) {
                Toast.makeText(
                    activity,
                    "${fieldItem.getTitle()} is empty",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }

    private fun populateFields() {
        UserHelper.getCurrentUser { user ->
            if (user != null) {
                if (user.avatar.isNotBlank()) {
                    avatarView.loadImage(user.avatar)
                }
                usernameField.setText(user.username)
                bioField.setText(user.bio)
                nameField.setText(user.name)
                occupationField.setText(user.occupation)
                educationField.setText(user.education)
                ageGroupField.setText(user.ageGroup)
                genderField.setText(user.gender)
                createProfileButton.text = "Update Profile"
            }
        }
    }

    private fun handleProfileUpdate() {
        UserHelper.getCurrentUser { user ->
            val userModel = UserModel(
                user!!.userId,
                // Todo: validate that username doesn't exists
                usernameField.getText(),
                user.avatar,
                nameField.getText(),
                occupationField.getText(),
                educationField.getText(),
                bioField.getText(),
                ageGroupField.getText(),
                genderField.getText(),
                0, 0, 0
            )

            if (avatarImage != null) {
                FbStorageHelper.uploadAvatar(avatarImage!!,
                    object : FbStorageHelper.UploadCompletionCallback {
                        override fun onSuccess(url: String) {
                            user.avatar = url
                            updateProfile(user, userModel)
                        }

                        override fun onFailed(exception: Exception) {
                            ErrorNotifier.notifyError(activity!!, TAG, "Unable to update avatar", exception)
                        }
                    })
            } else {
                updateProfile(user, userModel)
            }
        }
    }

    private fun updateProfile(
        user: UserModel,
        userModel: UserModel
    ) {
        UserAccess().updateItemInFB(
            user.ref,
            convertUserToMap(userModel),
            object : FirebaseItemAccess.OnItemUpdated {
                override fun onSuccess() {
                    if (activity != null) {
                        UserHelper.currentUser = user
                        EventBus.getDefault().post(UserUpdatedEvent())
                        Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT)
                            .show()
                        activity!!.supportFragmentManager.popBackStack()
                    }
                }

                override fun onFailed(e: Exception) {
                    if (activity != null) {
                        ErrorNotifier.notifyError(
                            activity as Context,
                            TAG,
                            "Failed to update profile",
                            e
                        )
                    }
                }
            })
    }

    private fun convertUserToMap(user: UserModel): Map<String, Any> {
        return mapOf(
            Pair("username", user.username),
            Pair("avatar", user.avatar),
            Pair("name", user.name),
            Pair("occupation", user.occupation),
            Pair("education", user.education),
            Pair("bio", user.bio),
            Pair("ageGroup", user.ageGroup),
            Pair("gender", user.gender)
        )
    }

    private fun createProfile() {
        if (allFieldsValid()) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val userModel = UserModel(
                firebaseUser!!.uid,
                // Todo: validate that username doesn't exists
                usernameField.getText(),
                "",
                nameField.getText(),
                occupationField.getText(),
                educationField.getText(),
                bioField.getText(),
                ageGroupField.getText(),
                genderField.getText(),
                0, 0, 0
            )

            UserAccess().createUser(avatarImage!!, userModel,
                object : FirebaseItemAccess.OnItemCreated<UserModel> {
                    override fun onSuccess(item: UserModel) {
                        UserHelper.currentUser = item
                        launchMainActivity()
                    }

                    override fun onFailed(e: Exception) {
                        ErrorNotifier.notifyError(
                            context!!,
                            BaseActivity.TAG,
                            "Failed to create profile",
                            e
                        )
                    }
                })
        }
    }

    private fun initViews() {
        avatarView = avatar_view
        usernameField = username_field
        bioField = description_field
        nameField = name_field
        occupationField = occupation_field
        educationField = education_field
        ageGroupField = age_group_field
        genderField = gender_field
        createProfileButton = create_profile_button

        formFieldsList.add(usernameField)
        formFieldsList.add(bioField)
        formFieldsList.add(nameField)
        formFieldsList.add(occupationField)
        formFieldsList.add(educationField)
        formFieldsList.add(ageGroupField)
        formFieldsList.add(genderField)
    }

    private fun launchMainActivity() {
        val mainIntent = Intent(activity, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mainIntent)
    }

    companion object {

        val TAG = ProfileFragment::class.java.simpleName
        const val TITLE = "Profile"
        const val REQUEST_LOAD_IMG_3 = 3
        const val EXTRA_CREATE_MODE = "extra_create_mode"

        fun newInstance(createMode: Boolean): ProfileFragment {
            val bundle = Bundle()
            bundle.putBoolean(EXTRA_CREATE_MODE, createMode)

            val profileFragment = ProfileFragment()
            profileFragment.arguments = bundle
            return profileFragment
        }
    }
}