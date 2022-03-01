package com.example.freemediaplayer.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.R
import com.example.freemediaplayer.adapter.MediaFolderItemAdapter
import com.example.freemediaplayer.databinding.FolderItemsFragmentBinding
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "FOLDER_ITEMS_FRAGMENT"

@AndroidEntryPoint
class FolderItemsFragment : Fragment() {
    private var _binding: FolderItemsFragmentBinding? = null
    private val binding get() = _binding!!

    //private val mediaFolderItemsViewModel by viewModels<MediaFolderItemsViewModel>()
    private val mediaItemsViewModel by activityViewModels<MediaItemsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FolderItemsFragmentBinding.inflate(inflater, container, false)
        prepareRecycler()
        getFolderItemsFromViewModel()
        return binding.root
    }

    private fun prepareRecycler(){
        //TODO CLean up
//        mediaFolderItemsViewModel.currentFolderMediaItems.observe(viewLifecycleOwner){
//            binding.recyclerFolderItems.adapter = MediaFolderItemAdapter(it)
//        }

        mediaItemsViewModel.globalPlaylist.observe(viewLifecycleOwner){
            binding.recyclerFolderItems.adapter = MediaFolderItemAdapter(it)
        }
    }

    private fun getFolderItemsFromViewModel() {
        mediaItemsViewModel.allMediaLiveData
            .map { mediaItems ->
                mediaItems.filter {
                    if (isAudioDest()){
                        it.isAudio && (it.location == mediaItemsViewModel.currentFolderFullPathLiveData.value)
                    } else {
                        !it.isAudio && (it.location == mediaItemsViewModel.currentFolderFullPathLiveData.value)
                    }
                }
            }
            .observe(viewLifecycleOwner) {
                mediaItemsViewModel.globalPlaylist.postValue(it.toMutableList())
            }

//        if (isAudioDest()){
//            mediaItemsViewModel.allAudiosLiveData.observe(viewLifecycleOwner, observer)
//        } else {
//            mediaItemsViewModel.allVideosLiveData.observe(viewLifecycleOwner, observer)
//        }
    }

    fun onFolderItemClicked(position: Int) {
        mediaItemsViewModel.globalPlaylist.value?.let {
            //mediaItemsViewModel.globalPlaylist.clear()
            //mediaItemsViewModel.globalPlaylist.addAll(it)
            //mediaItemsViewModel.globalPlaylist.postValue(it.toMutableList())
            mediaItemsViewModel.activeMedia.postValue(it[position])
        }

        navigateToPlayer()
    }


/*    override fun getFolderPath() = args.fullPath
    override fun getMediaItemsLiveData(): LiveData<List<MediaItem>> = audioMediaItemsViewModel.allAudiosLiveData
    override fun getMediaItemsViewModel(): MediaItemsViewModel = audioMediaItemsViewModel*/

    private fun navigateToPlayer() {
        val navController = findNavController()

        if(isAudioDest()){
            navController.navigate(R.id.action_audioFolderItemsFragment_to_player_path)
        } else {
            navController.navigate(R.id.action_videoFolderItemsFragment_to_player_path)
        }

//        navController.navigate(
//            FolderItemsFragmentDirections.actionAudioFolderItemsFragmentToPlayerPath()
//        )
    }

    fun onAdapterChildThumbnailLoad(v: ImageView, item: MediaItem) {
        lifecycleScope.launch {
            val thumbnailObserver = object : Observer<Map<String, Bitmap?>> {
                override fun onChanged(thumbnailMap: Map<String, Bitmap?>?) {
                    thumbnailMap?.get(item.album)?.let {
                        v.setImageBitmap(it)
                        mediaItemsViewModel.loadedThumbnails.removeObserver(this)
                    }
                }
            }

            mediaItemsViewModel.loadedThumbnails.observe(viewLifecycleOwner, thumbnailObserver)

            mediaItemsViewModel.loadThumbnail(item)
        }
    }

    private fun isAudioDest() = findNavController().currentDestination?.id == R.id.audio_folder_items_path

}