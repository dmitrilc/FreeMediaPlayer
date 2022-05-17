package com.dimitrilc.freemediaplayer.ui.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.ui.adapter.ActivePlaylistItemAdapter
import com.dimitrilc.freemediaplayer.databinding.FragmentActivePlaylistBinding
import com.dimitrilc.freemediaplayer.ui.viewmodel.ActivePlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "ACTIVE_PLAYLIST"

@AndroidEntryPoint
class ActivePlaylistFragment : Fragment() {

    private var _binding: FragmentActivePlaylistBinding? = null
    private val binding get() = _binding!!

    private val activePlaylistViewModel: ActivePlaylistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivePlaylistBinding.inflate(inflater, container, false)

        prepareRecycler()

        return binding.root
    }

    private fun prepareRecycler(){
        val helper = ItemTouchHelper(helperCallback)
        helper.attachToRecyclerView(binding.recyclerActivePlaylist)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activePlaylistViewModel.playlist.collect {
                    if (it.isNotEmpty() && binding.recyclerActivePlaylist.adapter == null){
                        binding.recyclerActivePlaylist.adapter = ActivePlaylistItemAdapter(activePlaylistViewModel.cache)
                    }
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

                    activePlaylistViewModel.onPlaylistItemMoved(fromPos!!, toPos!!)
                    this.fromPos = null
                }
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //Updates the latest action so onMoved will work correctly
            lastAction = ACTION_STATE_SWIPE
            val position = viewHolder.bindingAdapterPosition

            //report to viewModel that item is removed
            activePlaylistViewModel.onPlaylistItemRemoved(position)

            binding.recyclerActivePlaylist.adapter?.notifyItemRemoved(position)
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