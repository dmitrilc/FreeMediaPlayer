package com.dimitrilc.freemediaplayer.fragments.folderitems

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
import com.dimitrilc.freemediaplayer.adapter.MediaFolderItemAdapter
import com.dimitrilc.freemediaplayer.databinding.FolderItemsFragmentBinding
import com.dimitrilc.freemediaplayer.entities.MediaItem
import com.dimitrilc.freemediaplayer.viewmodel.FolderItemsViewModel
import com.dimitrilc.freemediaplayer.viewmodel.MediaItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

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
        val items = itemsLiveData.value!!

        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                mediaItemsViewModel.updateGlobalPlaylistAndActiveItem(items, items[position])
            }

            withContext(Dispatchers.Main){
                navigateToPlayer()
            }
        }
    }

    fun onAdapterChildThumbnailLoad(v: ImageView, artUri: String, videoId: Long?) {
        lifecycleScope.launch {
            val bitmap = async(Dispatchers.IO) {
                mediaItemsViewModel.getThumbnail(artUri, videoId)
            }

            v.setImageBitmap(bitmap.await())
        }
    }
}