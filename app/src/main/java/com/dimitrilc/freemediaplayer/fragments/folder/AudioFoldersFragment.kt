package com.dimitrilc.freemediaplayer.fragments.folder

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R

class AudioFoldersFragment : FoldersFragment() {

    override val foldersLiveData by lazy {
        foldersViewModel.audioFoldersLiveData
    }

    override fun navigateToFolderItems(fullPath: String, navArgs: Bundle) {
        findNavController().navigate(
            R.id.action_audio_folders_path_to_audioFolderItemsFragment,
            navArgs
        )
    }
}