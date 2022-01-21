package com.example.freemediaplayer.fragments

import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.LocalFileAdapter
import com.example.freemediaplayer.databinding.FragmentAudioListBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.pojos.FolderData
import com.example.freemediaplayer.viewmodel.FmpViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentAudioListBinding? = null
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
        _binding = FragmentAudioListBinding.inflate(inflater, container, false)

        //val data = getFolderDataList()
        //binding.recyclerAudio.adapter = LocalFileAdapter(getFolderDataList())

        return binding.root
    }

    private fun scanAudios() {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM
        )
        val selection = null
        val selectionArgs = null
        val sortOrder = null

        activity?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val titleColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

            val audios = mutableListOf<Audio>()

            while (cursor.moveToNext()) {
                Audio(
                    title = cursor.getString(titleColIndex),
                    artist = cursor.getString(artistColIndex),
                    album = cursor.getString(albumIndex),
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
                ).also {
                    audios.add(it)
                }
            }

            viewModel.insertAudios(audios)
        }
    }

    private fun getFolderDataList(): List<FolderData> {
        scanAudios()

        val audioFolderData = viewModel.getAudioUris().map { uri ->
            FolderData("Audio", uri)
        }

        return audioFolderData
    }

    private fun setAdapterData(recycler: RecyclerView) {
        recycler.adapter = LocalFileAdapter(getFolderDataList())
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
            AudioListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}