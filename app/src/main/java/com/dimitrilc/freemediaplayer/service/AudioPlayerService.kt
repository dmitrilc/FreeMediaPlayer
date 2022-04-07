package com.dimitrilc.freemediaplayer.service

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import androidx.lifecycle.*
import androidx.media.MediaBrowserServiceCompat
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import com.dimitrilc.freemediaplayer.domain.activemedia.*
import com.dimitrilc.freemediaplayer.domain.controls.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemOnceUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistOnceUseCase
import com.dimitrilc.freemediaplayer.domain.worker.GetUpdateActiveMediaWorkerInfoObservableUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

private const val TAG = "PLAYER_SERVICE"

@AndroidEntryPoint
class AudioPlayerService : LifecycleOwner, MediaBrowserServiceCompat() {

    private val mLifecycleDispatcher = ServiceLifecycleDispatcher(this)

    @Inject
    lateinit var getUpdateActiveMediaWorkerInfoObservableUseCase: GetUpdateActiveMediaWorkerInfoObservableUseCase

    @Inject
    lateinit var insertActiveMediaUseCase: InsertActiveMediaUseCase

    @Inject
    lateinit var updateMediaProgressUseCase: UpdateActiveMediaProgressUseCase

    @Inject
    lateinit var getActiveMediaItemOnceUseCase: GetActiveMediaItemOnceUseCase

    @Inject
    lateinit var getMediaItemsInGlobalPlaylistOnceUseCase: GetMediaItemsInGlobalPlaylistOnceUseCase

    @Inject
    lateinit var getActiveMediaObservableUseCase: GetActiveMediaObservableUseCase

    @Inject
    lateinit var updateActiveMediaPlaylistPositionAndMediaIdUseCase: UpdateActiveMediaPlaylistPositionAndMediaIdUseCase

    @Inject
    lateinit var skipToNextUseCase: SkipToNextUseCase

    @Inject
    lateinit var skipToPreviousUseCase: SkipToPreviousUseCase

    @Inject
    lateinit var playUseCase: PlayUseCase

    @Inject
    lateinit var pauseUseCase: PauseUseCase

    @Inject
    lateinit var shuffleUseCase: ShuffleUseCase

    private val stateBuilder = PlaybackStateCompat.Builder()

    private var activeMediaCache: ActiveMedia? = null

