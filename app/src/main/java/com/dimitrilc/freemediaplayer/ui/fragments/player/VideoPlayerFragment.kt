package com.dimitrilc.freemediaplayer.ui.fragments.player

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.ui.viewmodel.player.VideoPlayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*

private const val TAG = "PLAYER_VIDEO"

class VideoPlayerFragment : PlayerFragment() {
    private val stateBuilder = PlaybackStateCompat.Builder()

    private val videoPlayerViewModel by viewModels<VideoPlayerViewModel>()

    private val videoMediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(context, TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupVideoMediaSessionCompat()
        syncButtonsToController()
        bindPlayerErrorListener()
        bindPlayerCompletionListener()
        bindPlayerOnPreparedListener()
        setupFullscreenButton()

        listenForActiveMedia()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupFullscreenButton(){
        var job: Job? = null

        binding.videoViewContainer.setOnClickListener {
            job?.cancel()

            binding.buttonFullscreen.visibility = View.VISIBLE

            job = viewLifecycleOwner.lifecycleScope.launch {
                delay(1000)
                binding.buttonFullscreen.visibility = View.GONE
            }
        }

        binding.buttonFullscreen.setOnClickListener {
            throw Exception()
        }
    }

    private fun bindPlayerCompletionListener(){
        binding.videoViewPlayer.setOnCompletionListener {
            when(videoPlayerViewModel.activeMediaCache?.repeatMode){
                PlaybackStateCompat.REPEAT_MODE_ONE -> {
                    videoMediaSessionCallback.repeat()
                }
                else -> {
                    videoMediaSessionCallback.onSkipToNext()
                }
            }
        }
    }

    private fun bindPlayerOnPreparedListener(){
        val listener = MediaPlayer.OnPreparedListener { player ->
            onActiveMediaDurationChanged(player.duration.toLong())
            videoMediaSessionCallback.onPlay()
        }

        binding.videoViewPlayer.setOnPreparedListener(listener)
    }

    override fun onResume() {
        super.onResume()
        if (!binding.videoViewPlayer.isPlaying){
            videoPlayerViewModel.activeMediaItemCache?.let{
                videoMediaSessionCallback.onPlayFromUri(it.uri, null)
                videoPlayerViewModel.activeMediaCache?.let {
                    videoMediaSessionCallback.onSeekTo(it.progress)
                }
            }
        }
    }

    private fun listenForActiveMedia(){
        videoPlayerViewModel.activeMediaObservable.observe(viewLifecycleOwner) {
            if (it != null && isDifferentToActiveMediaCache(it)){
                videoPlayerViewModel.activeMediaCache = it
                playCurrent()
            }
        }
    }

    private fun isDifferentToActiveMediaCache(it: ActiveMedia?): Boolean {
        return it?.mediaItemId != videoPlayerViewModel.activeMediaCache?.mediaItemId
    }

    private fun playCurrent(){
        lifecycleScope.launch {
            videoPlayerViewModel.getActiveMediaItemOnce()?.let {
                videoPlayerViewModel.activeMediaItemCache = it
                videoMediaSessionCallback.onPlayFromUri(it.uri, null)
            }
        }
    }

    private val videoMediaSessionCallback = object : MediaSessionCompat.Callback() {
        var syncJob: Job? = null

        private fun startProgressLoop() {
            syncJob = lifecycleScope.launch {
                while (videoMediaSessionCompat.isActive && isActive) {
                    try {
                        if (binding.videoViewPlayer.isPlaying){
                            onActiveMediaPositionChanged(binding.videoViewPlayer.currentPosition.toLong())
                        }
                    } catch (e: IllegalStateException){
                        break
                    }
                    delay(1000)
                }
            }
        }

        private fun endProgressLoop() {
            syncJob?.cancel()
            syncJob = null
        }

        override fun onPlay() {
            onActiveMediaPlayingStateChanged(true)
            binding.videoViewPlayer.start()
            startProgressLoop()
        }

        override fun onPause() {
            endProgressLoop()
            binding.videoViewPlayer.pause()
            onActiveMediaPlayingStateChanged(false)
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            endProgressLoop()

            try {
                binding.videoViewPlayer.setVideoURI(uri!!)
            } catch (e: IllegalStateException){
                Log.d(TAG, "User is spamming Seek button")
                Log.d(TAG, "$e")
            }
        }

        override fun onSkipToQueueItem(playlistPos: Long) {
            videoPlayerViewModel.updateActiveMediaPlaylistPosition(playlistPos)
        }

        override fun onSkipToNext() {
            videoPlayerViewModel.skipToNext()
        }

        override fun onSkipToPrevious() {
            videoPlayerViewModel.skipToPrevious()
        }

        override fun onSeekTo(pos: Long) {
            binding.videoViewPlayer.seekTo(pos.toInt())

            onActiveMediaPositionChanged(pos)
        }

        override fun onRewind() {
            videoPlayerViewModel.activeMediaCache?.progress?.minus(10_000)?.let {
                onSeekTo(it)
            }
        }

        override fun onFastForward() {
            videoPlayerViewModel.activeMediaCache?.progress?.plus(30_000)?.let {
                onSeekTo(it)
            }
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            onActiveMediaRepeatModeChange(repeatMode)
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
/*            lifecycleScope.launch(Dispatchers.IO){
                videoPlayerViewModel.shuffleGlobalPlaylistAndActiveItem()
            }*/
            videoPlayerViewModel.shuffle()
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

    private fun setupVideoMediaSessionCompat() {
        videoMediaSessionCompat.apply {
            val initialState = stateBuilder
                .setActions(
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_FROM_URI
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_REWIND
                    or PlaybackStateCompat.ACTION_FAST_FORWARD //30 seconds
                    or PlaybackStateCompat.ACTION_SEEK_TO
                    or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                    or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                )
                .build()

            //Setting callback
            setPlaybackState(initialState)
            setCallback(videoMediaSessionCallback)
            isActive = true
        }
    }

    private fun bindPlayerErrorListener(){
        binding.videoViewPlayer.setOnErrorListener { _, what, extra ->
            if(what == MediaPlayer.MEDIA_ERROR_UNKNOWN){
                when(extra){
                    -2147483648 -> {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle("Codec not supported")
                                .setMessage("This app is only used for demonstration purposes. " +
                                        "Add ExoPlayer lib to use additional software decoders.")
                                .setNeutralButton("Close") { _, _ -> }
                                .show()
                        }
                    }
                    MediaPlayer.MEDIA_ERROR_IO -> {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle("IO Error")
                                .setMessage("File or network problem")
                                .setNeutralButton("Close") { _, _ -> }
                                .show()
                        }
                    }
                    MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle("Malformed Error")
                                .setMessage("Bitstream is not conforming to the related coding standard or file spec.")
                                .setNeutralButton("Close") { _, _ -> }
                                .show()
                        }
                    }
                    MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle("Codec not supported")
                                .setMessage("This app is only used for demonstration purposes. " +
                                        "Add ExoPlayer lib to use additional software decoders.")
                                .setNeutralButton("Close") { _, _ -> }
                                .show()
                        }
                    }
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
        super.onDestroyView()
    }

