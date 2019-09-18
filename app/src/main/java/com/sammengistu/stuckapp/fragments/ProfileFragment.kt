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
import com.sammengistu.stuckapp.views.InputFormItemView
import com.sammengistu.stuckfirebase.FbStorageHelper
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.greenrobot.eventbus.EventBus

class ProfileFragment : BaseFragment() {

    lateinit var avatarView: AvatarView
    lateinit var usernameField: InputFormItemView
    lateinit var bioField: InputFormItemView
    lateinit var nameField: InputFormItemView
    lateinit var occupationField: InputFormItemView
    lateinit var educationField: InputFormItemView
    lateinit var ageGroupField: InputFormItemView
    lateinit var genderField: InputFormItemView
    lateinit var createProfileButton: Button
    var avatarImage: Bitmap? = null
    private val formFieldsList = ArrayList<InputFormItemView>()
    private var createMode = false

    override fun getFragmentTitle(): String = TITLE

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun getFragmentTag(): String = TAG

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
            if (user != null) {
                val updateUser = buildUserModel(user.userId, user.avatar)
                if (!user.isEqualTo(updateUser) || avatarImage != null) {
                    checkUsernameBeforeUpdating(updateUser) {
                        updateUserAccount(user, updateUser)
                    }
                } else {
                    Toast.makeText(activity, "Please update a field", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserAccount(
        user: UserModel,
        updateUser: UserModel
    ) {
        if (avatarImage != null) {
            FbStorageHelper.uploadAvatar(avatarImage!!,
                object : FbStorageHelper.UploadCompletionCallback {
                    override fun onSuccess(url: String) {
                        user.avatar = url
                        sendUpdates(user, updateUser)
                    }

                    override fun onFailed(exception: Exception) {
                        ErrorNotifier.notifyError(
                            activity!!,
                            TAG,
                            "Unable to update avatar",
                            exception
                        )
                    }
                })
        } else {
            sendUpdates(user, updateUser)
        }
    }

    private fun checkUsernameBeforeUpdating(
        updateUser: UserModel,
        postUserNameAction: () -> Unit
    ) {
        UserAccess().getItemsWhereEqual("username", updateUser.username,
            object : FirebaseItemAccess.OnItemRetrieved<UserModel> {
                override fun onSuccess(list: List<UserModel>) {
                    if (list.isNotEmpty()) {
                        val fetchedUser = list[0]
                        if (fetchedUser.userId != updateUser.userId) {
                            Toast.makeText(activity, "Username is taken", Toast.LENGTH_SHORT).show()
                        } else {
                            postUserNameAction.invoke()
                        }
                    } else {
                        postUserNameAction.invoke()
                    }
                }

                override fun onFailed(e: Exception) {
                    ErrorNotifier.notifyError(activity, "Error Occurred", TAG, e)
                }
            })
    }

    private fun sendUpdates(
        user: UserModel,
        updatedUser: UserModel
    ) {
        UserAccess().updateItemInFB(
            user.ref,
            updatedUser.convertUserToMap(),
            object : FirebaseItemAccess.OnItemUpdated {
                override fun onSuccess() {
                    if (activity != null) {
                        updatedUser.ref = user.ref
                        UserHelper.currentUser = updatedUser
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

    private fun createProfile() {
        if (allFieldsValid()) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val userModel = buildUserModel(firebaseUser.uid, "")
                checkUsernameBeforeUpdating(userModel) {
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
        }
    }

    private fun buildUserModel(userId: String, avatar: String): UserModel {
        return UserModel(
            userId,
            usernameField.getText(),
            avatar,
            nameField.getText(),
            occupationField.getText(),
            educationField.getText(),
            bioField.getText(),
            ageGroupField.getText(),
            genderField.getText(),
            0, 0, 0
        )
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