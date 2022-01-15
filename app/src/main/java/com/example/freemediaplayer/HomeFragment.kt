package com.example.freemediaplayer

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FragmentHomeBinding
import com.example.freemediaplayer.viewmodel.FmpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "HOME_FRAGMENT"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentHomeBinding? = null
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
        val fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val recycler = fragmentHomeBinding.recyclerDirectories

        //Checks Perm, Asks for Perm if necessary, updates RecyclerView.Adapter
        context?.let {
            if (isReadExternalStoragePermGranted(it)) { //TODO Handle first launch where this is always false
                setAdapterData(recycler)
            } else {
                registerForActivityResult(RequestPermission()) { isGranted ->
                    if (isGranted) { //TODO Display some message to the user that the permission has not been granted
                        setAdapterData(recycler)
                    }
                }.launch(READ_EXTERNAL_STORAGE)
            }
        }

        return fragmentHomeBinding.root
    }

    private fun isReadExternalStoragePermGranted(context: Context) =
        checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED

    private fun setAdapterData(recycler: RecyclerView) {
        recycler.adapter = LocalFileAdapter(getFolderDataList())
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

    //TODO Query internal Audio

    private fun getFolderDataList(): List<FolderData> {
        scanAudios()
        scanVideos()

        val audioFolderData = viewModel.getAudioUris().map { uri ->
            FolderData("Audio", uri)
        }

        val videoFolderData = viewModel.getVideoUris().map { uri ->
            FolderData("Video", uri)
        }

        return audioFolderData.plus(videoFolderData)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}