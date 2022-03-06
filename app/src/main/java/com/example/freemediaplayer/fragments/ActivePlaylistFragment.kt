package com.example.freemediaplayer.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.adapter.ActivePlaylistItemAdapter
import com.example.freemediaplayer.databinding.FragmentActivePlaylistBinding
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
import kotlinx.coroutines.launch

private const val TAG = "ACTIVE_PLAYLIST"

class ActivePlaylistFragment : Fragment() {

    private var _binding: FragmentActivePlaylistBinding? = null
    private val binding get() = _binding!!

    private val mediaItemsViewModel: MediaItemsViewModel by activityViewModels()

    private val helperCallback = object : ItemTouchHelper.SimpleCallback(
        UP or DOWN,
        START or END
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.bindingAdapterPosition
            val toPos = target.bindingAdapterPosition

            val movedItem = mediaItemsViewModel.globalPlaylist.value?.get(fromPos)

            mediaItemsViewModel.globalPlaylist.value?.removeAt(fromPos)

            if (movedItem != null) {
                mediaItemsViewModel.globalPlaylist.value?.add(toPos, movedItem)
            }

            binding.recyclerActivePlaylist.adapter?.notifyItemMoved(fromPos, toPos)

            return true
        }

        private fun isRemovedSameAsActive(audio: MediaItem) = mediaItemsViewModel.activeMedia.value == audio
        private fun isLastItemRemoved(removedPos: Int) = removedPos == mediaItemsViewModel.globalPlaylist.value?.size
        private fun isFirstItemRemoved(removedPos: Int) = removedPos == 0
        private fun isOnlyOneLeft() = mediaItemsViewModel.globalPlaylist.value?.size == 1

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition

            binding.recyclerActivePlaylist.adapter?.notifyItemRemoved(position)

            val removedItem = mediaItemsViewModel.globalPlaylist.value?.removeAt(position)

            if (mediaItemsViewModel.globalPlaylist.value?.isEmpty() == true){ //list is empty
                return
            }

            if (isOnlyOneLeft()){ //only one item left.
                val next = mediaItemsViewModel.globalPlaylist.value?.first()
                mediaItemsViewModel.activeMedia.postValue(next)
                return
            }

            if (isLastItemRemoved(position)){ //last item in list removed
                if (removedItem?.let { isRemovedSameAsActive(it) } == true){
                    val next = mediaItemsViewModel.globalPlaylist.value?.first()
                    mediaItemsViewModel.activeMedia.postValue(next)
                }
                return
            }

            if (isFirstItemRemoved(position)){ //first item removed
                if (removedItem?.let { isRemovedSameAsActive(it) } == true){
                    val next = mediaItemsViewModel.globalPlaylist.value?.first()
                    mediaItemsViewModel.activeMedia.postValue(next)
                }
                return
            }

            if (removedItem?.let { isRemovedSameAsActive(it) } == true){
                val next = mediaItemsViewModel.globalPlaylist.value?.get(position)
                mediaItemsViewModel.activeMedia.postValue(next)
                return
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )

            if (isCurrentlyActive){
                ViewCompat.setElevation(viewHolder.itemView, 32f)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivePlaylistBinding.inflate(inflater, container, false)

        val helper = ItemTouchHelper(helperCallback)

        helper.attachToRecyclerView(binding.recyclerActivePlaylist)

        binding.recyclerActivePlaylist.adapter = mediaItemsViewModel.globalPlaylist.value?.let {
            ActivePlaylistItemAdapter(
                it
            )
        }

        return binding.root
    }

    //TODO Remove duplicate code from FolderItemsFragment
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

    fun setActiveMedia(bindingAdapterPosition: Int){
        val next = mediaItemsViewModel.globalPlaylist.value?.get(bindingAdapterPosition)
        mediaItemsViewModel.activeMedia.postValue(next)
        val navController = findNavController()
        navController.popBackStack()
    }

}