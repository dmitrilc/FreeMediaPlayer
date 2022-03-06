package com.example.freemediaplayer.fragments.audio

import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.R
import com.example.freemediaplayer.fragments.FoldersFragment

class AudioFoldersFragment : FoldersFragment() {

    override val foldersLiveData by lazy {
        foldersViewModel.audioFoldersLiveData
    }

    override fun navigateToFolderItems(){
        findNavController().navigate(R.id.action_audio_folders_path_to_audioFolderItemsFragment)
    }
}