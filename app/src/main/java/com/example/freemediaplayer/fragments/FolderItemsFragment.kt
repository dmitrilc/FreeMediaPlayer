package com.example.freemediaplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.example.freemediaplayer.adapter.MediaFolderItemAdapter
import com.example.freemediaplayer.databinding.FolderItemsFragmentBinding
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.viewmodel.FolderItemsViewModel
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val TAG = "FOLDER_ITEMS_FRAGMENT"

@AndroidEntryPoint
abstract class FolderItemsFragment : Fragment() {
    private var _binding: FolderItemsFragmentBinding? = null
    private val binding get() = _binding!!

    protected val folderItemsViewModel: FolderItemsViewModel by viewModels()
    private val mediaItemsViewModel by activityViewModels<MediaItemsViewModel>()

    abstract val itemsLiveData: LiveData<List<MediaItem>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FolderItemsFragmentBinding.inflate(inflater, container, false)
        prepareRecycler()
        return binding.root
    }

    private fun prepareRecycler(){
        itemsLiveData.observe(viewLifecycleOwner){
            binding.recyclerFolderItems.adapter = MediaFolderItemAdapter(it)
        }
    }

    abstract fun navigateToPlayer()

    fun onFolderItemClicked(position: Int) {
        itemsLiveData.value?.let {
            mediaItemsViewModel.updateGlobalPlaylistAndActiveItem(it, it[position])
        }

        navigateToPlayer()
    }

    fun onAdapterChildThumbnailLoad(v: ImageView, albumArtUri: String) {
        lifecycleScope.launch {
            val bitmap = async(Dispatchers.IO) {
                mediaItemsViewModel.getThumbnail(albumArtUri)
            }

            v.setImageBitmap(bitmap.await())
        }
    }
}

//TODO Remove duplicate code from FolderItemsFragment. thumbnail load.