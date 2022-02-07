package com.example.freemediaplayer.fragments

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.freemediaplayer.FoldersAdapter
import com.example.freemediaplayer.R
import com.example.freemediaplayer.databinding.FragmentAudioListBinding
import com.example.freemediaplayer.databinding.FragmentAudioPlayerBinding
import com.example.freemediaplayer.pojos.FileData
import com.example.freemediaplayer.viewmodel.FmpViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "AUDIO_PLAYER_FRAGMENT"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioPlayerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    val args: AudioPlayerFragmentArgs by navArgs()

    val viewModel: FmpViewModel by activityViewModels()

    private var mediaPlayer: MediaPlayer? = null

    //TODO CLean up
    private val listener = NavController.OnDestinationChangedListener { _, dest, _ ->
        if (dest.id == R.id.audioPlayerFragment) {
            val pos = args.selectedFilePos
            val fileData =
                FileData(viewModel.currentAudioFiles[pos].displayName, Uri.parse(viewModel.currentAudioFiles[pos].uri))
            viewModel.currentFileData = fileData

            Log.d(TAG, fileData.uri.toString())

            context?.let {
                mediaPlayer = MediaPlayer.create(it, fileData.uri)
                    .apply {
                        start()
                        Log.d(TAG, "player started")
                    }
            }
        }
    }

    //TODO Remove if not needed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val navController = findNavController()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)

        mediaPlayer?.let { player ->
            val seekBar = binding.seekBar

            val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser){
                        player.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }

            seekBar.max = player.duration
            seekBar.setOnSeekBarChangeListener(seekBarListener)

            lifecycleScope.launch {
                while (true){
                    seekBar.progress = player.currentPosition
                    delay(1000)
                }
            }
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AudioPlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AudioPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onStop() {
        super.onStop()

        mediaPlayer?.release()
        mediaPlayer = null

        val navController = findNavController()
        navController.removeOnDestinationChangedListener(listener)
    }
}