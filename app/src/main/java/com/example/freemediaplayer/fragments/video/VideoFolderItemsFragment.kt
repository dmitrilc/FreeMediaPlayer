package com.example.freemediaplayer.fragments.video

import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.R
import com.example.freemediaplayer.fragments.FolderItemsFragment

class VideoFolderItemsFragment : FolderItemsFragment() {
    override val itemsLiveData by lazy {
        folderItemsViewModel.videoFolderItemsLiveData
    }

    override fun navigateToPlayer() {
        findNavController().navigate(R.id.action_videoFolderItemsFragment_to_player_path)
    }
}