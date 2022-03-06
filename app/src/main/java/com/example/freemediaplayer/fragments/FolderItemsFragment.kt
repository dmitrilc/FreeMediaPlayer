package com.example.freemediaplayer.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.freemediaplayer.adapter.MediaFolderItemAdapter
import com.example.freemediaplayer.databinding.FolderItemsFragmentBinding
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.viewmodel.FolderItemsViewModel
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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
            folderItemsViewModel.updateGlobalPlayList(it)
            folderItemsViewModel.updateGlobalActiveItem(it[position])
        }

        navigateToPlayer()
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

}