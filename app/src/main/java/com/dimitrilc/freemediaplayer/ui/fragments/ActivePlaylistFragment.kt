package com.dimitrilc.freemediaplayer.ui.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.ui.adapter.ActivePlaylistItemAdapter
import com.dimitrilc.freemediaplayer.databinding.FragmentActivePlaylistBinding
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.ui.state.folders.items.FolderItemsUiState
import com.dimitrilc.freemediaplayer.ui.viewmodel.ActivePlaylistViewModel
import com.dimitrilc.freemediaplayer.ui.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "ACTIVE_PLAYLIST"

@AndroidEntryPoint
class ActivePlaylistFragment : Fragment() {

    private var _binding: FragmentActivePlaylistBinding? = null
    private val binding get() = _binding!!

    private val activePlaylistViewModel: ActivePlaylistViewModel by viewModels()
    private val appViewModel: AppViewModel by activityViewModels()

    private val mPlaylistCache = mutableListOf<MediaItem>()
    private val mActiveMediaCache = MutableLiveData<MediaItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivePlaylistBinding.inflate(inflater, container, false)

        val helper = ItemTouchHelper(helperCallback)
        helper.attachToRecyclerView(binding.recyclerActivePlaylist)

/*        lifecycleScope.launch {
            withContext(lifecycleScope.coroutineContext){
                mPlaylistCache.addAll(appViewModel.getMediaItemsInGlobalPlaylistOnce()!!)
            }
            withContext(lifecycleScope.coroutineContext){
                binding.recyclerActivePlaylist.adapter = ActivePlaylistItemAdapter(mPlaylistCache)
            }
        }*/

        //Functions in this class should not use this value. This is only to update the cache.
/*        activePlaylistViewModel.activeMediaItemLiveData.observe(viewLifecycleOwner){
            mActiveMediaCache.value = it
        }*/

        //mActiveMediaCache.observe(viewLifecycleOwner){
            //activity?.mediaController?.sendCommand(PLAY_SELECTED, null, null)
        //}

        prepareRecycler()

        return binding.root
    }

    private val cache = MutableLiveData<List<FolderItemsUiState>>()

    private val activePlaylistItemAdapter: ActivePlaylistItemAdapter? = null

    private fun prepareRecycler(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                activePlaylistViewModel.uiState.collectLatest {
                    //Only on first emission
                    if (activePlaylistItemAdapter == null){
                        val activePlaylistItemAdapter = ActivePlaylistItemAdapter(it)
                        binding.recyclerActivePlaylist.adapter = activePlaylistItemAdapter
                    }

                    activePlaylistItemAdapter?.submitList(it)
                }
            }
       }
    }

    private val helperCallback = object : ItemTouchHelper.SimpleCallback(
        UP or DOWN,
        START or END
    ) {
        private var lastAction = ACTION_STATE_IDLE
        private var fromPos: Int? = null
        private var toPos: Int? = null

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onMoved(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            fromPos: Int,
            target: RecyclerView.ViewHolder,
            toPos: Int,
            x: Int,
            y: Int
        ) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
            binding.recyclerActivePlaylist.adapter?.notifyItemMoved(fromPos, toPos)

            if (this.fromPos == null){
                this.fromPos = fromPos
            }

            this.toPos = toPos
            lastAction = ACTION_STATE_DRAG
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ACTION_STATE_IDLE){
                if (lastAction == ACTION_STATE_DRAG
                    && fromPos != null
                    && toPos != null){

                    activePlaylistViewModel.moveGlobalPlaylistItemsPositions(fromPos!!, toPos!!)
                    this.fromPos = null
                }
            }
        }

        private fun isActiveRemoved(audio: MediaItem) = mActiveMediaCache.value == audio
        private fun isEndItemRemoved(removedPos: Int) = (removedPos - 1) == mPlaylistCache.lastIndex

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            lastAction = ACTION_STATE_SWIPE
/*            val position = viewHolder.bindingAdapterPosition
            binding.recyclerActivePlaylist.adapter!!.notifyItemRemoved(position)
            val removedItem = mPlaylistCache.removeAt(position)

            if (mPlaylistCache.isEmpty()){ //list is empty
                return
            }

            if (isActiveRemoved(removedItem)){
                val nextItemIndex = if (isEndItemRemoved(position)){
                    0
                } else {
                    position
                }

                setActiveMedia(nextItemIndex)
            } else {
                activePlaylistViewModel.removeGlobalPlaylistItem(
                    GlobalPlaylistItem(
                        position.toLong(),
                        removedItem.mediaItemId
                    )
                )
            }*/
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

            //TOOD Check why elevation is not working
            if (isCurrentlyActive){
                ViewCompat.setElevation(viewHolder.itemView, 32f)
            }
        }

    }

}

//TODO Consider reusing folder items layout or adapter or code.