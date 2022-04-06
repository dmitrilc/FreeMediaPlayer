package com.dimitrilc.freemediaplayer.ui.fragments.player

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.FragmentPlayerBinding
import com.dimitrilc.freemediaplayer.ui.viewmodel.AppViewModel
import com.dimitrilc.freemediaplayer.ui.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "PLAYER_ABSTRACT"

@AndroidEntryPoint
abstract class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    protected val binding get() = _binding!!

    protected val appViewModel: AppViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by viewModels()

    protected val mediaControllerCompat: MediaControllerCompat by lazy {
        getMediaController()
    }

    abstract fun getMediaController(): MediaControllerCompat
    abstract fun isAudio(): Boolean

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        playerViewModel.playerUiStateObservable.observe(viewLifecycleOwner) { playerUiState ->
            if (isAudio()){
                if (playerUiState.thumbnail != null){
                    binding.videoViewPlayer.background = playerUiState.thumbnail.toDrawable(resources)
                } else {
                    binding.videoViewPlayer.background = null
                }
            }

            binding.textViewPlayerTitle.text = playerUiState.title

            if (playerUiState.isPlaying) {
                binding.imageButtonPlayerPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
            } else {
                binding.imageButtonPlayerPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }

            if (playerUiState.repeatMode == REPEAT_MODE_ONE){
                binding.imageButtonPlayerReplayInfinite.setImageResource(R.drawable.ic_baseline_repeat_one_24)
            } else {
                binding.imageButtonPlayerReplayInfinite.setImageResource(R.drawable.ic_baseline_repeat_24)
            }

            binding.textViewPlayerTitle.text = playerUiState.title
            binding.seekBarPlayerSeekBar.progress = playerUiState.position.toInt()
            binding.seekBarPlayerSeekBar.max = playerUiState.duration.toInt()
        }

        return binding.root
    }

    protected fun syncButtonsToController(){
        mediaControllerCompat?.let { controller ->
            buildTransportControls(controller)
        }
    }

    private fun bindPlayPauseButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerPlayPause.setOnClickListener {
            playerViewModel.playerUiStateObservable.value?.let {
                if (it.isPlaying){
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
            }
        }
    }

    private fun bindSeekNextButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerSeekForward.setOnClickListener {
            controller.transportControls.skipToNext()
        }
    }

    private fun bindSeekPreviousButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerSeekBackward.setOnClickListener {
            controller.transportControls.skipToPrevious()
        }
    }

    private fun bindShuffleButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerShuffle.setOnClickListener {
            controller.transportControls.setShuffleMode(SHUFFLE_MODE_ALL)
        }
    }

    private fun bindPlaylistButtonToController() {
        binding.imageButtonPlayerPlaylist.setOnClickListener {
            findNavController().navigate(R.id.active_playlist_path)
        }
    }

    private fun bindSeekBarChangeListenerToController(controller: MediaControllerCompat) {
        val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    controller.transportControls.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        binding.seekBarPlayerSeekBar.setOnSeekBarChangeListener(seekBarListener)
    }

    private fun bindReplayButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerReplayInfinite.setOnClickListener {
            if (playerViewModel.playerUiStateObservable.value?.repeatMode == REPEAT_MODE_NONE){
                controller.transportControls.setRepeatMode(REPEAT_MODE_ONE)
            } else {
                controller.transportControls.setRepeatMode(REPEAT_MODE_NONE)
            }
        }
    }

    private fun bindReplay10ButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerReplay10.setOnClickListener {
            controller.transportControls.rewind()
        }
    }

    private fun bindForward30ButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerForward30.setOnClickListener {
            controller.transportControls?.fastForward()
        }
    }

    private fun buildTransportControls(controller: MediaControllerCompat) {
        bindPlaylistButtonToController()
        bindSeekPreviousButtonToController(controller)
        bindSeekNextButtonToController(controller)
        bindShuffleButtonToController(controller)
        bindReplayButtonToController(controller)
        bindSeekBarChangeListenerToController(controller)
        bindPlayPauseButtonToController(controller)
        bindReplay10ButtonToController(controller)
        bindForward30ButtonToController(controller)
    }
}