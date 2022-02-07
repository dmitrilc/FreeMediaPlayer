package com.example.freemediaplayer.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.freemediaplayer.FilesAdapter
import com.example.freemediaplayer.databinding.FilesFragmentBinding
import com.example.freemediaplayer.pojos.FileData
import com.example.freemediaplayer.viewmodel.FmpViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "FILES_FRAGMENT"

/**
 * A simple [Fragment] subclass.
 * Use the [FilesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FilesFragmentBinding? = null
    private val binding get() = _binding!!

    val viewModel: FmpViewModel by activityViewModels()

    val args: FilesFragmentArgs by navArgs()

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
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.audio_files_fragment, container, false)

        _binding = FilesFragmentBinding.inflate(inflater, container, false)

        //TODO CLean up

        val fileViewsData = viewModel.allAudios
            .filter { it.type == args.type }
            .map { FileData(it.displayName, Uri.parse(it.uri)) }

        viewModel.currentAudioFiles = viewModel.allAudios
            .filter { it.type == args.type }

        binding.recyclerFiles.adapter = FilesAdapter(fileViewsData)

        Log.d(TAG, binding.recyclerFiles.adapter.toString())



        return binding.root
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
            FilesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}