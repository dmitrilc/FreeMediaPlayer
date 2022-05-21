package com.dimitrilc.freemediaplayer.ui.fragments.folder.audio

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.ui.fragments.folder.FoldersFragment

private const val TAG = "AUDIO_FOLDERS"

class AudioFoldersFragment : FoldersFragment() {

    override fun navigateToFolderItems(fullPath: String, navArgs: Bundle) {
        findNavController().navigate(
            R.id.action_audio_folders_path_to_audioFolderItemsFragment,
            navArgs
        )
    }

    override fun isAudio() = true
}