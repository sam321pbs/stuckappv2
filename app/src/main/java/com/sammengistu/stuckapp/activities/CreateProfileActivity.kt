package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckapp.ErrorNotifier.Companion.notifyError
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.views.FormItemTextView
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.activity_create_profile.*

class CreateProfileActivity : LoggedInActivity() {

    lateinit var usernameField: FormItemTextView
    lateinit var descriptionField: FormItemTextView
    lateinit var nameField: FormItemTextView
    lateinit var occupationField: FormItemTextView
    lateinit var educationField: FormItemTextView
    lateinit var ageGroupField: FormItemTextView
    lateinit var genderField: FormItemTextView
    lateinit var createProfileButton: Button

    private val formFieldsList = ArrayList<FormItemTextView>()

    override fun getLayoutId(): Int {
        return R.layout.activity_create_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        createProfileButton.setOnClickListener {
            if (allFieldsValid()) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val userModel = UserModel(
                    firebaseUser!!.uid,
                    // Todo: validate that username doesn't exists
                    usernameField.getText(),
                    "ava",
                    nameField.getText(),
                    occupationField.getText(),
                    educationField.getText(),
                    descriptionField.getText(),
                    ageGroupField.getText(),
                    genderField.getText(),
                    0,0,0
                    )

                UserAccess().createItemInFB(userModel, object : FirebaseItemAccess.OnItemCreated<UserModel> {
                    override fun onSuccess(item: UserModel) {
                        launchMainActivity()
                    }

                    override fun onFailed(e: Exception) {
                        notifyError(this@CreateProfileActivity, TAG, "Failed to create post", e)
                    }
                })
            }
        }
    }

    private fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mainIntent)
    }

    private fun allFieldsValid(): Boolean {
        for (fieldItem in formFieldsList) {
            if (fieldItem.getText().isEmpty()) {
                Toast.makeText(
                    this@CreateProfileActivity,
                    "${fieldItem.getTitle()} is empty",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }

    private fun initViews() {
        usernameField = username_field
        descriptionField = description_field
        nameField = name_field
        occupationField = occupation_field
        educationField = education_field
        ageGroupField = age_group_field
        genderField = gender_field
        createProfileButton = create_profile_button

        formFieldsList.add(usernameField)
        formFieldsList.add(descriptionField)
        formFieldsList.add(nameField)
        formFieldsList.add(occupationField)
        formFieldsList.add(educationField)
        formFieldsList.add(ageGroupField)
        formFieldsList.add(genderField)
    }
}