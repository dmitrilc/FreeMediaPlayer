package com.dimitrilc.freemediaplayer.ui.activities

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.ActivityMainBinding
import com.dimitrilc.freemediaplayer.ui.fragments.folder.KEY_FULL_PATH
import com.dimitrilc.freemediaplayer.ui.viewmodel.AppViewModel
import com.dimitrilc.freemediaplayer.ui.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "MAIN_ACTIVITY"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
                    hideOverflowMenu()
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
            appViewModel.audioBrowser.value?.disconnect()
            appViewModel.audioBrowser.postValue(null)
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