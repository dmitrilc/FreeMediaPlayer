package com.example.freemediaplayer.fragments

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.pojos.AdapterFolderData
import com.example.freemediaplayer.viewmodel.AudiosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "AUDIO_FOLDERS_FRAGMENT"

@AndroidEntryPoint
class AudioFoldersFragment : FoldersFullFragment() {

    private val audiosViewModel: AudiosViewModel by activityViewModels()

    override fun getFolderDataFromViewModel() {
        audiosViewModel.allAudiosLiveData.observe(viewLifecycleOwner) { allAudios ->
            lifecycleScope.launch {
                val folderDataList = allAudios
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

        navController.navigate(
                AudioFoldersFragmentDirections.actionAudioFoldersPathToAudioFolderItemsFragment(
                    fullPath
                )
        )
    }

}