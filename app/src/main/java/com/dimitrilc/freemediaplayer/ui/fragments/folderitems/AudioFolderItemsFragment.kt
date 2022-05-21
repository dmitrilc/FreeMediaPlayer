package com.dimitrilc.freemediaplayer.ui.fragments.folderitems

import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R

class AudioFolderItemsFragment : FolderItemsFragment() {
    override fun navigateToPlayer() {
        findNavController().navigate(R.id.action_audioFolderItemsFragment_to_player_path)
    }

    override fun isAudio() = true
}