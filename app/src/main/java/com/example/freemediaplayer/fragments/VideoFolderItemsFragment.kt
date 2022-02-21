package com.example.freemediaplayer.fragments

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.freemediaplayer.adapter.VideoFolderItemAdapter
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.viewmodel.VideoFolderItemsViewModel
import com.example.freemediaplayer.viewmodel.VideosViewModel
import kotlinx.coroutines.launch

private const val tag = "VIDEO_FOLDER_ITEMS"

class VideoFolderItemsFragment : FolderItemsFragment() {

    private val args: VideoFolderItemsFragmentArgs by navArgs()
    
    private val videosViewModel by activityViewModels<VideosViewModel>()
    private val videoFolderItemsViewModel by viewModels<VideoFolderItemsViewModel>()

    override fun prepareRecycler() {
        videoFolderItemsViewModel.currentFolderVideos.observe(viewLifecycleOwner){
            binding.recyclerFolderItems.adapter = VideoFolderItemAdapter(it)
        }
    }

    override fun getFolderItemsFromViewModel() {
        videosViewModel.allVideosLiveData
            .map { allAudios ->
                allAudios.filter {
                    it.location == args.fullPath
                }
            }
            .observe(viewLifecycleOwner) {
                videoFolderItemsViewModel.currentFolderVideos.postValue(it)
            }
    }

    override fun onFolderItemClicked(position: Int) {
        videoFolderItemsViewModel.currentFolderVideos.value?.let {
            videosViewModel.globalVideoPlaylist.clear()
            videosViewModel.globalVideoPlaylist.addAll(it)
            videosViewModel.activeVideo.postValue(it[position])
        }

        val navController = findNavController()

        navController.navigate(
            VideoFolderItemsFragmentDirections.actionVideoFolderItemsFragmentToAudioPlayerPath())
    }

    fun onAdapterChildThumbnailLoad(v: ImageView, video: Video) {
        lifecycleScope.launch {
            val thumbnailObserver = object : Observer<Map<String, Bitmap?>> {
                override fun onChanged(thumbnailMap: Map<String, Bitmap?>?) {
                    thumbnailMap?.get(video.album)?.let {
                        v.setImageBitmap(it)
                        videosViewModel.loadedThumbnails.removeObserver(this)
                    }
                }
            }

            videosViewModel.loadedThumbnails.observe(viewLifecycleOwner, thumbnailObserver)

            videosViewModel.loadThumbnail(video)
        }
    }

}