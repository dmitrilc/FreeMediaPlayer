package com.dimitrilc.freemediaplayer.fragments.folder

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R

class VideoFoldersFragment : FoldersFragment() {

    override val foldersLiveData by lazy {
        foldersViewModel.videoFoldersLiveData
    }

    override fun navigateToFolderItems(fullPath: String, navArgs: Bundle) {
        findNavController().navigate(
            R.id.action_video_folders_path_to_videoFolderItemsFragment,
            navArgs
        )
    }
}