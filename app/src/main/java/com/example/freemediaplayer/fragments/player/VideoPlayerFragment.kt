package com.example.freemediaplayer.fragments.player

import android.media.MediaPlayer
import android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.service.CUSTOM_MEDIA_ID
import com.example.freemediaplayer.service.PLAY_SELECTED
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "PLAYER_VIDEO"

class VideoPlayerFragment : PlayerFragment() {
    private var param1: String? = null
    private var param2: String? = null

    private val stateBuilder = PlaybackStateCompat.Builder()
    private val metadataBuilder = MediaMetadataCompat.Builder()

    private val videoMediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(context, TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupVideoMediaSessionCompat()
        syncButtonsToController()
        bindVideoError()
        bindPlayerCompletionListener()
        playSelected()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun bindPlayerCompletionListener(){
        binding.videoViewPlayer.setOnCompletionListener {
            when(mediaControllerCompat.repeatMode){
                REPEAT_MODE_ONE -> {
                    videoMediaSessionCallback.repeat()
                }
                else -> {
                    videoMediaSessionCallback.onSkipToNext()
                }
            }
        }

        val listener = MediaPlayer.OnPreparedListener { player ->
            val metaData = metadataBuilder
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    player.duration.toLong()
                )
                .build()

            videoMediaSessionCompat.setMetadata(metaData)

            videoMediaSessionCallback.onPlay()
        }

        binding.videoViewPlayer.setOnPreparedListener(listener)
    }

    private val videoMediaSessionCallback = object : MediaSessionCompat.Callback() {
        var syncJob: Job? = null

        private fun startProgressLoop() {
            syncJob = lifecycleScope.launch {
                while (videoMediaSessionCompat.isActive
                    && mediaControllerCompat.playbackState.state == STATE_PLAYING) {
                    val state = stateBuilder
                        .setState(
                            STATE_PLAYING,
                            binding.videoViewPlayer.currentPosition.toLong(),
                            1.0f
                        )
                        .build()

                    videoMediaSessionCompat.setPlaybackState(state)

                    delay(1000)
                }
            }
        }

        private fun endProgressLoop() {
            syncJob?.cancel()
            syncJob = null
        }

        override fun onPlay() {
            super.onPlay()
            binding.videoViewPlayer.start()

            val state = stateBuilder
                .setState(
                    STATE_PLAYING,
                    binding.videoViewPlayer.currentPosition.toLong(),
                    1.0f
                )
                .build()

            videoMediaSessionCompat!!.setPlaybackState(state)

            startProgressLoop()
        }

        override fun onPause() {
            super.onPause()
            endProgressLoop()
            binding.videoViewPlayer.pause()

            val state = stateBuilder
                .setState(
                    STATE_PAUSED,
                    binding.videoViewPlayer.currentPosition.toLong(),
                    1.0f
                )
                .build()

            videoMediaSessionCompat.setPlaybackState(state)

            endProgressLoop()
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
            endProgressLoop()

            binding.videoViewPlayer.setVideoURI(uri!!)

            //onPlay()
        }

        override fun onSkipToQueueItem(playlistPos: Long) {
            super.onSkipToQueueItem(playlistPos)
            lifecycleScope.launch {
                val playlist = mediaItemsViewModel.getPlaylistOnce()
                val nextItem = playlist[playlistPos.toInt()]

                onMediaChanged(nextItem, playlistPos, true)
            }
        }

        private fun onMediaChanged(item: MediaItem, currentItemPos: Long, isPlayingNew: Boolean) {
            if (isPlayingNew){
                onPlayFromUri(item.uri, null)
            }

            setActiveMediaMetaData(currentItemPos, item.id)
            setSessionMetadata(item.title, item.id, item.albumArtUri.toString())
            //videoMediaSessionCompat!!.setRepeatMode(mRepeatMode)
        }

        private fun setSessionMetadata(title: String, id: Long, artUri: String) {
            val metaData = metadataBuilder
                .putString(
                    MediaMetadataCompat.METADATA_KEY_TITLE,
                    title
                )
                .putLong(
                    CUSTOM_MEDIA_ID,
                    id
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    artUri
                )
                .build()

            videoMediaSessionCompat.setMetadata(metaData)
        }

        private fun setActiveMediaMetaData(currentItemPos: Long, id: Long) {
            lifecycleScope.launch(Dispatchers.IO){
                mediaItemsViewModel.insertActiveMedia(currentItemPos, id)
            }
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            lifecycleScope.launch{
                val currentItem = mediaItemsViewModel.getActiveOnce()
                val playlist = mediaItemsViewModel.getPlaylistOnce()
                val currentItemPos = playlist.indexOf(currentItem)

                val nextItemPos = if (currentItemPos == playlist.lastIndex){
                    0
                } else {
                    currentItemPos + 1
                }

                val nextItem = playlist[nextItemPos]

                onMediaChanged(nextItem, nextItemPos.toLong(), true)
            }
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            lifecycleScope.launch{
                val currentItem = mediaItemsViewModel.getActiveOnce()
                val playlist = mediaItemsViewModel.getPlaylistOnce()
                val currentItemPos = playlist.indexOf(currentItem)

                val nextItemPos = if (currentItemPos == 0){
                    playlist.lastIndex
                } else {
                    currentItemPos - 1
                }

                val nextItem = playlist[nextItemPos]

                onMediaChanged(nextItem, nextItemPos.toLong(), true)
            }
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            binding.videoViewPlayer.seekTo(pos.toInt())

            val state = if (mediaControllerCompat!!.playbackState.state == STATE_PLAYING){
                stateBuilder
                    .setState(
                        STATE_PLAYING,
                        pos,
                        1.0f
                    )
                    .build()
            } else {
                stateBuilder
                    .setState(
                        STATE_PAUSED,
                        pos,
                        1.0f
                    )
                    .build()
            }

            videoMediaSessionCompat!!.setPlaybackState(state)
        }

        override fun onRewind() {
            super.onRewind()
            onSeekTo(videoMediaSessionCompat!!.controller.playbackState.position - 10_000)
        }

        override fun onFastForward() {
            super.onFastForward()
            onSeekTo(videoMediaSessionCompat!!.controller.playbackState.position + 30_000)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            videoMediaSessionCompat!!.setRepeatMode(repeatMode)
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)
        }

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            if (command == PLAY_SELECTED) {
                lifecycleScope.launch {
                    val item = mediaItemsViewModel.getActiveOnce()
                    val playlist = mediaItemsViewModel.getPlaylistOnce()
                    val currentItemPos = playlist.indexOf(item).toLong()

                    onMediaChanged(item, currentItemPos, true)
                }
            }
        }

        fun repeat(){
            onSeekTo(0)
            endProgressLoop()
            onPlay()
        }

        override fun onStop() {
            super.onStop()
            endProgressLoop()
            binding.videoViewPlayer.stopPlayback()
        }
    }

    private fun playSelected(){
        mediaControllerCompat.sendCommand(PLAY_SELECTED, null, null)
    }

    private fun setupVideoMediaSessionCompat() {
        videoMediaSessionCompat.apply {
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

    override fun getMediaController(): MediaControllerCompat {
        return videoMediaSessionCompat.controller
    }

    override fun onDestroyView() {
        videoMediaSessionCallback.onStop()
        binding.videoViewPlayer.stopPlayback()
        mediaControllerCompat.unregisterCallback(mediaControllerCallbacks)
        super.onDestroyView()
    }

    override fun onDestroy() {
        videoMediaSessionCompat.release()
        super.onDestroy()
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

//TODO Reduce code dup between video and audio service