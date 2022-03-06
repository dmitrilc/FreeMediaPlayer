package com.example.freemediaplayer.service

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.media.MediaBrowserServiceCompat
import com.example.freemediaplayer.viewmodel.MediaItemsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "PLAYER_SERVICE"

class AudioPlayerService : LifecycleOwner, MediaBrowserServiceCompat() {

    private var mediaSessionCompat: MediaSessionCompat? = null
    private val stateBuilder = PlaybackStateCompat.Builder()
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val mediaDescBuilder = MediaDescriptionCompat.Builder()
    private var mediaPlayer: MediaPlayer? = null
    private val playlist = mutableListOf<MediaSessionCompat.QueueItem>()
    private var activeQueueItem: MediaSessionCompat.QueueItem? = null

    override fun onCreate() {
        super.onCreate()

        // Create a MediaSessionCompat
        mediaSessionCompat = MediaSessionCompat(applicationContext, TAG).apply {
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

            val audioMediaSessionCallback = object : MediaSessionCompat.Callback() {
                var syncJob: Job? = null

                private fun startProgressBroadCastLoop() {
                    val listener = MediaPlayer.OnPreparedListener {
                        val metaData = metadataBuilder
                            .putLong(
                                MediaMetadataCompat.METADATA_KEY_DURATION,
                                it.duration.toLong()
                            )
                            .build()

                        setMetadata(metaData)
                    }

                    mediaPlayer?.setOnPreparedListener(listener)

                    syncJob = lifecycleScope.launch {
                        mediaSessionCompat?.let { mediaSessionCompat ->
                            mediaPlayer?.let { mediaPlayer ->
                                while (mediaSessionCompat.isActive) {
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
                    }
                }

                private fun endProgressBroadCastLoop(){
                    val state = mediaPlayer?.currentPosition?.let {
                        stateBuilder
                            .setState(
                                STATE_PAUSED,
                                it.toLong(),
                                1.0f
                            )
                            .build()
                    }

                    setPlaybackState(state)

                    syncJob?.cancel()
                    syncJob = null
                }

                override fun onPlay() {
                    super.onPlay()

                    mediaPlayer?.let {
                        it.start()

                        val state = stateBuilder
                            .setState(
                                STATE_PLAYING,
                                it.currentPosition.toLong(),
                                1.0f
                            )
                            .build()

                        setPlaybackState(state)
                    }

                    startProgressBroadCastLoop()
                }

                override fun onPause() {
                    super.onPause()
                    mediaPlayer?.let {
                        it.pause()

                        val state = stateBuilder
                            .setState(
                                STATE_PAUSED,
                                it.currentPosition.toLong(),
                                1.0f
                            )
                            .build()

                        setPlaybackState(state)
                    }

                    endProgressBroadCastLoop()
                }

                override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
                    super.onPlayFromUri(uri, extras)

                    if (mediaPlayer == null){
                        mediaPlayer = MediaPlayer.create(
                            applicationContext,
                            uri
                        ).apply {
                            setOnCompletionListener {
                                if (controller.repeatMode == REPEAT_MODE_ONE) {
                                    controller.transportControls.seekTo(0)
                                    controller.transportControls.play()
                                } else {
                                    controller.transportControls.skipToNext()
                                }
                            }
                        }
                    } else {
                        mediaPlayer?.let {
                            it.reset()
                            if (uri != null) {
                                it.setDataSource(applicationContext, uri)
                            }
                            it.prepare()
                            //it.start()
                        }
                    }

                    activeQueueItem = playlist.find {
                        it.description.mediaUri == uri
                    }

                    val metaData = metadataBuilder
                        .putString(
                            MediaMetadataCompat.METADATA_KEY_TITLE,
                            activeQueueItem?.description?.title.toString()
                        )
                        .putString(
                            MediaMetadataCompat.METADATA_KEY_ALBUM,
                            activeQueueItem?.description?.description.toString() //album here
                        )
                        .build()

                    setMetadata(metaData)

                    endProgressBroadCastLoop()
                    onPlay()
                }

                override fun onSkipToQueueItem(id: Long) {
                    super.onSkipToQueueItem(id)
                }

                override fun onSkipToNext() {
                    super.onSkipToNext()

                    //Log.d(TAG, "Received command to skip to next")

                    //val currentMediaPos = playlist.indexOf()
                    //val nextItem: MediaSessionCompat.QueueItem

                    activeQueueItem = if (activeQueueItem === playlist.last()) {
                        playlist.first()
                    } else {
                        val currentAudioIndex = playlist.indexOf(activeQueueItem)
                        playlist[currentAudioIndex + 1]
                    }
                    //TODO Handle playlist repeat mode

                    onPlayFromUri(activeQueueItem?.description?.mediaUri, Bundle())

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

//                if (mediaItemsViewModel.activeMedia.value === mediaItemsViewModel.globalPlaylist.first()){
//                    mediaItemsViewModel.globalPlaylist.lastIndex.let {
//                        mediaItemsViewModel.activeMedia.postValue(mediaItemsViewModel.globalPlaylist[it])
//                    }
//                } else {
//                    val currentAudioIndex = mediaItemsViewModel.globalPlaylist.indexOf(mediaItemsViewModel.activeMedia.value)
//                    mediaItemsViewModel.activeMedia
//                        .postValue(mediaItemsViewModel.globalPlaylist[currentAudioIndex - 1])
//                }

                    activeQueueItem = if (activeQueueItem === playlist.first()) {
                        playlist.last()
                    } else {
                        val currentAudioIndex = playlist.indexOf(activeQueueItem)
                        playlist[currentAudioIndex - 1]
                    }
                    //TODO Handle playlist repeat mode

                    onPlayFromUri(activeQueueItem?.description?.mediaUri, Bundle())
                }

                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)

                    mediaPlayer?.let {
                        it.seekTo(pos.toInt())

                        val state = stateBuilder
                            .setState(
                                STATE_BUFFERING,
                                pos,
                                1.0f
                            )
                            .build()

                        setPlaybackState(state)
                    }
                }

                override fun onRewind() {
                    super.onRewind()

                    mediaPlayer?.let {
                        val newPos = it.currentPosition - 10_000

                        it.seekTo(newPos)

                        val state = stateBuilder
                            .setState(
                                STATE_REWINDING,
                                newPos.toLong(),
                                1.0f
                            )
                            .build()

                        setPlaybackState(state)
                    }
                }

                override fun onFastForward() {
                    super.onFastForward()
                    mediaPlayer?.let {
                        val newPos = it.currentPosition + 30_000

                        it.seekTo(newPos)

                        val state = stateBuilder
                            .setState(
                                STATE_FAST_FORWARDING,
                                newPos.toLong(),
                                1.0f
                            )
                            .build()

                        setPlaybackState(state)
                    }
                }

                override fun onSetRepeatMode(repeatMode: Int) {
                    super.onSetRepeatMode(repeatMode)
                    setRepeatMode(repeatMode)
                }

                override fun onSetShuffleMode(shuffleMode: Int) {
                    super.onSetShuffleMode(shuffleMode)

                    playlist.shuffle()
                }

                override fun onAddQueueItem(description: MediaDescriptionCompat?) {
                    super.onAddQueueItem(description)

                    description?.mediaId?.toLong()?.let {
                        playlist.add(
                            MediaSessionCompat.QueueItem(description, it)
                        )
                    }
                }

                override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
                    super.onRemoveQueueItem(description)

                    description?.mediaId?.toLong()?.let {
                        playlist.remove(
                            MediaSessionCompat.QueueItem(description, it)
                        )
                    }
                }

                override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
                    super.onCommand(command, extras, cb)

                    if (command == "Clear Queue"){
                        playlist.clear()
                    }
                }

                override fun onStop() {
                    super.onStop()
                    endProgressBroadCastLoop()
                }
            }

            //Setting callback
            setFlags(FLAG_HANDLES_QUEUE_COMMANDS)
            setPlaybackState(initialState)
            setQueue(playlist)
            setCallback(audioMediaSessionCallback)
            isActive = true
        }

        sessionToken = mediaSessionCompat?.sessionToken
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

    private fun onAudioPlayingCompleted(controller: MediaControllerCompat) {
        mediaPlayer?.setOnCompletionListener {
            if (controller.repeatMode == REPEAT_MODE_ONE) {
                controller.transportControls.seekTo(0)
                controller.transportControls.play()
            } else {
                controller.transportControls.skipToNext()
            }
        }
    }

    override fun getLifecycle(): Lifecycle = ServiceLifecycleDispatcher(this).lifecycle


    override fun onDestroy() {
        super.onDestroy()
        mediaSessionCompat?.release()
        mediaPlayer?.release()
        mediaSessionCompat = null
        mediaPlayer = null
        stopSelf()
    }

}