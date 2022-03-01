package com.example.freemediaplayer.fragments

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.R
import com.example.freemediaplayer.databinding.FragmentPlayerBinding
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "PLAYER_ABSTRACT"

@AndroidEntryPoint
abstract class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    protected val binding get() = _binding!!

    protected val mediaItemsViewModel: MediaItemsViewModel by activityViewModels()
    protected var mediaControllerCompat: MediaControllerCompat? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        adaptChildPlayer()
        return binding.root
    }

    abstract fun adaptChildPlayer()

    protected fun syncButtonsToController(){
        mediaControllerCompat?.let { controller ->
            buildTransportControls(controller)
        }
    }

    private fun bindMediaControllerCallback(controller: MediaControllerCompat) {
        controller.registerCallback(mediaControllerCallbacks)
    }

    private fun bindPlayPauseButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerPlayPause.setOnClickListener {
            if (controller.playbackState.state == STATE_PLAYING) {
                controller.transportControls.pause()
            } else {
                controller.transportControls.play()
            }
        }
    }

    //TODO Fix glitch where progress bar does not resync automatically
    private fun bindSeekNextButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerSeekForward.setOnClickListener {
            controller.transportControls.skipToNext()
        }
    }

    //TODO Remove duplicate code from this and seeknext
    private fun bindSeekPreviousButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerSeekBackward.setOnClickListener {
            controller.transportControls.skipToPrevious()
        }
    }

    private fun bindShuffleButtonToController(controller: MediaControllerCompat) {
        //TODO Test if need to post value to livedata
        binding.imageButtonPlayerShuffle.setOnClickListener {
            controller.transportControls.setShuffleMode(SHUFFLE_MODE_ALL)
        }
    }

    private fun bindPlaylistButtonToController() {
        binding.imageButtonPlayerPlaylist.setOnClickListener {
//            findNavController().navigate(
//                PlayerFragmentDirections.actionPlayerPathToActivePlaylistPath()
//            )
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
            when (controller.repeatMode) {
                REPEAT_MODE_NONE -> {
                    controller.transportControls.setRepeatMode(REPEAT_MODE_ONE)
                }
                REPEAT_MODE_ONE -> {
                    controller.transportControls.setRepeatMode(REPEAT_MODE_NONE)
                }
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
            controller.transportControls.fastForward()
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

        bindMediaControllerCallback(controller)
    }

    private val mediaControllerCallbacks = object : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)

            state?.position?.let {
                binding.seekBarPlayerSeekBar.progress = it.toInt()
            }

            state?.state.let {
                when (it) {
                    STATE_PLAYING -> {
                        binding.imageButtonPlayerPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
                    }
                    STATE_PAUSED -> {
                        binding.imageButtonPlayerPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                    STATE_FAST_FORWARDING -> {}
                    STATE_REWINDING -> {}
                    STATE_SKIPPING_TO_NEXT -> {}
                    STATE_SKIPPING_TO_PREVIOUS -> {}
                    STATE_SKIPPING_TO_QUEUE_ITEM -> {}
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)

            metadata?.getLong(METADATA_KEY_DURATION)?.let {
                binding.seekBarPlayerSeekBar.max = it.toInt()
            }

            metadata?.getString(METADATA_KEY_ALBUM)?.let {
                val thumbnail = mediaItemsViewModel.loadedThumbnails.value?.get(it)
                val drawable = thumbnail?.toDrawable(resources)

                binding.videoViewPlayer.background = drawable
            }

            metadata?.getString(METADATA_KEY_TITLE)?.let {
                binding.textViewPlayerTitle.text = it
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)

            if (repeatMode == REPEAT_MODE_NONE) {
                binding.imageButtonPlayerReplayInfinite.setImageResource(R.drawable.ic_baseline_repeat_24)
            } else {
                binding.imageButtonPlayerReplayInfinite.setImageResource(R.drawable.ic_baseline_repeat_one_24)
            }
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            mediaControllerCompat?.transportControls?.setShuffleMode(SHUFFLE_MODE_ALL)
        }
    }

}