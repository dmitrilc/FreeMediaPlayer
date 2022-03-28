package com.dimitrilc.freemediaplayer.ui.fragments.folder.video

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.ui.fragments.folder.FoldersFragment
import com.dimitrilc.freemediaplayer.ui.fragments.folder.KEY_FULL_PATH
import com.dimitrilc.freemediaplayer.ui.fragments.folder.audio.AudioFoldersFragmentDirections

class VideoFoldersFragment : FoldersFragment() {

    override fun navigateToFolderItems(fullPath: String, navArgs: Bundle) {
        findNavController().navigate(
            R.id.action_video_folders_path_to_videoFolderItemsFragment,
            navArgs
        )
    }

    override fun isAudio() = false

}