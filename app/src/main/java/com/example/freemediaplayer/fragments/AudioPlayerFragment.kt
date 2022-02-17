package com.example.freemediaplayer.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.R
import com.example.freemediaplayer.databinding.FragmentAudioPlayerBinding
import com.example.freemediaplayer.viewmodel.FmpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    //private val args: AudioPlayerFragmentArgs by navArgs()

    val viewModel: FmpViewModel by activityViewModels()

    private var mediaPlayer: MediaPlayer? = null

    //TODO Remove if not needed
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
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (context !== null && viewModel.currentAudio !== null) {
            mediaPlayer = MediaPlayer.create(context, viewModel.currentAudio!!.uri)

            mediaPlayer?.let {
                it.start()
                syncSeekBarToPlayer(it)
                syncPlayPauseButtonToPlayer(it)
                syncReplayButtonToPlayer(it)
                syncReplay10ButtonToPlayer(it)
                syncForward30ButtonToPlayer(it)

                prepShuffleButton()
                prepSeekPreviousButton()
                prepSeekNextButton()

                prepPlaylistButton()

                binding.imageViewAudioPlayerDisplayArt.setImageBitmap(viewModel.getAudioPlayerThumbnail())
                //viewModel.audioPlayerThumbnail = viewModel.loadedThumbnails[viewModel.currentAudio?.album]
                //Log.d(TAG, "${viewModel.audioPlayerThumbnail}")
                binding.textViewAudioPlayerTitle.text = viewModel.currentAudio?.title
            }
        }

    }

    private fun prepSeekNextButton(){
        binding.imageButtonAudioPlayerSeekForward.setOnClickListener {
            if (viewModel.currentAudio === viewModel.currentPlaylist.last()){
                viewModel.currentAudio = viewModel.currentPlaylist.first()
            } else {
                val currentAudioIndex = viewModel.currentPlaylist.indexOf(viewModel.currentAudio)
                viewModel.currentAudio = viewModel.currentPlaylist[currentAudioIndex + 1]
            }

            //TODO Clean up null checks
            context?.let { context ->
                viewModel.currentAudio?.let { audio ->
                    mediaPlayer?.let { player ->
                        player.reset()
                        player.setDataSource(context, audio.uri)
                        player.prepare()
                        player.start()
                        binding.imageViewAudioPlayerDisplayArt.setImageBitmap(viewModel.getAudioPlayerThumbnail())
                        binding.textViewAudioPlayerTitle.text = viewModel.currentAudio?.title
                    }
                }
            }
        }
    }

    //TODO Remove duplicate code from this and seeknext
    private fun prepSeekPreviousButton(){
        binding.imageButtonAudioPlayerSeekBackward.setOnClickListener {
            if (viewModel.currentAudio === viewModel.currentPlaylist.first()){
                viewModel.currentAudio = viewModel.currentPlaylist.last()
            } else {
                val currentAudioIndex = viewModel.currentPlaylist.indexOf(viewModel.currentAudio)
                viewModel.currentAudio = viewModel.currentPlaylist[currentAudioIndex - 1]
            }

            //TODO Clean up null checks
            context?.let { context ->
                viewModel.currentAudio?.let { audio ->
                    mediaPlayer?.let { player ->
                        player.reset()
                        player.setDataSource(context, audio.uri)
                        player.prepare()
                        player.start()
                        binding.imageViewAudioPlayerDisplayArt.setImageBitmap(viewModel.getAudioPlayerThumbnail())
                        binding.textViewAudioPlayerTitle.text = viewModel.currentAudio?.title
                    }
                }
            }
        }
    }

    private fun prepShuffleButton(){
        binding.imageButtonAudioPlayerShuffle.setOnClickListener {
            viewModel.currentPlaylist.shuffle()
            viewModel.currentPlaylist.forEach {
                Log.d(TAG, it.toString())
            }
        }
    }

    private fun prepPlaylistButton() {
        val navController = findNavController()

        binding.imageButtonAudioPlayerPlaylist.setOnClickListener {
            navController.navigate(AudioPlayerFragmentDirections.actionAudioPlayerPathToActivePlaylistPath())
        }
    }

    private fun syncSeekBarToPlayer(player: MediaPlayer) {
        binding.seekBarAudioPlayerSeekBar.max = player.duration

        val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        binding.seekBarAudioPlayerSeekBar.setOnSeekBarChangeListener(seekBarListener)

        viewLifecycleOwner.lifecycleScope.launch {
            while (mediaPlayer !== null) {
                if (player.isPlaying) {
                    binding.seekBarAudioPlayerSeekBar.progress = player.currentPosition
                }
                delay(1000)
            }
        }
    }

    private fun syncPlayPauseButtonToPlayer(player: MediaPlayer) {
        binding.imageButtonAudioPlayerPlayPause.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                binding.imageButtonAudioPlayerPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            } else {
                player.start()
                binding.imageButtonAudioPlayerPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
            }
        }

        player.setOnCompletionListener {
            binding.imageButtonAudioPlayerPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun syncReplayButtonToPlayer(player: MediaPlayer) {
        binding.imageButtonAudioPlayerReplayInfinite.setOnClickListener {
            player.isLooping = !player.isLooping

            if (player.isLooping) {
                binding.imageButtonAudioPlayerReplayInfinite.setImageResource(
                    R.drawable.ic_baseline_repeat_one_24
                )
            } else {
                binding.imageButtonAudioPlayerReplayInfinite.setImageResource(
                    R.drawable.ic_baseline_repeat_24
                )
            }
        }
    }

    //Also syncs to ProgressBar
    private fun syncReplay10ButtonToPlayer(player: MediaPlayer) {
        binding.imageButtonAudioPlayerReplay10.setOnClickListener {
            player.seekTo(player.currentPosition - 10_000)
            binding.seekBarAudioPlayerSeekBar.progress = player.currentPosition
        }
    }

    //Also syncs to ProgressBar
    private fun syncForward30ButtonToPlayer(player: MediaPlayer) {
        binding.imageButtonAudioPlayerForward30.setOnClickListener {
            player.seekTo(player.currentPosition + 30_000)
            binding.seekBarAudioPlayerSeekBar.progress = player.currentPosition
        }
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
    }
}

enum class RepeatMode{
    PLAYLIST, SELF, NONE
}

//TODO Player bug where you drag and then player pauses