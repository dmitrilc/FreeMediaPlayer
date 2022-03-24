package com.example.freemediaplayer.fragments.folderitems

import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.R

class VideoFolderItemsFragment : FolderItemsFragment() {
    override val itemsLiveData by lazy {
        folderItemsViewModel.videoFolderItemsLiveData
    }

    override fun navigateToPlayer() {
        findNavController().navigate(R.id.action_videoFolderItemsFragment_to_player_path)
    }
}