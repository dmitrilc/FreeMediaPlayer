package com.dimitrilc.freemediaplayer.ui.fragments.player

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.FragmentVideoPlayerBinding
import com.dimitrilc.freemediaplayer.hilt.FmpApplication
import com.dimitrilc.freemediaplayer.ui.viewmodel.player.VideoPlayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "VIDEO_PLAYER_FRAG"

@AndroidEntryPoint
class VideoPlayerFragment : Fragment() {
    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private val videoPlayerViewModel by viewModels<VideoPlayerViewModel>()

    private val stateBuilder = PlaybackStateCompat.Builder()

    private val videoMediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(context, TAG)
    }

    private val mediaControllerCompat: MediaControllerCompat by lazy {
        videoMediaSessionCompat.controller
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Disconnects Audio playback, if present
        (requireActivity().application as FmpApplication).audioBrowser?.disconnect()

        videoPlayerViewModel.navigator = {
            findNavController().navigate(R.id.active_playlist_path)
        }

        setupVideoMediaSessionCompat()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = videoPlayerViewModel
        binding.controller = mediaControllerCompat

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindSeekBarChangeListener()

        bindPlayerErrorListener()
        bindPlayerCompletionListener()
        bindPlayerOnPreparedListener()

        listenForActiveMedia()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun bindSeekBarChangeListener() {
        val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaControllerCompat.transportControls.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        binding.seekBarVideoPlayerSeekBar.setOnSeekBarChangeListener(seekBarListener)
    }

    private fun bindPlayerCompletionListener(){
        binding.videoViewPlayer.setOnCompletionListener {
            when(videoPlayerViewModel.activeMedia.value?.repeatMode){
                PlaybackStateCompat.REPEAT_MODE_ONE -> {
                    mediaControllerCompat.sendCommand(COMMAND_REPEAT, null, null)
                }
                else -> {
                    mediaControllerCompat.transportControls.skipToNext()
                }
            }
        }
    }

    private fun bindPlayerOnPreparedListener(){
        val listener = MediaPlayer.OnPreparedListener { player ->
            if (requireActivity().isRotated()) {
                //startImmersiveMode()
                //startImmersiveMode()

/*                Log.d(TAG, binding.videoViewContainer.width.toString())
                Log.d(TAG, binding.videoViewContainer.height.toString())

                if (binding.videoViewPlayer.width > binding.videoViewPlayer.height){//if video is designed for landscape view
                    val params = ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    params.bottomToBottom = binding.videoViewContainer.id
                    params.topToTop = binding.videoViewContainer.id
                    params.startToStart = binding.videoViewContainer.id
                    params.endToEnd = binding.videoViewContainer.id

                    binding.videoViewPlayer.layoutParams = params
                } else if (binding.videoViewPlayer.width < binding.videoViewPlayer.height){//if video is designed for portrait view
*//*                    val params = ConstraintLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                    params.bottomToBottom = binding.videoViewContainer.id
                    params.topToTop = binding.videoViewContainer.id
                    params.startToStart = binding.videoViewContainer.id
                    params.endToEnd = binding.videoViewContainer.id

                    binding.videoViewPlayer.layoutParams = params*//*
                } else {

                }
            }*/

            }
            //onActiveMediaDurationChanged(player.duration.toLong())

            //Updates the new max duration
            videoPlayerViewModel.onDurationChanged(player.duration.toLong())

            videoPlayerViewModel.activeMedia.value?.let {
                mediaControllerCompat.transportControls.seekTo(it.progress)
            }

            mediaControllerCompat.transportControls.play()
        }

        binding.videoViewPlayer.setOnPreparedListener(listener)
    }

    private fun listenForActiveMedia(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                videoPlayerViewModel.activeMediaItem.collectLatest { mediaItem ->
                    mediaControllerCompat.transportControls.playFromUri(mediaItem.uri, null)
                }
            }
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

            val videoMediaSessionCallback = object : MediaSessionCompat.Callback() {
                var syncJob: Job? = null

                private fun startProgressLoop() {
                    syncJob = viewLifecycleOwner.lifecycleScope.launch {
                        while (videoMediaSessionCompat.isActive && isActive) {
                            try {
                                if (binding.videoViewPlayer.isPlaying){
                                    videoPlayerViewModel.onProgressChanged(binding.videoViewPlayer.currentPosition.toLong())
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
                    videoPlayerViewModel.play()

                    binding.videoViewPlayer.start()
                    startProgressLoop()
                }

                override fun onPause() {
                    endProgressLoop()
                    binding.videoViewPlayer.pause()
                    videoPlayerViewModel.pause()
                }

                override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
                    endProgressLoop()
                    binding.videoViewPlayer.stopPlayback()

                    try {
                        binding.videoViewPlayer.setVideoURI(uri)
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

                    videoPlayerViewModel.onProgressChanged(pos)
                }

                override fun onRewind() {
                    videoPlayerViewModel.activeMedia.value?.progress?.minus(10_000)?.let {
                        onSeekTo(it)
                    }
                }

                override fun onFastForward() {
                    videoPlayerViewModel.activeMedia.value?.progress?.plus(30_000)?.let {
                        onSeekTo(it)
                    }
                }

                override fun onSetRepeatMode(repeatMode: Int) {
                    videoPlayerViewModel.onRepeatModeChanged(repeatMode)
                }

                override fun onSetShuffleMode(shuffleMode: Int) {
                    videoPlayerViewModel.shuffle()
                }

                override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
                    when(command){
                        COMMAND_REPEAT -> {
                            onSeekTo(0)
                            endProgressLoop()
                            onPlay()
                        }
                    }
                }

                override fun onStop() {
                    super.onStop()
                    endProgressLoop()
                    binding.videoViewPlayer.stopPlayback()
                }
            }
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

    override fun onDestroyView() {
        mediaControllerCompat.transportControls.stop()
        super.onDestroyView()
    }

    override fun onDestroy() {
        videoMediaSessionCompat.release()
        super.onDestroy()
    }

    companion object {
        private const val COMMAND_REPEAT = "0"
    }

    private fun isVideoSmallerThanContainer(): Boolean {
        val playerWidth = binding.videoViewPlayer.width
        val playerHeight = binding.videoViewPlayer.height

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = requireActivity().windowManager.currentWindowMetrics.bounds
            val height = bounds.height()
            val width = bounds.width()
//                Log.d(TAG, height.toString())
//                Log.d(TAG, width.toString())

            //binding.videoViewContainer.layoutParams = ViewGroup.LayoutParams(width, height)

/*                val playerWidth = binding.videoViewPlayer.width
                val playerHeight = binding.videoViewPlayer.height
                Log.d(TAG, playerHeight.toString())
                Log.d(TAG, playerWidth.toString())*/
        } else {
            val display = requireActivity().windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)

            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels

            //binding.videoViewContainer.layoutParams = ViewGroup.LayoutParams(width, height)
        }

        return true
    }
}

fun Activity.isRotated(): Boolean {
    val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        baseContext.display?.rotation
    } else {
        val display = windowManager.defaultDisplay
        display.rotation
    }

    return when(rotation){
        Surface.ROTATION_90 -> true
        Surface.ROTATION_270 -> true
        else -> false
    }
}