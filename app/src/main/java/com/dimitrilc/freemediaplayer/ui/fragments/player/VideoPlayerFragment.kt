package com.dimitrilc.freemediaplayer.ui.fragments.player

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.databinding.FragmentVideoPlayerBinding
import com.dimitrilc.freemediaplayer.hilt.FmpApplication
import com.dimitrilc.freemediaplayer.ui.viewmodel.player.Action
import com.dimitrilc.freemediaplayer.ui.viewmodel.player.VideoPlayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

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
            findNavController().navigate(VideoPlayerFragmentDirections.actionVideoPlayerPathToActivePlaylistPath())
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
        setupSliderListener()
        bindPlayerErrorListener()
        bindPlayerCompletionListener()
        bindPlayerOnPreparedListener()

        listenForActiveMedia()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupSliderListener() {
        val sliderTouchListener = object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                mediaControllerCompat.sendCommand(COMMAND_STOP_PROGRESS, null, null)
            }

            override fun onStopTrackingTouch(slider: Slider) {
                mediaControllerCompat.transportControls.seekTo(slider.value.toLong())
                mediaControllerCompat.sendCommand(COMMAND_START_PROGRESS, null, null)
            }
        }

        binding.sliderVideoPlayerSlider.addOnSliderTouchListener(sliderTouchListener)

        //If user is still interacting with the Slider, keep controls visible
        binding.sliderVideoPlayerSlider.addOnChangeListener { _, _, fromUser ->
            if (fromUser){
                videoPlayerViewModel.accept(Action.UiAction.ShowControls)
            }
        }

        val smallSdf = SimpleDateFormat("mm:ss", Locale.getDefault())
        val largeSdf = SimpleDateFormat("h:mm:ss", Locale.getDefault())

        binding.sliderVideoPlayerSlider.setLabelFormatter {
            val date = Date(it.toLong())

            if (it.compareTo(ONE_HOUR) < 0){
                smallSdf.format(date)
            } else {
                largeSdf.format(date)
            }
        }
    }

    private fun bindPlayerCompletionListener(){
        binding.videoViewPlayer.setOnCompletionListener {
            when(videoPlayerViewModel.uiState.value?.repeatMode){
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
            if (player.videoWidth > player.videoHeight){
                val params = ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                params.bottomToBottom = binding.videoViewContainer.id
                params.topToTop = binding.videoViewContainer.id
                params.startToStart = binding.videoViewContainer.id
                params.endToEnd = binding.videoViewContainer.id

                binding.videoViewPlayer.layoutParams = params
            }
            //if video is designed for portrait view
            else if (player.videoWidth < player.videoHeight){
                val params = ConstraintLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                params.bottomToBottom = binding.videoViewContainer.id
                params.topToTop = binding.videoViewContainer.id
                params.startToStart = binding.videoViewContainer.id
                params.endToEnd = binding.videoViewContainer.id

                binding.videoViewPlayer.layoutParams = params
            }

            //Updates the new max duration
            videoPlayerViewModel.accept(Action.UiAction.UpdateDuration(player.duration))

            //On resume or after destroyed
            videoPlayerViewModel.uiState.value?.let {
                mediaControllerCompat.transportControls.seekTo(it.position.toLong())
            }

            mediaControllerCompat.transportControls.play()
        }

        binding.videoViewPlayer.setOnPreparedListener(listener)
    }

    private var playingUri: Uri = Uri.EMPTY

    private fun listenForActiveMedia(){
        videoPlayerViewModel.activeMediaItem.observe(viewLifecycleOwner){
            if (it!= null && it.uri != playingUri){
                playingUri = it.uri
                mediaControllerCompat.transportControls.playFromUri(it.uri, null)
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
                                    videoPlayerViewModel.accept(
                                        Action.UiAction.UpdateProgress(
                                            binding.videoViewPlayer.currentPosition
                                        ))
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
                    videoPlayerViewModel.accept(Action.UiAction.Play)

                    binding.videoViewPlayer.start()
                    startProgressLoop()
                }

                override fun onPause() {
                    endProgressLoop()
                    binding.videoViewPlayer.pause()
                    videoPlayerViewModel.accept(Action.UiAction.Pause)
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
                    videoPlayerViewModel.accept(Action.UiAction.SkipNext)
                }

                override fun onSkipToPrevious() {
                    videoPlayerViewModel.accept(Action.UiAction.SkipPrevious)
                }

                override fun onSeekTo(pos: Long) {
                    binding.videoViewPlayer.seekTo(pos.toInt())
                    videoPlayerViewModel.accept(Action.UiAction.UpdateProgress(pos.toInt()))
                }

                override fun onRewind() {
                    videoPlayerViewModel.uiState.value?.let {
                        onSeekTo(it.position - 10_000L)
                    }
                }

                override fun onFastForward() {
                    videoPlayerViewModel.uiState.value?.let {
                        onSeekTo(it.position + 30_000L)
                    }
                }

                override fun onSetRepeatMode(repeatMode: Int) {
                    videoPlayerViewModel.accept(Action.UiAction.SetRepeatMode(repeatMode))
                }

                override fun onSetShuffleMode(shuffleMode: Int) {
                    videoPlayerViewModel.accept(Action.UiAction.Shuffle)
                }

                override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
                    when(command){
                        COMMAND_REPEAT -> {
                            onSeekTo(0)
                            endProgressLoop()
                            onPlay()
                        }
                        COMMAND_STOP_PROGRESS -> {
                            endProgressLoop()
                        }
                        COMMAND_START_PROGRESS -> {
                            startProgressLoop()
                        }
                    }
                }

                override fun onStop() {
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
                                .setNeutralButton("Go Back") { _, _ -> }
                                .setOnDismissListener {
                                    findNavController().popBackStack()
                                }
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
                                .setNeutralButton("Go Back") { _, _ -> }
                                .setOnDismissListener {
                                    findNavController().popBackStack()
                                }
                                .show()
                        }
                    }
                }
            }
            true
        }
    }

    override fun onPause() {
        mediaControllerCompat.transportControls.stop()
        super.onPause()
    }

    override fun onDestroyView() {
        videoMediaSessionCompat.release()
        super.onDestroyView()
    }

    companion object {
        private const val ONE_HOUR = 3600000L
        private const val COMMAND_REPEAT = "0"
        private const val COMMAND_STOP_PROGRESS = "1"
        private const val COMMAND_START_PROGRESS = "2"
    }
}