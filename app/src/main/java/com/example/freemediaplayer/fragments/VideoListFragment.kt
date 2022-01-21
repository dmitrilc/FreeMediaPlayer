package com.example.freemediaplayer.fragments

import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.freemediaplayer.databinding.FragmentVideoListBinding
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.pojos.FolderData
import com.example.freemediaplayer.viewmodel.FmpViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VideoListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VideoListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentVideoListBinding? = null
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
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_video_list, container, false)

        _binding = FragmentVideoListBinding.inflate(inflater, container, false)

        //val data = getFolderDataList()
        //binding.recyclerAudio.adapter = LocalFileAdapter(getFolderDataList())

        return binding.root
    }

    private fun scanVideos() {
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE
        )
        val selection = null
        val selectionArgs = null
        val sortOrder = null

        activity?.contentResolver?.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val titleColIndex = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
            val videos = mutableListOf<Video>()

            while (cursor.moveToNext()) {
                Video(
                    title = cursor.getString(titleColIndex),
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
                ).also {
                    videos.add(it)
                }
            }

            viewModel.insertVideos(videos)
        }
    }

    private fun getFolderDataList(): List<FolderData> {
        scanVideos()

//        val audioFolderData = viewModel.getAudioUris().map { uri ->
//            FolderData("Audio", uri)
//        }

        val videoFolderData = viewModel.getVideoUris().map { uri ->
            FolderData("Video", uri)
        }

        return videoFolderData
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VideoListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VideoListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}