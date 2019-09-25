package com.sammengistu.stuckapp.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.BaseActivity
import com.sammengistu.stuckapp.activities.MainActivity
import com.sammengistu.stuckapp.constants.Gender.Companion.FEMALE
import com.sammengistu.stuckapp.constants.Gender.Companion.IGNORE
import com.sammengistu.stuckapp.constants.Gender.Companion.MALE
import com.sammengistu.stuckapp.events.UserUpdatedEvent
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper.Companion.loadImageFromGallery
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckapp.views.InputFormItemView
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.FbStorageHelper
import com.sammengistu.stuckfirebase.UserHelper
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
    lateinit var ageGroupSpinner: Spinner
    lateinit var genderSpinner: Spinner
    lateinit var createProfileButton: Button
    lateinit var progressBar: FrameLayout

    private var avatarImage: Bitmap? = null
    private var selectedAgeGroup: Int? = IGNORE
    private var selectedGender: Int? = IGNORE
    private val formFieldsList = ArrayList<InputFormItemView>()
    private var createMode = false
    private lateinit var arrayAgeGroup : Array<CharSequence>
    private lateinit var arrayGender : Array<CharSequence>


    override fun getFragmentTitle(): String = TITLE

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun getFragmentTag(): String = TAG

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createMode = arguments?.getBoolean(EXTRA_CREATE_MODE) ?: false
        arrayAgeGroup = activity!!.resources.getTextArray(R.array.age_group_array)
        arrayGender = activity!!.resources.getTextArray(R.array.gender_array)
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
                ageGroupSpinner.setSelection(convertAgeGroupToPos(user.ageGroup))
                genderSpinner.setSelection(convertGenderToPos(user.gender))
                createProfileButton.text = "Update Profile"
            }
        }
    }

    private fun handleProfileUpdate() {
        UserHelper.getCurrentUser { user ->
            if (user != null) {
                progressBar.visibility = View.VISIBLE
                val updateUser = buildUserModel(user.userId, user.avatar)
                if (!user.isEqualTo(updateUser) || avatarImage != null) {
                    checkUsernameBeforeUpdating(updateUser) {
                        updateUserAccount(user, updateUser)
                    }
                } else {
                    progressBar.visibility = View.GONE
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
                        progressBar.visibility = View.GONE
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
                            progressBar.visibility = View.GONE
                        } else {
                            postUserNameAction.invoke()
                        }
                    } else {
                        postUserNameAction.invoke()
                    }
                }

                override fun onFailed(e: Exception) {
                    progressBar.visibility = View.GONE
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
                        progressBar.visibility = View.GONE
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
                        progressBar.visibility = View.GONE
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
            progressBar.visibility = View.VISIBLE
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val userModel = buildUserModel(firebaseUser.uid, "")
                checkUsernameBeforeUpdating(userModel) {
                    UserAccess().createUser(avatarImage!!, userModel,
                        object : FirebaseItemAccess.OnItemCreated<UserModel> {
                            override fun onSuccess(item: UserModel) {
                                progressBar.visibility = View.GONE
                                UserHelper.currentUser = item
                                launchMainActivity()
                            }

                            override fun onFailed(e: Exception) {
                                progressBar.visibility = View.GONE
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
            selectedAgeGroup!!,
            selectedGender!!,
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
        ageGroupSpinner = age_group_spinner
        genderSpinner = gender_spinner
        createProfileButton = create_profile_button
        progressBar = progress_bar_container

        setupSpinners()

        formFieldsList.add(usernameField)
        formFieldsList.add(bioField)
        formFieldsList.add(nameField)
        formFieldsList.add(occupationField)
        formFieldsList.add(educationField)
    }

    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            activity!!,
            R.array.age_group_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            ageGroupSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            activity!!,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }

        ageGroupSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { selectedAgeGroup = IGNORE }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedAgeGroup = convertPosToAgeGroup(position)
                }

            }

        genderSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { selectedGender = IGNORE }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedGender = convertPosToGender(position)
                }
            }

        if (createMode) {
            ageGroupSpinner.setSelection(0)
            ageGroupSpinner.setSelection(0)
        }
    }

    private fun convertPosToAgeGroup(position: Int): Int {
        return when (position) {
            1 -> IGNORE
            2 -> 180
            3 -> 1825
            4 -> 2630
            5 -> 3140
            6 -> 4150
            7 -> 5160
            8 ->  6100
            else -> IGNORE
        }
    }

    private fun convertAgeGroupToPos(ageRange: Int): Int {
        return when (ageRange) {
            180 -> 2
            1825 -> 3
            2630 -> 4
            3140 -> 5
            4150 -> 6
            5160 -> 7
            6100 -> 8
            else -> 1
        }
    }

    private fun convertPosToGender(position: Int): Int {
        return when(position) {
            2 -> MALE
            3 -> FEMALE
            else -> IGNORE
        }
    }

    private fun convertGenderToPos(gender: Int): Int {
        return when(gender) {
            MALE -> 2
            FEMALE -> 3
            else -> 1
        }
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