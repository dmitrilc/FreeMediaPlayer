package com.dimitrilc.freemediaplayer.ui.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.ActivityMainBinding
import com.dimitrilc.freemediaplayer.hilt.FmpApplication
import com.dimitrilc.freemediaplayer.service.AudioPlayerService
import com.dimitrilc.freemediaplayer.ui.viewmodel.AppViewModel
import com.dimitrilc.freemediaplayer.ui.viewmodel.KEY_FULL_PATH
import com.dimitrilc.freemediaplayer.ui.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MAIN_ACTIVITY"

const val AUDIO_CONTROLS_NOTIFICATION_CHANNEL_ID = "AUDIO_CONTROLS_NOTIFICATION_CHANNEL_ID"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var fmpApp: FmpApplication

    //Can only access during onCreate().
    //Cannot use viewbinding because of bug https://issuetracker.google.com/issues/142847973
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentContainerView_navHostFragment) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val appViewModel: AppViewModel by viewModels()

    private val sharedPrefManager by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private val preferencesKeyIsDark by lazy {
        getString(R.string.preferences_key_is_dark)
    }

    private val onDestinationChangedListener = object : NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            when(destination.id){
                R.id.audio_folders_path -> {
                    setTopAppBarTitle("Audios")
                    setBottomNavVisible()
                    showOverflowMenu()
                }
                R.id.video_folders_path -> {
                    setTopAppBarTitle("Videos")
                    setBottomNavVisible()
                    showOverflowMenu()
                }
                R.id.playlists_path -> {
                    setTopAppBarTitle("Playlists")
                    setBottomNavVisible()
                    showOverflowMenu()
                }
                R.id.audio_folder_items_path -> {
                    val fullPath = arguments!!.getString(KEY_FULL_PATH)!!
                    setTopAppBarTitle(fullPath)
                    setBottomNavGone()
                    hideOverflowMenu()
                }
                R.id.video_folder_items_path -> {
                    val fullPath = arguments!!.getString(KEY_FULL_PATH)!!
                    setTopAppBarTitle(fullPath)
                    setBottomNavGone()
                    hideOverflowMenu()
                    endImmersiveMode()
                }
                R.id.audio_player_path -> {
                    setTopAppBarTitle("Add file Path here")
                    setBottomNavGone()
                    hideOverflowMenu()
                }
                R.id.video_player_path -> {
                    setTopAppBarTitle("Add file Path here")
                    setBottomNavGone()
                    closeAudioSession()
                    //hideOverflowMenu()
                    //endImmersiveMode()
                    startImmersiveMode()
                    stopAudioService()
                }
                R.id.active_playlist_path -> {
                    setTopAppBarTitle("Playlist")
                    setBottomNavGone()
                    hideOverflowMenu()
                }
                R.id.settings_path -> {
                    hideOverflowMenu()
                    setBottomNavGone()
                }
                else -> {
                    setTopAppBarTitle("N/A")
                    setBottomNavGone()
                }
            }
        }

        fun setBottomNavVisible(){
            binding.bottomNavViewBottomNav.visibility = View.VISIBLE
        }

        fun setBottomNavGone(){
            binding.bottomNavViewBottomNav.visibility = View.GONE
        }

        fun setTopAppBarTitle(value: String){
            binding.materialToolBarViewTopAppBar.title = value
        }

        fun closeAudioSession(){
            mediaController?.transportControls?.stop()
            appViewModel.audioBrowser?.disconnect()
            appViewModel.audioBrowser = null
            mediaController = null
        }

        fun hideOverflowMenu(){
            binding.materialToolBarViewTopAppBar.menu.findItem(R.id.settings).isVisible = false
            binding.materialToolBarViewTopAppBar.menu.findItem(R.id.rescan).isVisible = false
        }

        fun showOverflowMenu(){
            binding.materialToolBarViewTopAppBar.menu.findItem(R.id.settings).isVisible = true
            binding.materialToolBarViewTopAppBar.menu.findItem(R.id.rescan).isVisible = true
        }

        fun startImmersiveMode(){
            hideTopAppBar()

            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // Hide both the status bar and the navigation bar
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        fun endImmersiveMode(){
            showTopAppBar()
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // Hide both the status bar and the navigation bar
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }

        fun hideTopAppBar(){
            binding.materialToolBarViewTopAppBar.visibility = View.GONE
        }

        fun showTopAppBar(){
            binding.materialToolBarViewTopAppBar.visibility = View.VISIBLE
        }

        fun stopAudioService(){
            fmpApp.audioBrowser?.disconnect()
            fmpApp.audioBrowser = null
            val stopIntent = Intent(applicationContext, AudioPlayerService::class.java)
            stopService(stopIntent)
        }
    }

    private val preferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == preferencesKeyIsDark){
            saveBottomNavState()
            checkIsDarkAndSetTheme()
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            mainActivityViewModel.scanForMediaFiles()
        } else {

        }
    }

    private fun checkIsDarkAndSetTheme(){
        val isDark = sharedPrefManager.getBoolean(preferencesKeyIsDark, false)
        if (isDark){
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        checkIsDarkAndSetTheme()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restoreNavState()

        prepareTopAppBar()

        bindBottomNavToNavController()

        bindTopAppBarToNavController()

        //Add onDestinationChangedListener to navController
        navController.addOnDestinationChangedListener(onDestinationChangedListener)

        requestReadExternalStoragePerm()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Audio Player Controls"
            val descriptionText = "Audio Player Controls"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(
                AUDIO_CONTROLS_NOTIFICATION_CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun restoreNavState(){
        lifecycleScope.launch {
            when(mainActivityViewModel.getBottomNavState().first()){
                R.id.video_folders_path -> {
                    navController.navigate(R.id.action_global_video_folders_path)
                    navController.graph.setStartDestination(R.id.video_folders_path)
                }
                R.id.playlists_path -> {
                    navController.graph.setStartDestination(R.id.video_folders_path)
                    navController.navigate(R.id.playlists_path)
                    navController.popBackStack()
                }
                else -> restoreNavGraphState()
            }
        }
    }

    private fun restoreNavGraphState(){
        val state = mainActivityViewModel.getSavedNavState()
        state?.let {
            navController.restoreState(it)
        }
    }

    private fun prepareTopAppBar(){
        binding.materialToolBarViewTopAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.settings -> {
                    navController.navigate(R.id.settings_path)
                    true
                }
                R.id.rescan -> {
                    requestReadExternalStoragePerm()
                    true
                }
                else -> false
            }
        }

        listenToSharedPreferencesChange()
    }

    private fun listenToSharedPreferencesChange(){
        sharedPrefManager.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    private fun unregisterSharedPreferencesChangeListener(){
        sharedPrefManager.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    private fun bindBottomNavToNavController(){
        binding.bottomNavViewBottomNav.setupWithNavController(navController)
    }

    private fun bindTopAppBarToNavController(){
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.audio_folders_path,
                R.id.video_folders_path,
                R.id.playlists_path
            )
        )

        binding.materialToolBarViewTopAppBar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun requestReadExternalStoragePerm(){
        if (!isReadExternalStoragePermGranted()) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            mainActivityViewModel.scanForMediaFiles()
        }
    }

    private fun isReadExternalStoragePermGranted(): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return isGranted == PermissionChecker.PERMISSION_GRANTED
    }

    private fun saveBottomNavState(){
        val currentDestination = navController.currentDestination
        if (currentDestination != null){
            mainActivityViewModel.persistBottomNavState(currentDestination.id)
        }
    }

    private fun saveNavGraphState(){
        val navState = navController.saveState()
        mainActivityViewModel.saveNavState(navState)
    }

    override fun onStop() {
        saveNavGraphState()
        saveBottomNavState()
        super.onStop()
    }

    override fun onPause() {
        unregisterSharedPreferencesChangeListener()
        super.onPause()
    }

    override fun onResume() {
        listenToSharedPreferencesChange()
        super.onResume()
    }

}