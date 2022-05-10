package com.dimitrilc.freemediaplayer.ui.fragments.player

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.MediaController
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.databinding.FragmentVideoPlayerBinding
import com.dimitrilc.freemediaplayer.hilt.FmpApplication
import com.dimitrilc.freemediaplayer.ui.viewmodel.AppViewModel
import com.dimitrilc.freemediaplayer.ui.viewmodel.player.AudioPlayerViewModel
import com.dimitrilc.freemediaplayer.ui.viewmodel.player.VideoPlayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

private const val TAG = "VIDEO_PLAYER_FRAG"

@AndroidEntryPoint
class VideoPlayerFragment : Fragment() {
    private val stateBuilder = PlaybackStateCompat.Builder()

    private val videoPlayerViewModel by viewModels<VideoPlayerViewModel>()

    private val videoMediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(context, TAG)
    }

    private var _binding: FragmentVideoPlayerBinding? = null
    protected val binding get() = _binding!!

    protected val appViewModel: AppViewModel by activityViewModels()
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModels()

    protected val mediaControllerCompat: MediaControllerCompat by lazy {
        getMediaController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as FmpApplication).audioBrowser?.disconnect()
    }

    protected fun syncButtonsToController(){
        mediaControllerCompat?.let { controller ->
            buildTransportControls(controller)
        }
    }

    private fun buildTransportControls(controller: MediaControllerCompat) {
/*        if (isRotated()){
            //build full screen buttons here
        } else {
            bindPlaylistButtonToController()
            bindSeekPreviousButtonToController(controller)
            bindSeekNextButtonToController(controller)
            bindShuffleButtonToController(controller)
            bindReplayButtonToController(controller)
            bindSeekBarChangeListenerToController(controller)
            bindPlayPauseButtonToController(controller)
            bindReplay10ButtonToController(controller)
            bindForward30ButtonToController(controller)
        }*/
    }

    private fun bindPlayPauseButtonToController(controller: MediaControllerCompat) {
        binding.imageButtonPlayerPlayPause.setOnClickListener {
            audioPlayerViewModel.uiState.value?.let {
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
            controller.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
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
            if (audioPlayerViewModel.uiState.value?.repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE){
                controller.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
            } else {
                controller.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
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
/*        var job: Job? = null

        binding.videoViewContainer.setOnClickListener {
            job?.cancel()

            binding.buttonFullscreen.visibility = View.VISIBLE

            job = viewLifecycleOwner.lifecycleScope.launch {
                delay(1000)
                binding.buttonFullscreen.visibility = View.GONE
            }
        }

        binding.buttonFullscreen.setOnClickListener {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }*/

        //requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

/*        portraitPlayerBinding.buttonFullscreen.setOnClickListener {
            val windowInsetsController = WindowCompat.getInsetsController(
                requireActivity().window,
                requireActivity().window.decorView
            )

            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // Hide both the status bar and the navigation bar
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

            portraitPlayerBinding.playerFirstControlGroup.visibility = View.GONE
            portraitPlayerBinding.playerSecondControlGroup.visibility = View.GONE
            portraitPlayerBinding.textViewPlayerTitle.visibility = View.GONE
            portraitPlayerBinding.seekBarPlayerSeekBar.visibility = View.GONE
            val topAppBar = requireActivity()
                .findViewById<MaterialToolbar>(R.id.materialToolBarView_topAppBar)
            topAppBar.visibility = View.GONE

            //To get the screen size
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bounds = requireActivity().windowManager.currentWindowMetrics.bounds
                val height = bounds.height()
                val width = bounds.width()
//                Log.d(TAG, height.toString())
//                Log.d(TAG, width.toString())
                portraitPlayerBinding.videoViewContainer.layoutParams = ViewGroup.LayoutParams(width, height)

*//*                val playerWidth = binding.videoViewPlayer.width
                val playerHeight = binding.videoViewPlayer.height
                Log.d(TAG, playerHeight.toString())
                Log.d(TAG, playerWidth.toString())*//*
            } else {
                val display = requireActivity().windowManager.defaultDisplay
                val displayMetrics = DisplayMetrics()
                display.getMetrics(displayMetrics)

                val height = displayMetrics.heightPixels
                val width = displayMetrics.widthPixels

                portraitPlayerBinding.videoViewContainer.layoutParams = ViewGroup.LayoutParams(width, height)
            }

            //requireActivity().isRotated()

            //getRotation



*//*            val set = ConstraintSet()
            binding.videoViewContainer.setConstraintSet(set)*//*
            //binding.videoViewContainer.layoutParams = ViewGroup.LayoutParams(2000, 2000)
            //binding.videoViewPlayer.layoutParams = ViewGroup.LayoutParams(2000, 2000)


        }*/

/*        fun endImmersiveMode(){
            //showTopAppBar()
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // Hide both the status bar and the navigation bar
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }*/

/*        fun hideTopAppBar(){
            binding.materialToolBarViewTopAppBar.visibility = View.GONE
        }

        fun showTopAppBar(){
            binding.materialToolBarViewTopAppBar.visibility = View.VISIBLE
        }*/
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
            if (requireActivity().isRotated()) {
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

    fun getMediaController(): MediaControllerCompat {
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

//class MyMediaController: MediaController()

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