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
import com.example.freemediaplayer.AdapterChildThumbnailLoad
import com.example.freemediaplayer.databinding.FragmentActivePlaylistBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.viewmodel.AudiosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "ACTIVE_PLAYLIST"

/**
 * A simple [Fragment] subclass.
 * Use the [ActivePlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ActivePlaylistFragment : Fragment(), AdapterChildThumbnailLoad {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentActivePlaylistBinding? = null
    private val binding get() = _binding!!

    private val audiosViewModel: AudiosViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivePlaylistBinding.inflate(inflater, container, false)

        val helperCallback = object : ItemTouchHelper.SimpleCallback(
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

                val movedItem = audiosViewModel.globalPlaylist[fromPos]

                audiosViewModel.globalPlaylist.removeAt(fromPos)
                audiosViewModel.globalPlaylist.add(toPos, movedItem)

                binding.recyclerActivePlaylist.adapter?.notifyItemMoved(fromPos, toPos)

                return true
            }

            private fun isRemovedSameAsActive(audio: Audio) = audiosViewModel.activeAudio.value == audio
            private fun isLastItemRemoved(removedPos: Int) = removedPos == audiosViewModel.globalPlaylist.size
            private fun isFirstItemRemoved(removedPos: Int) = removedPos == 0
            private fun isOnlyOneLeft() = audiosViewModel.globalPlaylist.size == 1

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition

                binding.recyclerActivePlaylist.adapter?.notifyItemRemoved(position)

                val removedItem = audiosViewModel.globalPlaylist.removeAt(position)

                if (audiosViewModel.globalPlaylist.isEmpty()){ //list is empty
                    return
                }

                if (isOnlyOneLeft()){ //only one item left.
                    val next = audiosViewModel.globalPlaylist.first()
                    audiosViewModel.activeAudio.postValue(next)
                    return
                }

                if (isLastItemRemoved(position)){ //last item in list removed
                    if (isRemovedSameAsActive(removedItem)){
                        val next = audiosViewModel.globalPlaylist.first()
                        audiosViewModel.activeAudio.postValue(next)
                    }
                    return
                }

                if (isFirstItemRemoved(position)){ //first item removed
                    if (isRemovedSameAsActive(removedItem)){
                        val next = audiosViewModel.globalPlaylist.first()
                        audiosViewModel.activeAudio.postValue(next)
                    }
                    return
                }

                if (isRemovedSameAsActive(removedItem)){
                    val next = audiosViewModel.globalPlaylist[position]
                    audiosViewModel.activeAudio.postValue(next)
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

        val helper = ItemTouchHelper(helperCallback)

        helper.attachToRecyclerView(binding.recyclerActivePlaylist)

        binding.recyclerActivePlaylist.adapter = ActivePlaylistItemAdapter(audiosViewModel.globalPlaylist)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ActivePlaylistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //TODO Remove duplicate code from FolderItemsFragment
    override fun onAdapterChildThumbnailLoad(v: ImageView, audio: Audio) {
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

    fun setActiveAudio(bindingAdapterPosition: Int){
        val next = audiosViewModel.globalPlaylist[bindingAdapterPosition]
        audiosViewModel.activeAudio.postValue(next)
        val navController = findNavController()
        navController.popBackStack()
    }

}