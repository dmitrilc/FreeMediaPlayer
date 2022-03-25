package com.dimitrilc.freemediaplayer

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dimitrilc.freemediaplayer.databinding.ActivityMainBinding
import com.dimitrilc.freemediaplayer.fragments.folder.KEY_FULL_PATH
import com.dimitrilc.freemediaplayer.viewmodel.MainActivityViewModel
import com.dimitrilc.freemediaplayer.viewmodel.MediaItemsViewModel
import com.dimitrilc.freemediaplayer.worker.MediaScanWorker
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
    private val mediaItemsViewModel: MediaItemsViewModel by viewModels()

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
                }
                R.id.video_folders_path -> {
                    setTopAppBarTitle("Videos")
                    setBottomNavVisible()
                }
                R.id.playlists_path -> {
                    setTopAppBarTitle("Playlists")
                    setBottomNavVisible()
                }
                R.id.audio_folder_items_path -> {
                    val fullPath = arguments!!.getString(KEY_FULL_PATH)!!
                    setTopAppBarTitle(fullPath)
                    setBottomNavGone()
                }
                R.id.video_folder_items_path -> {
                    val fullPath = arguments!!.getString(KEY_FULL_PATH)!!
                    setTopAppBarTitle(fullPath)
                    setBottomNavGone()
                }
                R.id.audio_player_path -> {
                    setTopAppBarTitle("Add file Path here")
                    setBottomNavGone()
                }
                R.id.video_player_path -> {
                    setTopAppBarTitle("Add file Path here")
                    setBottomNavGone()
                    mediaController?.transportControls?.stop()
                    mediaItemsViewModel.audioBrowser.value?.disconnect()
                    mediaItemsViewModel.audioBrowser.postValue(null)
                    mediaController = null
                }
                R.id.active_playlist_path -> {
                    setTopAppBarTitle("Playlist")
                    setBottomNavGone()
                }
                else -> {
                    binding.materialToolBarViewTopAppBar.title = "N/A"
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindBottomNavToNavController()
        bindTopAppBarToNavController()

        navController.addOnDestinationChangedListener(onDestinationChangedListener)
        requestReadExternalStoragePerm()
    }

    private fun bindBottomNavToNavController(){
        binding.bottomNavViewBottomNav.setupWithNavController(navHostFragment.navController)
    }

    private fun bindTopAppBarToNavController(){
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.audio_folders_path,
                R.id.video_folders_path,
                R.id.playlists_path
            )
        )

        binding.materialToolBarViewTopAppBar.setupWithNavController(navHostFragment.navController, appBarConfiguration)
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