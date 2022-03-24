package com.example.freemediaplayer.fragments.folder

import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.R

class VideoFoldersFragment : FoldersFragment() {

    override val foldersLiveData by lazy {
        foldersViewModel.videoFoldersLiveData
    }

    override fun navigateToFolderItems(){
        findNavController().navigate(R.id.action_video_folders_path_to_videoFolderItemsFragment)
    }
}