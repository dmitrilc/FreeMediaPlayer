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
import com.example.freemediaplayer.databinding.ActivityMainBinding
import com.example.freemediaplayer.viewmodel.MainActivityViewModel
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
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

        //TODO Cannot use viewbinding because of bug https://issuetracker.google.com/issues/142847973
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
        //TODO Clean up
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.audio_folders_path,
                R.id.video_folders_path,
                R.id.playlists_path
            )
        )

        //TODO fix awkward animation of titles
        binding.materialToolBarViewTopAppBar.setupWithNavController(navHostFragment.navController, appBarConfiguration)

        //navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.addOnDestinationChangedListener { navController, dest, _ ->
            val currentFolderFullPathObserver = object : Observer<String> {
                override fun onChanged(t: String?) {
                    binding.materialToolBarViewTopAppBar.title = t
                    mediaItemsViewModel.currentFolderFullPathLiveData.removeObserver(this)
                }
            }

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
                    mediaItemsViewModel.currentFolderFullPathLiveData.observe(this, currentFolderFullPathObserver)
                    binding.bottomNavViewBottomNav.visibility = View.GONE
                }
                R.id.video_folder_items_path -> {
                    mediaItemsViewModel.currentFolderFullPathLiveData.observe(this, currentFolderFullPathObserver)
                    binding.bottomNavViewBottomNav.visibility = View.GONE
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

    private fun runQueries(){
        mediaItemsViewModel.queryAudios()
        mediaItemsViewModel.queryVideos()
    }

    private fun requestReadExternalStoragePerm(){
        if (!mainActivityViewModel.isReadExternalStoragePermGranted()) { //TODO Handle first launch where this is always false
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//                if (!isGranted) { //TODO Display some message to the user that the permission has not been granted
//                }
            }.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            runQueries()
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