package com.example.freemediaplayer.fragments

import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.freemediaplayer.FoldersAdapter
import com.example.freemediaplayer.databinding.FragmentAudioListBinding
import com.example.freemediaplayer.entities.Audio
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
        binding.recyclerAudio.adapter = FoldersAdapter(getFolderDataList())
        //val list =
        //val navController = findNavController()

        //binding.recyclerAudio

//        binding.recyclerAudio.children.forEach { child ->
//            Log.d(TAG, child.toString())
//            child.setOnClickListener {
//                Log.d(TAG, "navigating")
//                navController.navigate(AudioListFragmentDirections.actionMusicPathsToFilesPath())
//            }
//        }

        return binding.root
    }

    private fun scanAudios() {
/*        val audioTypes = mutableListOf(
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_PODCAST,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.IS_ALARM
        )*/

//        if (isSameOrAfterQ()) {
//            audioTypes.add(MediaStore.Audio.Media.IS_AUDIOBOOK)
//        }
//
//        if (isSameOrAfterS()) {
//            audioTypes.add(MediaStore.Audio.Media.IS_RECORDING)
//        }

        val projection = mutableListOf(
            //MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA
        )

//        if(isSameOrAfterQ()){
//            projection.add(MediaStore.Audio.Media.RELATIVE_PATH)
//        } else {
//            projection.add(MediaStore.Audio.Media.DATA)
//        }

        val selection = null
        val selectionArgs = null
        val sortOrder = null

        activity?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(),
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            //val displayNameColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            val dataColIndex: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

//            val dataColIndex: Int? = if (isBeforeQ()){
//                cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
//            } else null

//            val relativePathColIndex: Int? = if (isSameOrAfterQ()) {
//                cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH)
//            } else null

            val audios = mutableListOf<Audio>()

            while (cursor.moveToNext()) {
//                val audioType = audioTypeIndexMap.entries.first {
//                    (it.value > -1) && (cursor.getInt(it.value) == 1)
//                }.key.let {
//                    toReadableAudioType(it)
//                }

//                val android11AndLowerPath = dataColIndex?.let {
//                    cursor.getString(it)
//                }

//                val android11AndLowerType = dataColIndex?.let {
//                    val data = cursor.getString(it)
//                    File(data).parent
//                        ?.split("/")
//                        ?.last()
//                }

    ///storage/emulated/0/Music/Axletree - Unexpected Gifts of Spring.mp3
                val data = cursor.getString(dataColIndex)

                val displayName = data.substringAfterLast('/')
                val type = data.substringBeforeLast('/')

//                val android11AndHigherRelativePath = relativePathColIndex?.let {
//                    cursor.getString(it)
//                }
//
//                val displayName = cursor.getString(displayNameColIndex)
//
//                val android11AndHigherPath = android11AndHigherRelativePath?.let { relativePath ->
//                    relativePath + displayName
//                }
//
//                val type = if (android11AndLowerPath !== null){
//                    android11AndLowerPath.substringAfterLast('/')
//                } else android11AndHigherRelativePath
//
//                val uri = if (android11AndLowerPath !== null){
//                    android11AndLowerPath
//                } else android11AndHigherPath

                val audio = Audio(
                    displayName = displayName,
//                    title = cursor.getString(titleColIndex),
//                    artist = cursor.getString(artistColIndex),
//                    album = cursor.getString(albumColIndex),
                    type = type,
                    uri = data
                )

                audios.add(audio)
            }

            viewModel.insertAudios(audios)
        }
    }

//    private fun toReadableAudioType(type: String) = when (type) {
//        MediaStore.Audio.Media.IS_RINGTONE -> "Ringtones"
//        MediaStore.Audio.Media.IS_MUSIC -> "Music"
//        MediaStore.Audio.Media.IS_PODCAST -> "Podcast"
//        MediaStore.Audio.Media.IS_NOTIFICATION -> "Notifications"
//        MediaStore.Audio.Media.IS_ALARM -> "Alarms"
//        MediaStore.Audio.Media.IS_AUDIOBOOK -> "Audio Books"
//        MediaStore.Audio.Media.IS_RECORDING -> "Recording"
//        else -> "Unknown"
//    }

    private fun getFolderDataList(): List<FolderData> {
        scanAudios()

//        val audioFolderData = viewModel.getAudioTypes().map { path ->
//            FolderData(path)
//        }

        //TODO Fix issue of slow loading on first load

        val audioFolderData = viewModel.allAudios.map { audio ->
            FolderData(audio.type)
        }

        return audioFolderData
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