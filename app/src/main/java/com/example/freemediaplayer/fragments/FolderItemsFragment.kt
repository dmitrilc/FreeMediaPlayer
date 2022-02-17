package com.example.freemediaplayer.fragments

import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.AdapterChildClickedListener
import com.example.freemediaplayer.AdapterChildThumbnailLoad
import com.example.freemediaplayer.FileAdapter
import com.example.freemediaplayer.databinding.FolderItemsFragmentBinding
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.viewmodel.FmpViewModel
import java.io.FileNotFoundException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "FILES_FRAGMENT"

/**
 * A simple [Fragment] subclass.
 * Use the [FolderItemsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FolderItemsFragment : Fragment(), AdapterChildClickedListener, AdapterChildThumbnailLoad {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FolderItemsFragmentBinding? = null
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
        _binding = FolderItemsFragmentBinding.inflate(inflater, container, false)
        prepareRecycler()
        return binding.root
    }

    private fun prepareRecycler(){
        //TODO CLean up
        binding.recyclerFolderItems.adapter = FileAdapter(viewModel.currentAudioFiles)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FileListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FolderItemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAdapterChildClicked(v: View, position: Int) {
        //TODO CLean up
        viewModel.currentPlaylist.clear()
        viewModel.currentPlaylist.addAll(viewModel.currentAudioFiles)
        viewModel.currentAudio = viewModel.currentAudioFiles[position]
        //viewModel.audioPlayerThumbnail = viewModel.loadedThumbnails[viewModel.currentAudio?.album]

        val navController = findNavController()

        //TODO Clean up
        navController.navigate(
            FolderItemsFragmentDirections.actionFolderItemsPathToAudioPlayerPath())
    }

    override fun onAdapterChildThumbnailLoad(v: ImageView, position: Int){
        val audio = viewModel.currentAudioFiles[position]
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