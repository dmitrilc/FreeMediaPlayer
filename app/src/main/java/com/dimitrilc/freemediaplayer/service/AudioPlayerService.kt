package com.dimitrilc.freemediaplayer.service

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import androidx.lifecycle.*
import androidx.media.MediaBrowserServiceCompat
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import com.dimitrilc.freemediaplayer.data.repos.UPDATE_ACTIVE_WORKER_UUID
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import com.dimitrilc.freemediaplayer.domain.worker.GetUpdateActiveMediaWorkerInfoObservable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

private const val TAG = "PLAYER_SERVICE"
const val PLAY_SELECTED = "PLAY_SELECTED"
const val CUSTOM_MEDIA_ID = "CUSTOM_MEDIA_ID"

@AndroidEntryPoint
class AudioPlayerService : LifecycleOwner, MediaBrowserServiceCompat() {

    private val mLifecycleDispatcher = ServiceLifecycleDispatcher(this)

    @Inject
    lateinit var mediaManager: MediaManager

    @Inject
    lateinit var globalPlaylistRepository: GlobalPlaylistRepository

    @Inject
    lateinit var activeMediaItemRepository: ActiveMediaRepository

    @Inject
    lateinit var activeMediaRepository: ActiveMediaRepository

    @Inject
    lateinit var mediaItemRepository: MediaItemRepository

    @Inject
    lateinit var getUpdateActiveMediaWorkerInfoObservable: GetUpdateActiveMediaWorkerInfoObservable

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
        activeMediaItemRepository.getObservable().asLiveData().observe(this) {
            if (it != null && isDifferentToActiveMediaCache(it)){
                activeMediaCache = it
                //audioMediaSessionCallback.onCommand(PLAY_SELECTED, null, null)
                playCurrent(it.globalPlaylistPosition)
            }
        }
    }

    private fun playCurrent(playlistPosition: Long){
        lifecycleScope.launch {
            getActiveOnce()?.let {
                audioMediaSessionCallback.onMediaChanged(
                    item = it,
                    currentItemPos = playlistPosition,
                    isPlayingNew = true
                )
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
                while (mediaSessionCompat.isActive && activeMediaCache?.isPlaying == true && isActive) {
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
            lifecycleScope.launch(Dispatchers.IO){
                val playlist = getPlaylistOnce()!!
                val nextItem = playlist[playlistPos.toInt()]

                onMediaChanged(nextItem, playlistPos, true)
            }
        }

        override fun onSkipToNext() {
            lifecycleScope.launch(Dispatchers.IO){
                val currentItem = getActiveOnce()!!
                val playlist = getPlaylistOnce()!!
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
            lifecycleScope.launch(Dispatchers.IO){
                val currentItem = getActiveOnce()!!
                val playlist = getPlaylistOnce()!!
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
            lifecycleScope.launch(Dispatchers.IO){
                mediaManager.shuffleGlobalPlaylistAndActiveItem()
            }
        }

        //TODO Clean up
        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            if (command == PLAY_SELECTED) {
                lifecycleScope.launch(Dispatchers.IO){
/*                    val item = getActiveOnce()!!
                    val playlist = getPlaylistOnce()!!
                    val metadata = mediaSessionCompat.controller.metadata
                    val currentItemPos = playlist.indexOf(item).toLong()

                    if (metadata != null){ //not first launch of player fragment
                        val currentItemId = metadata.getLong(CUSTOM_MEDIA_ID)
                        if (currentItemId != item.id){ //if service is not playing same song
                            onMediaChanged(item, currentItemPos, true)
                        } else { //if already playing same song, publishes metadata anyways for player fragment
                            onMediaChanged(item, currentItemPos, false)
                        }
                    } else { //first launch of player fragment
                        onMediaChanged(item, currentItemPos, true)
                    }*/


                }
            }
        }

        fun onMediaChanged(item: MediaItem, currentItemPos: Long, isPlayingNew: Boolean){
            if (isPlayingNew){
                onPlayFromUri(item.uri, null)
            }

            onActiveMediaChanged(currentItemPos, item.id)
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

    private suspend fun getActiveOnce() = mediaItemRepository.getActiveMediaItemOnce()
    private suspend fun getPlaylistOnce() = mediaItemRepository.getMediaItemsInGlobalPlaylistOnce()

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
            postActiveMediaProgressToRoom(
                ActiveMediaProgress(progress = position)
            )
        }
    }

    private fun onActiveMediaChanged(playlistPos: Long, id: Long){
        activeMediaCache?.let {
            val new = it.copy(
                mediaItemId = id,
                globalPlaylistPosition = playlistPos
            )

            setActiveMediaCache(new)
        }
    }

    private fun setActiveMediaCache(activeMedia: ActiveMedia){
        activeMediaCache = activeMedia
    }

    private fun postActiveMediaToRoom(activeMedia: ActiveMedia){
        lifecycleScope.launch(Dispatchers.IO){
            activeMediaRepository.insert(activeMedia)
        }
    }

    private fun postActiveMediaProgressToRoom(activeMediaProgress: ActiveMediaProgress){
        lifecycleScope.launch(Dispatchers.IO){
            activeMediaRepository.updateProgress(activeMediaProgress)
        }
    }

}