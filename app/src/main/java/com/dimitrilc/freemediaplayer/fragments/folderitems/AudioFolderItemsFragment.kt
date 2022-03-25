package com.dimitrilc.freemediaplayer.fragments.folderitems

import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R

class AudioFolderItemsFragment : FolderItemsFragment() {
    override val itemsLiveData by lazy {
        folderItemsViewModel.audioFolderItemsLiveData
    }

    override fun navigateToPlayer() {
        findNavController().navigate(R.id.action_audioFolderItemsFragment_to_player_path)
    }
}