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
import com.example.freemediaplayer.adapter.AudioFolderItemAdapter
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.viewmodel.AudioFolderItemsViewModel
import com.example.freemediaplayer.viewmodel.AudiosViewModel
import kotlinx.coroutines.launch

private const val tag = "AUDIO_FOLDER_ITEMS"

class AudioFolderItemsFragment : FolderItemsFragment() {

    private val args: AudioFolderItemsFragmentArgs by navArgs()

    private val audiosViewModel by activityViewModels<AudiosViewModel>()
    private val audioFolderItemsViewModel by viewModels<AudioFolderItemsViewModel>()

    override fun prepareRecycler(){
        //TODO CLean up
        audioFolderItemsViewModel.currentFolderAudios.observe(viewLifecycleOwner){
            binding.recyclerFolderItems.adapter = AudioFolderItemAdapter(it)
        }
    }

    override fun getFolderItemsFromViewModel() {
        audiosViewModel.allAudiosLiveData
            .map { allAudios ->
                allAudios.filter {
                    it.location == args.fullPath
                }
            }
            .observe(viewLifecycleOwner) {
                audioFolderItemsViewModel.currentFolderAudios.postValue(it)
            }
    }

    override fun onFolderItemClicked(position: Int) {
        audioFolderItemsViewModel.currentFolderAudios.value?.let {
            audiosViewModel.globalPlaylist.clear()
            audiosViewModel.globalPlaylist.addAll(it)
            audiosViewModel.activeAudio.postValue(it[position])
        }

        val navController = findNavController()

        navController.navigate(
            AudioFolderItemsFragmentDirections.actionAudioFolderItemsFragmentToAudioPlayerPath())
    }

    fun onAdapterChildThumbnailLoad(v: ImageView, audio: Audio) {
        lifecycleScope.launch {
            val thumbnailObserver = object : Observer<Map<String, Bitmap?>> {
                override fun onChanged(thumbnailMap: Map<String, Bitmap?>?) {
                    thumbnailMap?.get(audio.album)?.let {
                        v.setImageBitmap(it)
                        audiosViewModel.loadedThumbnails.removeObserver(this)
                    }
                }
            }

            audiosViewModel.loadedThumbnails.observe(viewLifecycleOwner, thumbnailObserver)

            audiosViewModel.loadThumbnail(audio)
        }
    }

}