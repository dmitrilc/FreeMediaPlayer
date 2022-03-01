package com.example.freemediaplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.R
import com.example.freemediaplayer.adapter.FoldersFullAdapter
import com.example.freemediaplayer.databinding.FragmentFoldersFullBinding
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.pojos.AdapterFolderData
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
import kotlinx.coroutines.launch

private const val TAG = "FOLDERS_FRAGMENT"

class FoldersFragment : Fragment() {
    private var _binding: FragmentFoldersFullBinding? = null
    private val binding get() = _binding!!

    private val mediaItemsViewModel: MediaItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoldersFullBinding.inflate(inflater, container, false)
        prepareRecycler()
        return binding.root
    }

    private fun prepareRecycler(){
        mediaItemsViewModel.mediaFoldersLiveData.observe(viewLifecycleOwner){
            binding.recyclerFoldersFull.adapter = FoldersFullAdapter(it)
        }

        getFolderDataFromViewModel()
    }

    private fun getFolderDataFromViewModel() {
        val observer: Observer<List<MediaItem>> = Observer { mediaItems ->
            lifecycleScope.launch {
                mediaItems
                    .asSequence()
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
                    .toList()
                    .also {
                        mediaItemsViewModel.mediaFoldersLiveData.postValue(it)
                    }
            }
        }

        if (isAudioDest()){
            mediaItemsViewModel.allAudiosLiveData.observe(viewLifecycleOwner, observer)
        } else {
            mediaItemsViewModel.allVideosLiveData.observe(viewLifecycleOwner, observer)
        }
    }

    fun onFolderRelativeClicked(fullPathPos: Int, relativePathPos: Int){
        val folder = mediaItemsViewModel.mediaFoldersLiveData.value?.get(fullPathPos)

        folder?.let { folderData ->
            val pathParent = folderData.parentPath
            val pathRelative = folderData.relativePaths[relativePathPos]
            val fullPath = "$pathParent/$pathRelative"

            navigateToFolderItems(fullPath)
        }
    }

    private fun navigateToFolderItems(fullPath: String){
        mediaItemsViewModel.currentFolderFullPathLiveData.postValue(fullPath)

        val navController = findNavController()

        if(isAudioDest()){
            navController.navigate(R.id.action_audio_folders_path_to_audioFolderItemsFragment)
        } else {
            navController.navigate(R.id.action_video_folders_path_to_videoFolderItemsFragment)
        }
    }

    private fun isAudioDest() = findNavController().currentDestination?.id == R.id.audio_folders_path

}