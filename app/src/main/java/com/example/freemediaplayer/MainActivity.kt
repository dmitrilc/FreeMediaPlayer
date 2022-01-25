package com.example.freemediaplayer

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.freemediaplayer.databinding.ActivityMainBinding
import com.example.freemediaplayer.viewmodel.FmpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "MAIN_ACTIVITY"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: FmpViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNav()
        requestReadExternalStoragePerm()
    }

    private fun initBottomNav(){
        //Cannot use viewbinding because of bug https://issuetracker.google.com/issues/142847973
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
    }

    private fun requestReadExternalStoragePerm(){
        if (!viewModel.isReadExternalStoragePermGranted()) { //TODO Handle first launch where this is always false
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//                if (!isGranted) { //TODO Display some message to the user that the permission has not been granted
//                }
            }.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onPause() {
        val currentDestination: NavDestination? = navHostFragment.navController.currentDestination

        currentDestination?.run {
            viewModel.persistBottomNavState(currentDestination.id)
        }

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        //query saved state from ViewModel
        lifecycleScope.launch {
            val navController = navHostFragment.navController
            navController.navigate(viewModel.getBottomNavState().first())
        }
    }

}