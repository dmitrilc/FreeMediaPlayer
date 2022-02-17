package com.example.freemediaplayer.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.AdapterChildThumbnailLoad
import com.example.freemediaplayer.ActivePlaylistItemAdapter
import com.example.freemediaplayer.databinding.FragmentActivePlaylistBinding
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.viewmodel.FmpViewModel
import java.io.FileNotFoundException
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "PLAYLIST_FRAGMENT"

/**
 * A simple [Fragment] subclass.
 * Use the [ActivePlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActivePlaylistFragment : Fragment(), AdapterChildThumbnailLoad {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentActivePlaylistBinding? = null
    private val binding get() = _binding!!

    val viewModel: FmpViewModel by activityViewModels()

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

                val movedItem = viewModel.currentPlaylist[fromPos]

                viewModel.currentPlaylist.removeAt(fromPos)
                viewModel.currentPlaylist.add(toPos, movedItem)

                binding.recyclerActivePlaylist.adapter?.notifyItemMoved(
                    fromPos, toPos
                )

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                //TODO("Not yet implemented")
//                Log.d(TAG, "$direction")

                val position = viewHolder.bindingAdapterPosition

                val removedItem = viewModel.currentPlaylist.removeAt(position)

                if (viewModel.currentPlaylist.isEmpty()){
                    //TODO POP backstack
                    throw RuntimeException("playlist is emptied. need to pop backstack")
                } else {
                    if (viewModel.currentAudio === removedItem && position == viewModel.currentPlaylist.size){ //TODO add fun IfLastItemRemoved, etc.
                        viewModel.currentAudio = viewModel.currentPlaylist.first()
                    } else if (viewModel.currentAudio === removedItem){
                        viewModel.currentAudio = viewModel.currentPlaylist[position]
                    }
                }

                binding.recyclerActivePlaylist.adapter?.apply {
                    notifyItemRemoved(position)
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
                    ViewCompat.setElevation(viewHolder.itemView, 16f)
                }
            }

        }


        val helper = ItemTouchHelper(helperCallback)

        helper.attachToRecyclerView(binding.recyclerActivePlaylist)

        binding.recyclerActivePlaylist.adapter = ActivePlaylistItemAdapter(viewModel.currentPlaylist)

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

    override fun onAdapterChildThumbnailLoad(v: ImageView, position: Int) {
        val audio = viewModel.currentPlaylist[position]
        val thumbnailKey = audio.album
        val thumbnails = viewModel.loadedThumbnails

        //TODO Clean up
        context?.contentResolver?.let {
            if (!thumbnails.containsKey(thumbnailKey)) {
                if (isSameOrAfterQ()) { //TODO check if thumbnail exists before querying
                    try {
                        val thumbnail = it.loadThumbnail(
                            audio.uri,
                            Size(300, 300),
                            null
                        )

                        viewModel.loadedThumbnails[thumbnailKey] = thumbnail

                        Log.d(TAG, "$thumbnailKey = ${viewModel.loadedThumbnails[thumbnailKey]}")
                    }
                    catch (e: FileNotFoundException) {
                        //TODO Implement
                        Log.d(TAG, e.toString())
                    }
                }
            }

            if (thumbnails[thumbnailKey] !== null){
                v.setImageBitmap(thumbnails[thumbnailKey])
            }
        }
    }
}

//TODO Clean up
//TODO add visual indications when moving/removing