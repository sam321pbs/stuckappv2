package com.sammengistu.stuckapp.activities

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.sammengistu.stuckapp.AlarmHelper
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.bottomsheet.BottomSheetHelper
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.events.ChangeBottomSheetStateEvent
import com.sammengistu.stuckapp.fragments.CategoriesPostsFragmentArgs
import com.sammengistu.stuckapp.fragments.PostViewFragmentArgs
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckapp.notification.StuckNotificationFactory
import com.sammengistu.stuckapp.setupWithNavController
import com.sammengistu.stuckapp.utils.StringUtils
import com.sammengistu.stuckfirebase.access.DeviceTokenAccess
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_post_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : LoggedInActivity() {

    private lateinit var bottomSheetHelper: BottomSheetHelper
    private lateinit var invisibleCover: View
    private lateinit var bottomNavigationView: BottomNavigationView

    private var currentNavController: LiveData<NavController>? = null
    private val appBarNavController : NavController
        get() = findNavController(R.id.nav_host_fragment)

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            val destinationId = destination.id
            
            if (destinationId == R.id.profileFragment ||
                destinationId == R.id.statsFragment ||
                destinationId == R.id.settingsFragment ||
                destinationId == R.id.newPostTypeFragment ||
                destinationId == R.id.newImagePostFragment ||
                destinationId == R.id.newTextPostFragment ||
                destinationId == R.id.commentsFragment ||
                destinationId == R.id.notificationSettingsFragment
            ) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }

            if (destinationId == R.id.categoriesPostsFragment && arguments != null) {
                destination.label =
                    StringUtils.capitilizeFirstLetter(
                        CategoriesPostsFragmentArgs.fromBundle(arguments).category)
            }
        }

    @Subscribe
    fun onChangeBottomSheet(event: ChangeBottomSheetStateEvent) {
        if (event.show && event.post != null) {
            showBottomSheet(event.post)
        } else {
            hideBottomSheet()
        }
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this)
        AssetImageUtils.initListOfImages(this)
        UserRepository.getUserInstance(this) { onUserLoaded(it) }

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()

        setupActionBar(appBarNavController)
        setupBottomSheet()
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        appBarNavController.addOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        handleStarterIntent()
        AlarmHelper.cancelDailyNotifier(this)
    }

    override fun onPause() {
        super.onPause()
        AlarmHelper.setDailyNotifier(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item!!.onNavDestinationSelected(appBarNavController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp() =
        NavigationUI.navigateUp(appBarNavController, drawer_layout) || super.onSupportNavigateUp()

    override fun onBackPressed() {
        //the code is beautiful enough without comments
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun goHome() { bottomNavigationView.selectedItemId = R.id.nav_home }

    private fun setupActionBar(navController: NavController) {
        setupNavigation()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupNavigation() {
        val homeDestinations = setOf(
            R.id.homeListFragment,
            R.id.categoriesFragment,
            R.id.newPostTypeFragment,
            R.id.favoritesListFragment,
            R.id.userProfileFragment
        )

        val appBarConfigurationBuilder = AppBarConfiguration.Builder(homeDestinations)
        appBarConfiguration = appBarConfigurationBuilder.build()
    }

    private fun onUserLoaded(user: UserModel?) {
        if (user != null) {
            UserStarredCollection.getInstance(this)
            UserVotesCollection.getInstance(this)
            DeviceTokenAccess(user.ref).checkTokenExists(this)
            HiddenItemsHelper(user.ref, this)
        }
    }

    private fun handleStarterIntent() {
        if (intent != null &&
            intent.extras != null &&
            !intent.getStringExtra(StuckNotificationFactory.EXTRA_POST_REF).isNullOrBlank()) {
            val postRef = intent.getStringExtra(StuckNotificationFactory.EXTRA_POST_REF)
            Log.d(TAG, "Main found ref $postRef")
            intent = null
            val args = PostViewFragmentArgs.Builder(postRef).build()
            findNavController(R.id.nav_host_fragment).navigate(R.id.postViewFragment, args.toBundle())
        }
    }

    private fun setupBottomSheet() {
        invisibleCover = invisible_view
        invisibleCover.setOnClickListener { hideBottomSheet() }
        bottomSheetHelper = BottomSheetHelper(this, bottom_sheet)
    }

    private fun showBottomSheet(post: PostModel) {
        if (invisibleCover != null) {
            invisibleCover.visibility = View.VISIBLE
        }
        bottomSheetHelper.showMenu(post)
    }

    private fun hideBottomSheet() {
        if (invisibleCover != null) {
            invisibleCover.visibility = View.GONE
        }
        bottomSheetHelper.hideMenu()
    }

    private fun setupBottomNavigationBar() {
        bottomNavigationView = findViewById(R.id.stuck_navigation_bar)

        val navGraphIds = listOf(
            R.navigation.nav_home,
            R.navigation.nav_categories,
            R.navigation.nav_create,
            R.navigation.nav_favorites,
            R.navigation.nav_me)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            navController.addOnDestinationChangedListener(onDestinationChangedListener)
            setupActionBarWithNavController(navController, appBarConfiguration)
        })

        currentNavController = controller
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
