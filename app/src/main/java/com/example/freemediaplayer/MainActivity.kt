package com.example.freemediaplayer

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.freemediaplayer.databinding.ActivityMainBinding
import com.example.freemediaplayer.viewmodel.MainActivityViewModel
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
import com.example.freemediaplayer.worker.MediaScanWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "MAIN_ACTIVITY"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val mediaItemsViewModel: MediaItemsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Cannot use viewbinding because of bug https://issuetracker.google.com/issues/142847973
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView_navHostFragment) as NavHostFragment

        bindNavElementsToNav()
        initBottomNav()
        requestReadExternalStoragePerm()

        contentResolver.getType(Uri.parse(""))
    }

    private fun initBottomNav(){
        binding.bottomNavViewBottomNav.setupWithNavController(navHostFragment.navController)
    }

    private fun bindNavElementsToNav(){
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.audio_folders_path,
                R.id.video_folders_path,
                R.id.playlists_path
            )
        )

        binding.materialToolBarViewTopAppBar.setupWithNavController(navHostFragment.navController, appBarConfiguration)

        //navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.addOnDestinationChangedListener { navController, dest, _ ->
            when(dest.id){
                R.id.audio_folders_path -> {
                    binding.materialToolBarViewTopAppBar.title = "Audios"
                    binding.bottomNavViewBottomNav.visibility = View.VISIBLE
                }
                R.id.video_folders_path -> {
                    binding.materialToolBarViewTopAppBar.title = "Videos"
                    binding.bottomNavViewBottomNav.visibility = View.VISIBLE
                }
                R.id.playlists_path -> {
                    binding.materialToolBarViewTopAppBar.title = "Playlists"
                    binding.bottomNavViewBottomNav.visibility = View.VISIBLE
                }
                R.id.audio_folder_items_path -> {
                    binding.bottomNavViewBottomNav.visibility = View.GONE
                    lifecycleScope.launch {
                        binding.materialToolBarViewTopAppBar.title = mediaItemsViewModel.getCurrentFolderFullPath().fullPath
                    }
                }
                R.id.video_folder_items_path -> {
                    binding.bottomNavViewBottomNav.visibility = View.GONE
                    lifecycleScope.launch {
                        binding.materialToolBarViewTopAppBar.title = mediaItemsViewModel.getCurrentFolderFullPath().fullPath
                    }
                }
                R.id.audio_player_path -> {
                    binding.materialToolBarViewTopAppBar.title = "Add file Path here"
                    binding.bottomNavViewBottomNav.visibility = View.GONE
                }
                R.id.video_player_path -> {
                    binding.materialToolBarViewTopAppBar.title = "Add file Path here"
                    binding.bottomNavViewBottomNav.visibility = View.GONE
                    mediaController?.transportControls?.stop()
                    mediaItemsViewModel.audioBrowser.value?.disconnect()
                    mediaItemsViewModel.audioBrowser.postValue(null)
                    mediaController = null
                }
                R.id.active_playlist_path -> {
                    binding.materialToolBarViewTopAppBar.title = "Playlist"
                    binding.bottomNavViewBottomNav.visibility = View.GONE
                }
                else -> {
                    binding.materialToolBarViewTopAppBar.title = "N/A"
                    binding.bottomNavViewBottomNav.visibility = View.GONE
                }
            }
        }
    }

    private fun activateMediaScanWorker(){
        val mediaScanWorkRequest = OneTimeWorkRequestBuilder<MediaScanWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(mediaScanWorkRequest)
    }

    private fun requestReadExternalStoragePerm(){
        if (!mainActivityViewModel.isReadExternalStoragePermGranted()) {
            val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    activateMediaScanWorker()
                } else {
                }
            }

            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            activateMediaScanWorker()
        }
    }

    override fun onPause() {
        val currentDestination: NavDestination? = navHostFragment.navController.currentDestination

        currentDestination?.run {
            mainActivityViewModel.persistBottomNavState(currentDestination.id)
        }

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        //query saved state from ViewModel
        lifecycleScope.launch {
            val navController = navHostFragment.navController
            navController.popBackStack()
            navController.navigate(mainActivityViewModel.getBottomNavState().first())
        }
    }

}