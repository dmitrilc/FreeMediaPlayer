package com.example.freemediaplayer.service

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import androidx.lifecycle.*
import androidx.media.MediaBrowserServiceCompat
import androidx.room.withTransaction
import com.example.freemediaplayer.entities.ActiveMediaItem
import com.example.freemediaplayer.entities.GlobalPlaylistItem
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import javax.inject.Inject

private const val TAG = "PLAYER_SERVICE"

@AndroidEntryPoint
class AudioPlayerService : LifecycleOwner, MediaBrowserServiceCompat() {

    private val mLifecycleDispatcher = ServiceLifecycleDispatcher(this)

    @Inject
    lateinit var appDb: AppDatabase

    private val stateBuilder = PlaybackStateCompat.Builder()
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val mediaDescBuilder = MediaDescriptionCompat.Builder()
    //private var mediaPlayer: MediaPlayer? = null


    private val mediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(applicationContext, TAG)
    }
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    private val mPlaylist by lazy {
        appDb.globalPlaylistDao().getGlobalPlaylist()
    }

    private val mActiveItem by lazy {
        appDb.activeMediaItemDao().getMediaItemLiveData()
    }

    override fun onBind(intent: Intent?): IBinder? {
        mLifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        mLifecycleDispatcher.onServicePreSuperOnCreate()

        bindPlayerUiChangeListeners()
        bindPlayerCompletionListener()

        mediaSessionCompat.apply {
            setPlaybackState(getInitialState())
            setCallback(audioMediaSessionCallback)
            isActive = true
        }

        sessionToken = mediaSessionCompat.sessionToken
    }

    private fun getInitialState(): PlaybackStateCompat { //TODO Remove uneeded states
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
        //TODO Update PlayerUI only
        mediaPlayer.setOnCompletionListener {
            when(mediaSessionCompat.controller.repeatMode){
                REPEAT_MODE_ONE -> {
                    lifecycleScope.launch(Dispatchers.IO){
                        val uri = appDb.activeMediaItemDao().getMediaItemOnce().uri
                        audioMediaSessionCallback.onPlayFromUri(uri, null)
                    }
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

    private fun bindPlayerUiChangeListeners() {
/*        appDb.activeMediaItemDao().getObservable().observe(this) {
            val desc = mediaDescBuilder
                .setMediaId(it.mediaItem.id.toString())
                .setMediaUri(it.mediaItem.uri)
                .setTitle(it.mediaItem.title)
                .build()


        }*/

        mPlaylist.observe(this){}
        mActiveItem.observe(this){}
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLifecycleDispatcher.onServicePreSuperOnStart()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun getLifecycle(): Lifecycle = mLifecycleDispatcher.lifecycle

    override fun onDestroy() {
        super.onDestroy()
        mLifecycleDispatcher.onServicePreSuperOnDestroy()

        mediaPlayer.release()
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

        private fun endProgressBroadCastLoop(){
            syncJob?.cancel()
            syncJob = null

/*            val state = mediaPlayer?.currentPosition?.let {
                stateBuilder
                    .setState(
                        STATE_PAUSED,
                        it.toLong(),
                        1.0f
                    )
                    .build()
            }

            mediaSessionCompat.setPlaybackState(state)*/
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

            setActiveMediaItem(uri)
        }

        private fun setActiveMediaItem(uri: Uri){
            lifecycleScope.launch(Dispatchers.IO){
                appDb.withTransaction {
                    val playlist = getPlaylistOnce()
                    val item = playlist.find {
                        it.uri == uri
                    }

                    appDb.activeMediaItemDao().insert(
                        ActiveMediaItem(
                            globalPlaylistPosition = playlist.indexOf(item).toLong(),
                            mediaItemId = item!!.id
                        )
                    )

                    val metaData = metadataBuilder
                        .putString(
                            MediaMetadataCompat.METADATA_KEY_TITLE,
                            item.title
                        )
                        .putString(
                            MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                            item.albumArtUri
                        )
                        .build()

                    mediaSessionCompat.setMetadata(metaData)
                }
            }
        }

        override fun onSkipToQueueItem(id: Long) {
            super.onSkipToQueueItem(id)
            val nextItem = mPlaylist.value!![id.toInt()]

            onPlayFromUri(nextItem.uri, null)
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            val currentItemIndex = mPlaylist.value!!.indexOf(mActiveItem.value)

            val nextIndex = if (currentItemIndex == mPlaylist.value!!.lastIndex){
                0
            } else {
                currentItemIndex + 1
            }

            onSkipToQueueItem(nextIndex.toLong())

            //Log.d(TAG, "Received command to skip to next")

            //val currentMediaPos = playlist.indexOf()
            //val nextItem: MediaSessionCompat.QueueItem

/*            activeQueueItem = if (activeQueueItem === playlist.last()) {
                playlist.first()
            } else {
                val currentAudioIndex = playlist.indexOf(activeQueueItem)
                playlist[currentAudioIndex + 1]
            }
            //TODO Handle playlist repeat mode

            onPlayFromUri(activeQueueItem?.description?.mediaUri, Bundle())*/

//                if (mediaItemsViewModel.activeMedia.value === mediaItemsViewModel.globalPlaylist.last()){
//                    mediaItemsViewModel.activeMedia.postValue(mediaItemsViewModel.globalPlaylist.first())
//                } else {
//                    val currentAudioIndex = mediaItemsViewModel.globalPlaylist.indexOf(mediaItemsViewModel.activeMedia.value)
//                    mediaItemsViewModel.activeMedia
//                        .postValue(mediaItemsViewModel.globalPlaylist[currentAudioIndex + 1])
//                }
            //TODO Handle playlist repeat mode
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()

            val currentItemIndex = mPlaylist.value!!.indexOf(mActiveItem.value)

            val nextIndex = if (currentItemIndex == 0){
                mPlaylist.value!!.lastIndex
            } else {
                currentItemIndex - 1
            }

            onSkipToQueueItem(nextIndex.toLong())

//                if (mediaItemsViewModel.activeMedia.value === mediaItemsViewModel.globalPlaylist.first()){
//                    mediaItemsViewModel.globalPlaylist.lastIndex.let {
//                        mediaItemsViewModel.activeMedia.postValue(mediaItemsViewModel.globalPlaylist[it])
//                    }
//                } else {
//                    val currentAudioIndex = mediaItemsViewModel.globalPlaylist.indexOf(mediaItemsViewModel.activeMedia.value)
//                    mediaItemsViewModel.activeMedia
//                        .postValue(mediaItemsViewModel.globalPlaylist[currentAudioIndex - 1])
//                }

/*            activeQueueItem = if (activeQueueItem === playlist.first()) {
                playlist.last()
            } else {
                val currentAudioIndex = playlist.indexOf(activeQueueItem)
                playlist[currentAudioIndex - 1]
            }
            //TODO Handle playlist repeat mode

            onPlayFromUri(activeQueueItem?.description?.mediaUri, Bundle())*/
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            if (mediaSessionCompat.controller.playbackState.state == STATE_PLAYING){
                mediaPlayer.seekTo(pos.toInt())

                val state = stateBuilder
                    .setState(
                        STATE_PLAYING,
                        pos,
                        1.0f
                    )
                    .build()

                mediaSessionCompat.setPlaybackState(state)
            } else {
                val state = stateBuilder
                    .setState(
                        STATE_PAUSED,
                        pos,
                        1.0f
                    )
                    .build()

                mediaSessionCompat.setPlaybackState(state)
            }
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
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)

            val shuffled = mPlaylist.value!!.shuffled().mapIndexed { index, item ->
                GlobalPlaylistItem(
                    mId = index.toLong(),
                    mediaItemId = item.id
                )
            }

            lifecycleScope.launch(Dispatchers.IO){
                appDb.globalPlaylistDao().replacePlaylist(shuffled)
            }
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            super.onAddQueueItem(description)

/*            description?.mediaId?.toLong()?.let {
                playlist.add(
                    MediaSessionCompat.QueueItem(description, it)
                )
            }*/
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            super.onRemoveQueueItem(description)

/*            description?.mediaId?.toLong()?.let {
                playlist.remove(
                    MediaSessionCompat.QueueItem(description, it)
                )
            }*/
        }

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            super.onCommand(command, extras, cb)
            if (command == PLAY_SELECTED){
                onPlayFromUri(mActiveItem.value!!.uri, null)
            }
        }

        override fun onStop() {
            super.onStop()
            endProgressBroadCastLoop()
        }
    }

    private suspend fun getMediaItemOnce() = appDb.activeMediaItemDao().getMediaItemOnce()
    private suspend fun getPlaylistOnce() = appDb.globalPlaylistDao().getOnce()

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
        //TODO Filter out who can access

        return MediaBrowserServiceCompat.BrowserRoot("MY_MEDIA_ROOT_ID", null)

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

        result.sendResult(mediaItems)
    }

}

const val PLAY_SELECTED = "PLAY_SELECTED"