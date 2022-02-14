package com.example.freemediaplayer.fragments

import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.freemediaplayer.*
import com.example.freemediaplayer.databinding.FragmentVideoListBinding
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.pojos.FolderData
import com.example.freemediaplayer.viewmodel.FmpViewModel
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "VIDEO_LIST_FRAGMENT"

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
        _binding = FragmentVideoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    //TODO Remove repetitive code
    private fun scanVideos() {
        val projection = mutableListOf(
            MediaStore.Video.Media.DISPLAY_NAME
        )

        if(isSameOrAfterQ()){
            projection.add(MediaStore.Video.Media.RELATIVE_PATH)
        } else {
            projection.add(MediaStore.Video.Media.DATA)
        }

        val selection = null
        val selectionArgs = null
        val sortOrder = null

        activity?.contentResolver?.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(),
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val displayNameColIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)

            val dataColIndex: Int? = if (isBeforeQ()){
                cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            } else null

            val relativePathColIndex: Int? = if (isSameOrAfterQ()) {
                cursor.getColumnIndex(MediaStore.Video.Media.RELATIVE_PATH)
            } else null

            val videos = mutableListOf<Video>()

            while (cursor.moveToNext()) {
                val dataLocation = dataColIndex?.let {
                    val data = cursor.getString(it)
                    File(data).parent
                        ?.split("/")
                        ?.last()
                }

                val pathLocation = relativePathColIndex?.let {
                    cursor.getString(it).dropLast(1)
                }

               val video = Video(
                   displayName = cursor.getString(displayNameColIndex),
                   uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString(),
                   path = dataLocation
                       ?: pathLocation
                       ?: "Unknown"
               )

                videos.add(video)
            }

            viewModel.insertVideos(videos)
        }
    }

    //TODO Remove repetitive code
    private fun getFolderDataList(): List<FolderData> {
        scanVideos()

        val videoFolderData = viewModel.getVideoTypes().map { type ->
            FolderData(type)
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