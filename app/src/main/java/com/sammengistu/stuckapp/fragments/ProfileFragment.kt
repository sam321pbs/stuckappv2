package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.views.FormItemView
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {

    lateinit var usernameField: FormItemView
    lateinit var bioField: FormItemView
    lateinit var nameField: FormItemView
    lateinit var occupationField: FormItemView
    lateinit var educationField: FormItemView
    lateinit var ageGroupField: FormItemView
    lateinit var genderField: FormItemView
    lateinit var createProfileButton: Button

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun getFragmentTag(): String {
        return TAG
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        populateFields()
        createProfileButton.setOnClickListener {
            handleProfileUpdate()
        }
    }

    private fun populateFields() {
        UserHelper.getCurrentUser { user ->
           if (user != null) {
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
                "ava",
                nameField.getText(),
                occupationField.getText(),
                educationField.getText(),
                bioField.getText(),
                ageGroupField.getText(),
                genderField.getText(),
                0, 0, 0
            )

//            UserAccess().updateItemInFB(
//                userModel,
//                object : FirebaseItemAccess.OnItemCreated<UserModel> {
//                    override fun onSuccess(item: UserModel) {
//                        launchMainActivity()
//                    }
//
//                    override fun onFailed(e: Exception) {
//                        ErrorNotifier.notifyError(
//                            this@CreateProfileActivity,
//                            BaseActivity.TAG,
//                            "Failed to create post",
//                            e
//                        )
//                    }
//                })
        }
    }

    private fun initViews() {
        usernameField = username_field
        bioField = description_field
        nameField = name_field
        occupationField = occupation_field
        educationField = education_field
        ageGroupField = age_group_field
        genderField = gender_field
        createProfileButton = create_profile_button
    }

    companion object {
        val TAG = ProfileFragment::class.java.simpleName
    }
}