    private val mediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(applicationContext, TAG)
    }

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onBind(intent: Intent?): IBinder? {
        mLifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    override fun onCreate() {
        mLifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()

        bindPlayerCompletionListener()
        bindPlayerOnPreparedListener()

        mediaSessionCompat.apply {
            setPlaybackState(getInitialState())
            setCallback(audioMediaSessionCallback)
            isActive = true
        }

        sessionToken = mediaSessionCompat.sessionToken

        listenForActiveMedia()
    }

    private fun getInitialState(): PlaybackStateCompat {
        return stateBuilder
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
            .build()
    }

    private fun bindPlayerCompletionListener(){
        mediaPlayer.setOnCompletionListener {
            when(activeMediaCache?.repeatMode){
                REPEAT_MODE_ONE -> {
                    audioMediaSessionCallback.repeat()
                }
                else -> {
                    audioMediaSessionCallback.onSkipToNext()
                }
            }
        }
    }

    private fun bindPlayerOnPreparedListener(){
        val listener = MediaPlayer.OnPreparedListener { player ->
            onActiveMediaDurationChanged(player.duration.toLong())
            audioMediaSessionCallback.onPlay()
        }

        mediaPlayer.setOnPreparedListener(listener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLifecycleDispatcher.onServicePreSuperOnStart()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun listenForActiveMedia(){
        getActiveMediaObservableUseCase().asLiveData().observe(this) {
            if (it != null && isDifferentToActiveMediaCache(it)){
                activeMediaCache = it
                playCurrent()
            }
        }
    }

    private fun playCurrent() {
        lifecycleScope.launch {
            getActiveMediaItemOnceUseCase()?.let {
                audioMediaSessionCallback.onPlayFromUri(it.uri, null)
            }
        }
    }

    private fun isDifferentToActiveMediaCache(it: ActiveMedia?): Boolean {
        return it?.mediaItemId != activeMediaCache?.mediaItemId
    }

    override fun getLifecycle(): Lifecycle = mLifecycleDispatcher.lifecycle

    private val audioMediaSessionCallback = object : MediaSessionCompat.Callback() {
        var syncJob: Job? = null

        private fun startProgressBroadCastLoop() {
            syncJob = lifecycleScope.launch {
                while (mediaSessionCompat.isActive && isActive) {
                    try {
                        if (mediaPlayer.isPlaying){
                            onActiveMediaPositionChanged(mediaPlayer.currentPosition.toLong())
                        }
                    } catch (e: IllegalStateException){
                        break
                    }
                    delay(1000)
                }
            }
        }

        fun endProgressBroadCastLoop(){
            syncJob?.cancel()
            syncJob = null
        }

        fun repeat(){
            onSeekTo(0)
            endProgressBroadCastLoop()
            onPlay()
        }

        override fun onPlay() {
            onActiveMediaPlayingStateChanged(true)

            mediaPlayer.start()

            startProgressBroadCastLoop()
        }

        override fun onPause() {
            mediaPlayer.pause()

            endProgressBroadCastLoop()

            onActiveMediaPlayingStateChanged(false)
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            endProgressBroadCastLoop()

            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(applicationContext, uri!!)
                mediaPlayer.prepare()
            } catch (e: IllegalStateException){
                Log.d(TAG, "User is spamming Seek button")
                Log.d(TAG, "$e")
            } catch (e: IOException){
                Log.d(TAG, "$e")
            }
        }

        override fun onSkipToQueueItem(playlistPos: Long) {
            updateActiveMediaPlaylistPositionAndMediaIdUseCase(playlistPos)
        }

        override fun onSkipToNext() {
            skipToNextUseCase()
        }

        override fun onSkipToPrevious() {
            skipToPreviousUseCase()
        }

        override fun onSeekTo(pos: Long) {
            mediaPlayer.seekTo(pos.toInt())
            onActiveMediaPositionChanged(pos)
        }

        override fun onRewind() {
            activeMediaCache?.progress?.minus(10_000)?.let {
                onSeekTo(it)
            }
        }

        override fun onFastForward() {
            activeMediaCache?.progress?.plus(30_000)?.let {
                onSeekTo(it)
            }
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            onActiveMediaRepeatModeChange(repeatMode)
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            shuffleUseCase()
        }

        override fun onStop() {
            super.onStop()
            endProgressBroadCastLoop()
            mediaPlayer.release()
        }
    }

    override fun onDestroy() {
        mLifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()

        mediaSessionCompat.controller.transportControls.stop()
        mediaSessionCompat.release()
        stopSelf()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
//        return if (allowBrowsing(clientPackageName, clientUid)) {
//            // Returns a root ID that clients can use with onLoadChildren() to retrieve
//            // the content hierarchy.
//            MediaBrowserServiceCompat.BrowserRoot(MY_MEDIA_ROOT_ID, null)
//        } else {
//            // Clients can connect, but this BrowserRoot is an empty hierachy
//            // so onLoadChildren returns nothing. This disables the ability to browse for content.
//            MediaBrowserServiceCompat.BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
//        }

        return MediaBrowserServiceCompat.BrowserRoot("MY_MEDIA_ROOT_ID", null)

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

        result.sendResult(mediaItems)
    }

    private fun onActiveMediaRepeatModeChange(repeatMode: Int){
        activeMediaCache?.let {
            val new = it.copy(repeatMode = repeatMode)
            setActiveMediaCache(new)
            postActiveMediaToRoom(new)
        }
    }

    private fun onActiveMediaDurationChanged(duration: Long){
        activeMediaCache?.let {
            val new = it.copy(duration = duration)
            setActiveMediaCache(new)
            postActiveMediaToRoom(new)
        }
    }

    private fun onActiveMediaPlayingStateChanged(isPlaying: Boolean){
/*        lifecycleScope.launch(Dispatchers.IO){
            if (isPlaying){
                playUseCase()
            } else {
                pauseUseCase()
            }
        }*/

        activeMediaCache?.let {
            val new = it.copy(isPlaying = isPlaying)
            setActiveMediaCache(new)
            postActiveMediaToRoom(new)
        }
    }

    private fun onActiveMediaPositionChanged(position: Long){
        activeMediaCache?.let {
            val new = it.copy(progress = position)
            setActiveMediaCache(new)
            postActiveMediaToRoom(new)
        }
    }

    private fun setActiveMediaCache(activeMedia: ActiveMedia){
        activeMediaCache = activeMedia
    }

    private fun postActiveMediaToRoom(activeMedia: ActiveMedia){
        lifecycleScope.launch(Dispatchers.IO){
            insertActiveMediaUseCase(activeMedia)
        }
    }

/*    private fun postActiveMediaProgressToRoom(activeMediaProgress: ActiveMediaProgress){
        lifecycleScope.launch(Dispatchers.IO){
            updateMediaProgressUseCase(activeMediaProgress)
        }
    }*/

}