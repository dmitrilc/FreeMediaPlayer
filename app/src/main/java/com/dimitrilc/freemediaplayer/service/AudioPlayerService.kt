package com.dimitrilc.freemediaplayer.service

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.lifecycle.*
import androidx.media.MediaBrowserServiceCompat
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val TAG = "PLAYER_SERVICE"

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
    lateinit var mediaItemRepository: MediaItemRepository

    private val stateBuilder = PlaybackStateCompat.Builder()
    private val metadataBuilder = MediaMetadataCompat.Builder()

    private var mRepeatMode = REPEAT_MODE_NONE

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

        mediaSessionCompat.apply {
            setPlaybackState(getInitialState())
            setCallback(audioMediaSessionCallback)
            isActive = true
        }

        sessionToken = mediaSessionCompat.sessionToken
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
            .setState(
                STATE_NONE,
                0,
                1.0f
            )
            .build()
    }

    private fun bindPlayerCompletionListener(){
        mediaPlayer.setOnCompletionListener {
            when(mediaSessionCompat.controller.repeatMode){
                REPEAT_MODE_ONE -> {
                    audioMediaSessionCallback.repeat()
                }
                else -> {
                    audioMediaSessionCallback.onSkipToNext()
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

            mediaSessionCompat.setMetadata(metaData)

            audioMediaSessionCallback.onPlay()
        }

        mediaPlayer.setOnPreparedListener(listener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLifecycleDispatcher.onServicePreSuperOnStart()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun getLifecycle(): Lifecycle = mLifecycleDispatcher.lifecycle

    override fun onDestroy() {
        mLifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()

        mediaSessionCompat.controller.transportControls.stop()
        mediaSessionCompat.release()
        stopSelf()
    }

    private val audioMediaSessionCallback = object : MediaSessionCompat.Callback() {
        var syncJob: Job? = null

        private fun startProgressBroadCastLoop() {
            syncJob = lifecycleScope.launch {
                while (mediaSessionCompat.isActive
                    && mediaSessionCompat.controller.playbackState.state == STATE_PLAYING) {
                    val state = stateBuilder
                        .setState(
                            STATE_PLAYING,
                            mediaPlayer.currentPosition.toLong(),
                            1.0f
                        )
                        .build()

                    mediaSessionCompat.setPlaybackState(state)

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
            super.onPlay()
            mediaPlayer.start()

            val state = stateBuilder
                .setState(
                    STATE_PLAYING,
                    mediaPlayer.currentPosition.toLong(),
                    1.0f
                )
                .build()

            mediaSessionCompat.setPlaybackState(state)

            startProgressBroadCastLoop()
        }

        override fun onPause() {
            super.onPause()
            mediaPlayer.pause()

            val state = stateBuilder
                .setState(
                    STATE_PAUSED,
                    mediaPlayer.currentPosition.toLong(),
                    1.0f
                )
                .build()

            mediaSessionCompat.setPlaybackState(state)

            endProgressBroadCastLoop()
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
            endProgressBroadCastLoop()

            mediaPlayer.reset()
            mediaPlayer.setDataSource(applicationContext, uri!!)
            mediaPlayer.prepare()
        }

        override fun onSkipToQueueItem(playlistPos: Long) {
            super.onSkipToQueueItem(playlistPos)
            lifecycleScope.launch(Dispatchers.IO){
                val playlist = getPlaylistOnce()
                val nextItem = playlist[playlistPos.toInt()]

                onMediaChanged(nextItem, playlistPos, true)
            }
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            lifecycleScope.launch(Dispatchers.IO){
                val currentItem = getActiveOnce()
                val playlist = getPlaylistOnce()
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
            lifecycleScope.launch(Dispatchers.IO){
                val currentItem = getActiveOnce()
                val playlist = getPlaylistOnce()
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
            mediaPlayer.seekTo(pos.toInt())

            val state = if (mediaSessionCompat.controller.playbackState.state == STATE_PLAYING){
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

            mediaSessionCompat.setPlaybackState(state)
        }

        override fun onRewind() {
            super.onRewind()
            onSeekTo(mediaSessionCompat.controller.playbackState.position - 10_000)
        }

        override fun onFastForward() {
            super.onFastForward()
            onSeekTo(mediaSessionCompat.controller.playbackState.position + 30_000)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            mediaSessionCompat.setRepeatMode(repeatMode)
            mRepeatMode = repeatMode
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)
            lifecycleScope.launch(Dispatchers.IO){
                mediaManager.shuffleGlobalPlaylistAndActiveItem()
            }
        }

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            super.onCommand(command, extras, cb)
            if (command == PLAY_SELECTED) {
                lifecycleScope.launch(Dispatchers.IO){
                    val item = getActiveOnce()
                    val playlist = getPlaylistOnce()
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
                    }
                }
            }
        }

        fun onMediaChanged(item: MediaItem, currentItemPos: Long, isPlayingNew: Boolean){
            if (isPlayingNew){
                onPlayFromUri(item.uri, null)
            }

            setActiveMedia(currentItemPos, item.id)
            setSessionMetadata(item.title, item.id, item.albumArtUri.toString())
            mediaSessionCompat.setRepeatMode(mRepeatMode)
        }

        override fun onStop() {
            super.onStop()
            endProgressBroadCastLoop()
            mediaPlayer.release()
        }
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

    private fun setSessionMetadata(title: String, id: Long, artUri: String){
        val metaData = metadataBuilder
            .putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                title
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                artUri
            )
            .build()

        mediaSessionCompat.setMetadata(metaData)
    }

    private fun setActiveMedia(playlistPos: Long, id: Long){
        lifecycleScope.launch(Dispatchers.IO) {
            activeMediaItemRepository.insert(
                ActiveMediaItem(
                    globalPlaylistPosition = playlistPos,
                    mediaItemId = id
                )
            )
        }
    }

}

const val PLAY_SELECTED = "PLAY_SELECTED"
const val CUSTOM_MEDIA_ID = "custom.media.id"