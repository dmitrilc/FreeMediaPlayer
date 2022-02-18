package com.example.freemediaplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.FoldersFullAdapter
import com.example.freemediaplayer.databinding.FragmentAudioFoldersFullBinding
import com.example.freemediaplayer.pojos.AdapterFolderData
import com.example.freemediaplayer.viewmodel.AudioFoldersViewModel
import com.example.freemediaplayer.viewmodel.AudiosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "AUDIO_LIST_FRAGMENT"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioFoldersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AudioFoldersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentAudioFoldersFullBinding? = null
    private val binding get() = _binding!!

    private val audioFoldersViewModel: AudioFoldersViewModel by viewModels()
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
    ): View {
        _binding = FragmentAudioFoldersFullBinding.inflate(inflater, container, false)
        prepareRecycler()

        return binding.root
    }

    private fun prepareRecycler(){
        audioFoldersViewModel.allAudioFoldersLiveData.observe(viewLifecycleOwner){
            binding.recyclerAudioFoldersFull.adapter = FoldersFullAdapter(it)
        }

        audiosViewModel.allAudiosLiveData.observe(viewLifecycleOwner) { allAudios ->
            lifecycleScope.launch {
                val folderDataList = allAudios
                    .distinctBy { it.location }
                    .map { it.location }
                    .groupBy({ it.substringBeforeLast('/') }) {
                        it.substringAfterLast('/')
                    }
                    .map {
                        AdapterFolderData(
                            parentPath = it.key,
                            relativePaths = it.value
                        )
                    }

                audioFoldersViewModel.allAudioFoldersLiveData.postValue(folderDataList)
            }
        }
    }

    fun onFolderRelativeClicked(fullPathPos: Int, relativePathPos: Int){
        //TODO CLean up
        //viewModel.currentAudioLocation = position
        //val audioFolder = viewModel.audioFolderData?.get(fullPathPos)
        //val audioFolder = viewModel.allAudioFolders?.get(fullPathPos)


        val audioFolder = audioFoldersViewModel.allAudioFoldersLiveData.value?.get(fullPathPos)

        audioFolder?.let { folderData ->
            val audioPathParent = folderData.parentPath
            val audioPathRelative = folderData.relativePaths[relativePathPos]
            val audioFullPath = "$audioPathParent/$audioPathRelative"

            //TODO Separate
//            viewModel.currentAudioFiles = viewModel.allAudios
//                .filter { it.location == audioFullPath }

            val navController = findNavController()

            navController
                .navigate(
                    AudioFoldersFragmentDirections.actionAudioFoldersPathToFolderItemsPath(audioFullPath))
        }
    }

    fun onFolderFullCardViewClicked(position: Int){
        audioFoldersViewModel.refreshAllAudioFoldersLiveData(position)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AudioListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AudioFoldersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}