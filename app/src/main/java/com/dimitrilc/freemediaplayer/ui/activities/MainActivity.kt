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
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.ActivityMainBinding
import com.dimitrilc.freemediaplayer.ui.fragments.folder.KEY_FULL_PATH
import com.dimitrilc.freemediaplayer.ui.fragments.folder.audio.AudioFoldersFragmentDirections
import com.dimitrilc.freemediaplayer.ui.fragments.settings.SettingsFragmentDirections
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
                    showSettingsGear()
                }
                R.id.video_folders_path -> {
                    setTopAppBarTitle("Videos")
                    setBottomNavVisible()
                    showSettingsGear()
                }
                R.id.playlists_path -> {
                    setTopAppBarTitle("Playlists")
                    setBottomNavVisible()
                    showSettingsGear()
                }
                R.id.audio_folder_items_path -> {
                    val fullPath = arguments!!.getString(KEY_FULL_PATH)!!
                    setTopAppBarTitle(fullPath)
                    setBottomNavGone()
                    hideSettingsGear()
                }
                R.id.video_folder_items_path -> {
                    val fullPath = arguments!!.getString(KEY_FULL_PATH)!!
                    setTopAppBarTitle(fullPath)
                    setBottomNavGone()
                    hideSettingsGear()
                }
                R.id.audio_player_path -> {
                    setTopAppBarTitle("Add file Path here")
                    setBottomNavGone()
                    hideSettingsGear()
                }
                R.id.video_player_path -> {
                    setTopAppBarTitle("Add file Path here")
                    setBottomNavGone()
                    closeAudioSession()
                    hideSettingsGear()
                }
                R.id.active_playlist_path -> {
                    setTopAppBarTitle("Playlist")
                    setBottomNavGone()
                    showSettingsGear()
                }
                R.id.settings_path -> {
                    hideSettingsGear()
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
        fun hideSettingsGear(){
            binding.materialToolBarViewTopAppBar.menu.findItem(R.id.settings).isVisible = false
        }

        fun showSettingsGear(){
            binding.materialToolBarViewTopAppBar.menu.findItem(R.id.settings).isVisible = true
        }
    }

    private val preferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == preferencesKeyIsDark){
            saveBottomNavState()
            checkIsDarkAndSetTheme()
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

        restoreBottomNavState()

        prepareTopAppBar()

        bindBottomNavToNavController()

        bindTopAppBarToNavController()

        //Add onDestinationChangedListener to navController
        navController.addOnDestinationChangedListener(onDestinationChangedListener)

        requestReadExternalStoragePerm()
    }

    private fun restoreBottomNavState(){
        lifecycleScope.launch {
            val destId = mainActivityViewModel.getBottomNavState().first()
            when(destId){
                R.id.audio_folders_path -> navController.navigate(R.id.action_global_audio_folders_path)
                R.id.video_folders_path -> navController.navigate(R.id.action_global_video_folders_path)
                R.id.playlists_path -> navController.navigate(R.id.action_global_playlists_path)
            }
        }
    }

    private fun prepareTopAppBar(){
        binding.materialToolBarViewTopAppBar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.settings){
                navController.navigate(R.id.settings_path)
                true
            } else {
                false
            }
        }

        listenToSharedPreferencesChange()
    }

    private fun listenToSharedPreferencesChange(){
        //val sharedPrefManager = PreferenceManager.getDefaultSharedPreferences(this)
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
            val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    mainActivityViewModel.scanForMediaFiles()
                } else {

                }
            }

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

    override fun onPause() {
        //mainActivityViewModel.persistBottomNavState(currentDestination.id)
        super.onPause()
    }

/*    override fun onSaveInstanceState(outState: Bundle) {
        //val state = navController.saveState()
        //outState.putBundle("NAV_STATE", state)
        Log.d(TAG, "Saving Nav Bundle")
        super.onSaveInstanceState(outState)
    }*/

    override fun onResume() {
        super.onResume()

        //query saved state from ViewModel
/*        lifecycleScope.launch {
            navController.popBackStack()
            navController.navigate(mainActivityViewModel.getBottomNavState().first())
        }*/
    }

    private fun saveBottomNavState(){
        val currentDestination = navController.currentDestination
        if (currentDestination != null){
            mainActivityViewModel.persistBottomNavState(currentDestination.id)
        }
    }

    override fun onStop() {
        saveBottomNavState()
        super.onStop()
    }

    override fun onRestart() {
        unregisterSharedPreferencesChangeListener()
        super.onRestart()
    }

    override fun onDestroy() {
        unregisterSharedPreferencesChangeListener()
        super.onDestroy()
    }

}