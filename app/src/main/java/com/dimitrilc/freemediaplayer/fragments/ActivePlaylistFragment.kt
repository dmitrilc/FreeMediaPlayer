package com.dimitrilc.freemediaplayer.fragments

import android.graphics.Canvas
import android.media.session.MediaController
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.adapter.ActivePlaylistItemAdapter
import com.dimitrilc.freemediaplayer.databinding.FragmentActivePlaylistBinding
import com.dimitrilc.freemediaplayer.entities.MediaItem
import com.dimitrilc.freemediaplayer.viewmodel.MediaItemsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ACTIVE_PLAYLIST"

class ActivePlaylistFragment : Fragment() {

    private var _binding: FragmentActivePlaylistBinding? = null
    private val binding get() = _binding!!

    private val mediaItemsViewModel: MediaItemsViewModel by activityViewModels()

    private val mPlaylistCache = mutableListOf<MediaItem>()
    private val mActiveMediaCache = MutableLiveData<MediaItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivePlaylistBinding.inflate(inflater, container, false)

        val helper = ItemTouchHelper(helperCallback)

        helper.attachToRecyclerView(binding.recyclerActivePlaylist)

        lifecycleScope.launch {
            withContext(lifecycleScope.coroutineContext){
                mPlaylistCache.addAll(mediaItemsViewModel.getPlaylistOnce())
            }
            withContext(lifecycleScope.coroutineContext){
                binding.recyclerActivePlaylist.adapter = ActivePlaylistItemAdapter(mPlaylistCache)
            }
        }

        //Functions in this class should not use this value. This is only to update the cache.
        mediaItemsViewModel.activeMediaLiveData.observe(viewLifecycleOwner){
            mActiveMediaCache.value = it
        }

        mActiveMediaCache.observe(viewLifecycleOwner){

        }

        return binding.root
    }

    fun onAdapterChildThumbnailLoad(v: ImageView, artUri: String, videoId: Long?) {
        lifecycleScope.launch {
            val bitmap = async(Dispatchers.IO) {
                mediaItemsViewModel.getThumbnail(artUri, videoId)
            }

            v.setImageBitmap(bitmap.await())
        }
    }

    //TOOD Clean up
    fun setActiveMedia(bindingAdapterPosition: Int){
        val controller: MediaController? = requireActivity().mediaController

        if (controller != null){ //audio
            controller.transportControls.skipToQueueItem(bindingAdapterPosition.toLong())
            mediaItemsViewModel.updateGlobalPlaylistAndActiveItem(mPlaylistCache, mPlaylistCache[bindingAdapterPosition])
        } else { //video
            mediaItemsViewModel.updateGlobalPlaylistAndActiveItem(mPlaylistCache, mPlaylistCache[bindingAdapterPosition])
            val navController = findNavController()
            navController.popBackStack()
        }
    }

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
            val movedItem = mPlaylistCache[fromPos]

            mPlaylistCache.removeAt(fromPos)
            mPlaylistCache.add(toPos, movedItem)

/*            if (movedItem != null) {
                mPlaylist.add(toPos, movedItem)
            }*/

            mediaItemsViewModel.updateGlobalPlaylistAndActiveItem(mPlaylistCache, movedItem)

            binding.recyclerActivePlaylist.adapter!!.notifyItemMoved(fromPos, toPos)

            return true
        }

        private fun isActiveRemoved(audio: MediaItem) = mActiveMediaCache.value == audio
        private fun isEndItemRemoved(removedPos: Int) = (removedPos - 1) == mPlaylistCache.lastIndex

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            binding.recyclerActivePlaylist.adapter!!.notifyItemRemoved(position)
            val removedItem = mPlaylistCache.removeAt(position)

            if (mPlaylistCache.isEmpty()){ //list is empty
                return
            }

            if (isActiveRemoved(removedItem)){
                val nextItemIndex =
                    if (isEndItemRemoved(position)){
                        0
                    } else {
                        position
                    }
                activity
                    ?.mediaController
                    ?.transportControls
                    ?.skipToQueueItem(nextItemIndex.toLong())
                mediaItemsViewModel.updateGlobalPlaylistAndActiveItem(mPlaylistCache, mPlaylistCache[nextItemIndex])
            } else {
                mediaItemsViewModel.updateGlobalPlaylistAndActiveItem(mPlaylistCache, mActiveMediaCache.value!!)
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

}