    override fun onDestroy() {
        videoMediaSessionCompat.release()
        super.onDestroy()
    }

    private fun onActiveMediaRepeatModeChange(repeatMode: Int){
        videoPlayerViewModel.activeMediaCache?.let {
            val new = it.copy(repeatMode = repeatMode)
            setActiveMediaCache(new)
            videoPlayerViewModel.postActiveMediaToRoom(new)
        }
    }

    private fun onActiveMediaDurationChanged(duration: Long){
        videoPlayerViewModel.activeMediaCache?.let {
            val new = it.copy(duration = duration)
            setActiveMediaCache(new)
            videoPlayerViewModel.postActiveMediaToRoom(new)
        }
    }

    private fun onActiveMediaPlayingStateChanged(isPlaying: Boolean){
        videoPlayerViewModel.activeMediaCache?.let {
            val new = it.copy(isPlaying = isPlaying)
            setActiveMediaCache(new)
            if (isPlaying){
                videoPlayerViewModel.play()
            } else {
                videoPlayerViewModel.pause()
            }
        }
    }

    private fun onActiveMediaPositionChanged(position: Long){
        videoPlayerViewModel.activeMediaCache?.let {
            val new = it.copy(progress = position)
            setActiveMediaCache(new)
            videoPlayerViewModel.postActiveMediaToRoom(new)
        }
    }

    private fun setActiveMediaCache(activeMedia: ActiveMedia){
        videoPlayerViewModel.activeMediaCache = activeMedia
    }

    override fun isAudio() = false
}