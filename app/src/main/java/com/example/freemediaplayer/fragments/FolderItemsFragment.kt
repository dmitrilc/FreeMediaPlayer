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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.freemediaplayer.FileAdapter
import com.example.freemediaplayer.databinding.FolderItemsFragmentBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.viewmodel.AudiosViewModel
import com.example.freemediaplayer.viewmodel.FolderItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
@AndroidEntryPoint
class FolderItemsFragment : Fragment()
    //AdapterChildThumbnailLoad
{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FolderItemsFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: FolderItemsFragmentArgs by navArgs()

    private val audiosViewModel by activityViewModels<AudiosViewModel>()
    private val folderItemsViewModel by viewModels<FolderItemsViewModel>()

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
        folderItemsViewModel.currentFolderAudioFiles.observe(viewLifecycleOwner){
            binding.recyclerFolderItems.adapter = FileAdapter(it)
        }

        audiosViewModel.allAudiosLiveData
            .map { allAudios ->
                allAudios.filter {
                    it.location == args.folderFullPath
                }
            }
            .observe(viewLifecycleOwner) {
                folderItemsViewModel.currentFolderAudioFiles.postValue(it)
            }
    }

    fun onFolderItemClicked(position: Int) {
        folderItemsViewModel.currentFolderLocation?.let {
            val navController = findNavController()

            navController.navigate(
                FolderItemsFragmentDirections.actionFolderItemsPathToAudioPlayerPath(it, position))
        }
    }

    fun onAdapterChildThumbnailLoad(imageView: ImageView, audio: Audio) {
        lifecycleScope.launch {
            val thumbnailObserver = object : Observer<Map<String, Bitmap?>> {
                override fun onChanged(thumbnailMap: Map<String, Bitmap?>?) {
                    thumbnailMap?.get(audio.album)?.let {
                        imageView.setImageBitmap(it)
                        audiosViewModel.loadedThumbnails.removeObserver(this)
                    }
                }
            }

            audiosViewModel.loadedThumbnails.observe(viewLifecycleOwner, thumbnailObserver)

            audiosViewModel.loadThumbnail(audio)
        }
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

}