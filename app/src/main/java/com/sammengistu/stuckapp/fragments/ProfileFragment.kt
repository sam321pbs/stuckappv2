package com.sammengistu.stuckapp.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuth
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.MainActivity
import com.sammengistu.stuckapp.constants.Gender.Companion.FEMALE
import com.sammengistu.stuckapp.constants.Gender.Companion.IGNORE
import com.sammengistu.stuckapp.constants.Gender.Companion.MALE
import com.sammengistu.stuckapp.dialog.GetImageFromDialog
import com.sammengistu.stuckapp.events.GetPhotoFromEvent
import com.sammengistu.stuckapp.events.OnAvatarSelected
import com.sammengistu.stuckapp.helpers.KeyboardStateHelper
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper
import com.sammengistu.stuckapp.utils.LoadImageFromGalleryHelper.Companion.loadImageFromGallery
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckapp.views.InputFormItemView
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : BaseFragment() {

    private lateinit var avatarView: AvatarView
    private lateinit var addAvatar: TextView
    private lateinit var usernameField: InputFormItemView
    private lateinit var bioField: InputFormItemView
    private lateinit var nameField: InputFormItemView
    private lateinit var occupationField: InputFormItemView
    private lateinit var educationField: InputFormItemView
    private lateinit var ageGroupSpinner: Spinner
    private lateinit var genderSpinner: Spinner
    private lateinit var createProfileButton: Button
    private lateinit var progressBar: FrameLayout

    private var avatarImage: Bitmap? = null
    private var selectedAgeGroup: Int? = IGNORE
    private var selectedGender: Int? = IGNORE
    private val formFieldsList = ArrayList<InputFormItemView>()
    private var createMode = false
    private lateinit var arrayAgeGroup: Array<CharSequence>
    private lateinit var arrayGender: Array<CharSequence>

    val userViewModel: UserViewModel by viewModels {
        InjectorUtils.provideUserFactory(requireContext())
    }

    @Subscribe
    fun onAvatarSelected(event: OnAvatarSelected) {
        avatarImage = event.bitmap
        avatarView.setImageBitmap(avatarImage)
    }

    @Subscribe
    fun getPhotoFrom(event: GetPhotoFromEvent) {
        if (event.choice == GetImageFromDialog.listOfChoices[0]) {
            loadImageFromGallery(this, REQUEST_LOAD_IMG_3)
        } else {
            addFragment(SelectAvatarFragment())
        }
    }

    override fun getFragmentTitle(): String = TITLE

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun getFragmentTag(): String = TAG

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createMode = arguments?.getBoolean(EXTRA_CREATE_MODE) ?: false
        arrayAgeGroup = activity!!.resources.getTextArray(R.array.age_group_array)
        arrayGender = activity!!.resources.getTextArray(R.array.gender_array)
        initViews()

        if (createMode) {
            val fbUser = FirebaseAuth.getInstance().currentUser
            val displayName = fbUser?.displayName ?: ""
            if (fbUser != null) {
                nameField.setText(displayName)
            }
        } else {
            UserRepository.getUserInstance(context!!) {
                if (it != null) {
                    userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
                        if (user != null) {
                            populateFields(user)
                        }
                    }
                    userViewModel.setUserRef(it.ref)
                }
            }
        }
        addAvatar.setOnClickListener {
            GetImageFromDialog().show(activity!!.supportFragmentManager, GetImageFromDialog.TAG)
        }
        createProfileButton.setOnClickListener {
            if (createMode) {
                createProfile()
            } else {
                handleProfileUpdate()
            }
        }

        KeyboardStateHelper(view) { open ->
            if (open) {
                createProfileButton.visibility = View.GONE
            } else {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (activity != null) {
                            activity!!.runOnUiThread {
                                if (createProfileButton != null) {
                                    createProfileButton.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }, 200)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
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

        if (avatarImage == null) {
            Toast.makeText(
                activity,
                "Avatar is empty",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun populateFields(user: UserModel?) {
        if (user != null) {
            if (user.avatar.isNotBlank()) {
                avatarView.loadImage(user.avatar)
                addAvatar.text = "Change Avatar"
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

    private fun handleProfileUpdate() {
        UserRepository.getUserInstance(context!!) { user ->
            if (user != null) {
                progressBar.visibility = View.VISIBLE
                val updateUser = buildUserModel(user)
                if (!user.isEqualTo(updateUser) || avatarImage != null) {
                    checkUsernameBeforeUpdating(updateUser) {
                        updateUserAccount(updateUser)
                    }
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(activity, "Please update a field", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserAccount(updateUser: UserModel) {
        if (avatarImage != null) {
            UserAccess().updateUserAndAvatar(avatarImage!!, updateUser,
                object : FirebaseItemAccess.OnItemUpdated {
                    override fun onSuccess() {
                        if (activity != null) {
                            progressBar.visibility = View.GONE
                            userViewModel.updateUser(updateUser)
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
        } else {
            sendUpdates(updateUser)
        }
    }

    private fun checkUsernameBeforeUpdating(
        updateUser: UserModel,
        postUserNameAction: () -> Unit
    ) {
        UserAccess().getItemsWhereEqual("username", updateUser.username,
            object : FirebaseItemAccess.OnItemsRetrieved<UserModel> {
                override fun onSuccess(list: List<UserModel>) {
                    if (list.isNotEmpty()) {
                        val fetchedUser = list[0]
                        if (fetchedUser.ref != updateUser.ref) {
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
            }, 1)
    }

    private fun sendUpdates(updatedUser: UserModel) {
        UserAccess().updateItemInFB(
            updatedUser.ref,
            updatedUser.convertUserToMap(),
            object : FirebaseItemAccess.OnItemUpdated {
                override fun onSuccess() {
                    if (activity != null) {
                        progressBar.visibility = View.GONE
                        userViewModel.updateUser(updatedUser)
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
                val userModel = buildUserModel(UserModel())
                checkUsernameBeforeUpdating(userModel) {
                    UserAccess().createUser(avatarImage!!, userModel,
                        object : FirebaseItemAccess.OnItemCreated<UserModel> {
                            override fun onSuccess(item: UserModel) {
                                progressBar.visibility = View.GONE
                                launchMainActivity()
                            }

                            override fun onFailed(e: Exception) {
                                progressBar.visibility = View.GONE
                                ErrorNotifier.notifyError(
                                    context!!,
                                    TAG,
                                    "Failed to create profile",
                                    e
                                )
                            }
                        })
                }
            }
        }
    }

    private fun buildUserModel(currentUser: UserModel): UserModel {
        val updatedUser = UserModel(
            currentUser._id,
            currentUser.userId,
            usernameField.getText().trim(),
            currentUser.avatar,
            nameField.getText().trim(),
            occupationField.getText().trim(),
            educationField.getText().trim(),
            bioField.getText().trim(),
            selectedAgeGroup!!,
            selectedGender!!,
            0, 0, 0
        )
        updatedUser.ref = currentUser.ref
        return updatedUser
    }

    private fun initViews() {
        avatarView = avatar_view
        addAvatar = text_add_avatar
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
//        formFieldsList.add(bioField)
        formFieldsList.add(nameField)
//        formFieldsList.add(occupationField)
//        formFieldsList.add(educationField)
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
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedAgeGroup = IGNORE
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedAgeGroup = convertPosToAgeGroup(position)
                }

            }

        genderSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedGender = IGNORE
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
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
            8 -> 6100
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
        return when (position) {
            2 -> MALE
            3 -> FEMALE
            else -> IGNORE
        }
    }

    private fun convertGenderToPos(gender: Int): Int {
        return when (gender) {
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