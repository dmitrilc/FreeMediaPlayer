package com.example.freemediaplayer.fragments

import android.media.MediaPlayer
import android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.freemediaplayer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "PLAYER_VIDEO"

/**
 * A simple [Fragment] subclass.
 * Use the [VideoPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//@AndroidEntryPoint
class VideoPlayerFragment : PlayerFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //private val mediaDescBuilder = MediaDescriptionCompat.Builder()
    private val stateBuilder = PlaybackStateCompat.Builder()
    private val metadataBuilder = MediaMetadataCompat.Builder()

    //Used for Video player only
    private var videoMediaSessionCompat: MediaSessionCompat? = null

    private val videoMediaSessionCallback = object : MediaSessionCompat.Callback() {
        var syncJob: Job? = null

        private fun startProgressBroadCastLoop() {
            val listener = MediaPlayer.OnPreparedListener {
                val metaData = metadataBuilder
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        it.duration.toLong()
                    )
                    .build()

                videoMediaSessionCompat?.setMetadata(metaData)
            }

            binding.videoViewPlayer.setOnPreparedListener(listener)

            syncJob = lifecycleScope.launch {
                videoMediaSessionCompat?.let { mediaSessionCompat ->
                    while (mediaSessionCompat.isActive) {
                        val state = stateBuilder
                            .setState(
                                STATE_PLAYING,
                                binding.videoViewPlayer.currentPosition.toLong(),
                                1.0f
                            )
                            .build()

                        mediaSessionCompat.setPlaybackState(state)

                        delay(1000)
                    }
                }
            }
        }

        private fun endProgressBroadCastLoop() {
            val state = stateBuilder
                .setState(
                    STATE_PAUSED,
                    binding.videoViewPlayer.currentPosition.toLong(),
                    1.0f
                )
                .build()

            videoMediaSessionCompat?.setPlaybackState(state)

            syncJob?.cancel()
            syncJob = null
        }

        override fun onPlay() {
            super.onPlay()
            binding.videoViewPlayer.start()
            startProgressBroadCastLoop()
        }

        override fun onPause() {
            super.onPause()
            binding.videoViewPlayer.pause()
            endProgressBroadCastLoop()
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)

            uri?.let {
                binding.videoViewPlayer.setVideoURI(it)
            }

            endProgressBroadCastLoop()
            onPlay()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()

            if (mediaItemsViewModel.activeMedia.value === mediaItemsViewModel.globalPlaylist.value?.last()) {
                mediaItemsViewModel.activeMedia.postValue(mediaItemsViewModel.globalPlaylist.value?.first())
            } else {
                val currentAudioIndex =
                    mediaItemsViewModel.globalPlaylist.value?.indexOf(mediaItemsViewModel.activeMedia.value)

                currentAudioIndex?.let {
                    mediaItemsViewModel.activeMedia
                        .postValue(mediaItemsViewModel.globalPlaylist.value?.get(it + 1))
                }
            }
            //TODO Handle playlist repeat mode
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()

            if (mediaItemsViewModel.activeMedia.value === mediaItemsViewModel.globalPlaylist.value?.first()) {
                mediaItemsViewModel.activeMedia.postValue(mediaItemsViewModel.globalPlaylist.value?.last())
            } else {
                val currentAudioIndex =
                    mediaItemsViewModel.globalPlaylist.value?.indexOf(mediaItemsViewModel.activeMedia.value)

                currentAudioIndex?.let {
                    mediaItemsViewModel.activeMedia
                        .postValue(mediaItemsViewModel.globalPlaylist.value?.get(it - 1))
                }
            }
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)

            binding.videoViewPlayer.seekTo(pos.toInt())

            val state = stateBuilder
                .setState(
                    STATE_BUFFERING,
                    pos,
                    1.0f
                )
                .build()

            videoMediaSessionCompat?.setPlaybackState(state)
        }

        override fun onRewind() {
            super.onRewind()

            val newPos = binding.videoViewPlayer.currentPosition - 10_000

            binding.videoViewPlayer.seekTo(newPos)

            val state = stateBuilder
                .setState(
                    STATE_REWINDING,
                    newPos.toLong(),
                    1.0f
                )
                .build()

            videoMediaSessionCompat?.setPlaybackState(state)
        }

        override fun onFastForward() {
            super.onFastForward()

            val newPos = binding.videoViewPlayer.currentPosition + 30_000

            binding.videoViewPlayer.seekTo(newPos)

            val state = stateBuilder
                .setState(
                    STATE_FAST_FORWARDING,
                    newPos.toLong(),
                    1.0f
                )
                .build()

            videoMediaSessionCompat?.setPlaybackState(state)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            videoMediaSessionCompat?.setRepeatMode(repeatMode)
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)

            mediaItemsViewModel.globalPlaylist.value?.shuffled()?.let {
                mediaItemsViewModel.globalPlaylist.postValue(it.toMutableList())
            }
        }

        override fun onStop() {
            super.onStop()
            endProgressBroadCastLoop()
        }
    }

    override fun adaptChildPlayer() {
        setupVideoMediaSessionCompat()
        setupVideoMediaControllerCompat()
        syncButtonsToController()
        syncActiveVideoToController()
        bindVideoError()
    }

    private fun syncActiveVideoToController(){
        mediaItemsViewModel.activeMedia.observe(viewLifecycleOwner) { mediaItem ->
            mediaControllerCompat?.transportControls?.playFromUri(mediaItem.uri, Bundle())
        }
    }

    private fun setupVideoMediaControllerCompat() {
        videoMediaSessionCompat?.let {
            mediaControllerCompat = MediaControllerCompat(context, it).also { controller ->
                bindVideoPlayingCompletion(controller)
            }
        }
    }

    private fun setupVideoMediaSessionCompat() {
        videoMediaSessionCompat = MediaSessionCompat(
            context,
            TAG
        ).apply {
            //mediaSessionCompat = MediaSessionCompat(applicationContext, TAG).apply {
            val initialState = stateBuilder
                .setActions(
                    ACTION_SKIP_TO_PREVIOUS
                            or ACTION_SKIP_TO_NEXT
                            or ACTION_PLAY
                            or ACTION_PLAY_FROM_URI
                            or ACTION_PAUSE
                            or ACTION_REWIND
                            or ACTION_FAST_FORWARD //30 seconds
                            or ACTION_SEEK_TO
                            or ACTION_SET_REPEAT_MODE
                            or ACTION_SET_SHUFFLE_MODE
                )
                .setState(
                    STATE_NONE,
                    0,
                    1.0f
                )
                .build()

            //Setting callback
            setPlaybackState(initialState)
            setCallback(videoMediaSessionCallback)
            isActive = true
        }
    }

    private fun bindVideoPlayingCompletion(controller: MediaControllerCompat) {
        binding.videoViewPlayer.setOnCompletionListener {
            if (controller.repeatMode == REPEAT_MODE_ONE) {
                controller.transportControls.seekTo(0)
                controller.transportControls.play()
            } else {
                controller.transportControls.skipToNext()
            }
        }
    }

    private fun bindVideoError(){
        binding.videoViewPlayer.setOnErrorListener { _, what, extra ->
            if (what == MEDIA_ERROR_UNKNOWN && extra == -2147483648){
                context?.let {
                    MaterialAlertDialogBuilder(it)
                        .setTitle("Codec not supported")
                        .setMessage("This app is only used for demonstration purposes. " +
                                "Add ExoPlayer lib to use additional software decoders.")
                        .setNeutralButton("Close") { _, _ -> }
                        .show()
                }
            }

            true
        }
    }

    override fun onStop() {
        super.onStop()
        binding.videoViewPlayer.stopPlayback()
        videoMediaSessionCompat?.release()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VideoPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}

//TODO Kill the audio service on resume if playing when get to this fragment