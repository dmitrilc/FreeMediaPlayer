package com.example.freemediaplayer.fragments

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.AdapterChildClickedListener
import com.example.freemediaplayer.FoldersAdapter
import com.example.freemediaplayer.databinding.FragmentAudioListBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.pojos.FolderData
import com.example.freemediaplayer.viewmodel.FmpViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "AUDIO_LIST_FRAGMENT"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioListFragment : Fragment(), AdapterChildClickedListener {
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
        prepareRecycler()

        return binding.root
    }

    private fun scanAudios() {
        val collection =
            if (isSameOrAfterQ()) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA
        )

        val selection = null
        val selectionArgs = null
        val sortOrder = null

        activity?.contentResolver?.query(
            collection,
            projection.toTypedArray(),
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val dataColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val titleColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val albumColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

            val audios = mutableSetOf<Audio>()

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColIndex)
                val data = cursor.getString(dataColIndex)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val displayName = data.substringAfterLast('/')
                val location = data.substringBeforeLast('/')

                val audio = Audio(
                    id = id,
                    uri = uri,
                    data = data,
                    displayName = displayName,
                    title = cursor.getString(titleColIndex),
                    album = cursor.getString(albumColIndex),
                    location = location
                )

                audios.add(audio)
            }

            viewModel.insertAudios(audios)
        }
    }

    //TODO Only scan if MediaStore changed
    private fun getFolderDataList(): List<FolderData> {
        scanAudios()

        //TODO Fix issue of slow loading on first load
//        val audioFolderData = viewModel.allAudios.map { audio ->
//            FolderData(audio.location)
//        }

        val audioFolderData = viewModel.allAudios
            .distinctBy { it.location }
            .map { FolderData(it.location) }

        //TODO Too lazy. Dont do this
        viewModel.allFolders = audioFolderData

        return audioFolderData
    }

    private fun prepareRecycler(){
        binding.recyclerAudio.adapter = FoldersAdapter(getFolderDataList())
    }

    override fun onAdapterChildClicked(v: View, position: Int){
        //TODO CLean up
        viewModel.currentAudioLocation = position

        viewModel.currentAudioFiles = viewModel.allAudios
            .filter { it.location == viewModel.allFolders?.get(position)?.type}

        val navController = findNavController()

        navController
            .navigate(
                AudioListFragmentDirections.actionMusicPathsToFilesPath())
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