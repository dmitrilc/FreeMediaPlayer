package com.example.freemediaplayer.fragments

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.pojos.AdapterFolderData
import com.example.freemediaplayer.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "VIDEO_FOLDERS_FRAGMENT"

@AndroidEntryPoint
class VideoFoldersFragment : FoldersFullFragment() {

    private val videosViewModel: VideosViewModel by activityViewModels()

    override fun getFolderDataFromViewModel() {
        videosViewModel.allVideosLiveData.observe(viewLifecycleOwner) { allVideos ->
            for (video in allVideos){
                Log.d(TAG, video.toString())
            }

            lifecycleScope.launch {
                val folderDataList = allVideos
                    .distinctBy { it.location }
                    .map { it.location }
                    .groupBy({ it.substringBeforeLast('/') }) {
                        it.substringAfterLast('/')
                    }
                    .map {
                        AdapterFolderData(
                            parentPath = it.key,
                            relativePaths = it.value
                        )
                    }

                allFoldersViewModel.allFoldersLiveData.postValue(folderDataList)
            }
        }
    }

    override fun navigateToFolderItems(fullPath: String){
        val navController = findNavController()

        navController
            .navigate(
                VideoFoldersFragmentDirections.actionVideoFoldersPathToVideoFolderItemsFragment(fullPath))
    }

}