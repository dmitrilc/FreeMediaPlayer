package com.dimitrilc.freemediaplayer.fragments.folder

import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R

class AudioFoldersFragment : FoldersFragment() {

    override val foldersLiveData by lazy {
        foldersViewModel.audioFoldersLiveData
    }

    override fun navigateToFolderItems(){
        findNavController().navigate(R.id.action_audio_folders_path_to_audioFolderItemsFragment)
    }
}