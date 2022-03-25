package com.dimitrilc.freemediaplayer.fragments.player

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.FragmentPlayerBinding
import com.dimitrilc.freemediaplayer.service.CUSTOM_MEDIA_ID
import com.dimitrilc.freemediaplayer.viewmodel.MediaItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val TAG = "PLAYER_ABSTRACT"

@AndroidEntryPoint
abstract class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    protected val binding get() = _binding!!

    protected val mediaItemsViewModel: MediaItemsViewModel by activityViewModels()
    protected val mediaControllerCompat: MediaControllerCompat by lazy {
        getMediaController()
    }

    abstract fun getMediaController(): MediaControllerCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    protected fun syncButtonsToController(){
        mediaControllerCompat?.let { controller ->
            buildTransportControls(controller)
        }
    }

    private fun bindPlayPauseButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerPlayPause.setOnClickListener {
            if (controller.playbackState.state == STATE_PLAYING){
                controller.transportControls.pause()
            } else {
                controller.transportControls.play()
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
            val repeatMode = controller.repeatMode

            if (repeatMode == REPEAT_MODE_NONE){
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

        bindMediaControllerCallback(controller)
    }

    private fun bindMediaControllerCallback(controller: MediaControllerCompat) {
        controller.registerCallback(mediaControllerCallbacks)
    }

    protected val mediaControllerCallbacks = object : MediaControllerCompat.Callback() {

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

            metadata?.getString(METADATA_KEY_TITLE)?.let {
                binding.textViewPlayerTitle.text = it
            }

            val albumArtUri = metadata?.getString(METADATA_KEY_ALBUM_ART_URI)
            val videoId = metadata?.getLong(CUSTOM_MEDIA_ID)

            if (albumArtUri != null && videoId == 0L){
                lifecycleScope.launch {
                    val drawable = async(Dispatchers.IO) {
                        mediaItemsViewModel
                            .getThumbnail(albumArtUri, null)
                            ?.toDrawable(resources)
                    }

                    binding.videoViewPlayer.background = drawable.await()

                    if (binding.videoViewPlayer.background != null){
                        binding.videoViewPlayer.visibility = View.VISIBLE
                    }
                }
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