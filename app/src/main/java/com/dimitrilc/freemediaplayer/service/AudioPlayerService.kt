package com.dimitrilc.freemediaplayer.service

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.appwidget.AppWidgetManager.*
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.support.v4.media.session.PlaybackStateCompat.Builder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.*
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.domain.activemedia.GetActiveMediaObservableUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.InsertActiveMediaUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.UpdateActiveMediaPlaylistPositionAndMediaIdUseCase
import com.dimitrilc.freemediaplayer.domain.controls.ShuffleUseCase
import com.dimitrilc.freemediaplayer.domain.controls.SkipToNextUseCase
import com.dimitrilc.freemediaplayer.domain.controls.SkipToPreviousUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemObservableUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemOnceUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistOnceUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByMediaIdUseCase
import com.dimitrilc.freemediaplayer.domain.worker.GetUpdateActiveMediaWorkerInfoObservableUseCase
import com.dimitrilc.freemediaplayer.hilt.FmpApplication
import com.dimitrilc.freemediaplayer.ui.activities.AUDIO_CONTROLS_NOTIFICATION_CHANNEL_ID
import com.dimitrilc.freemediaplayer.ui.activities.MainActivity
import com.dimitrilc.freemediaplayer.ui.widget.MediaControlWidgetProvider
import com.dimitrilc.freemediaplayer.ui.widget.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject
import androidx.media.app.NotificationCompat as MediaNotificationCompat

private const val TAG = "AUDIO_PLAYER_SERVICE"
private const val CONTROLS_NOTIFICATION_ID = 1
const val MISC_NOTIFICATION_ID = 2
const val METADATA_KEY_ID = "0"
const val COMMAND_RECONNECT = "1"
private const val COMMAND_REPEAT = "2"
const val METADATA_KEY_BITMAP = "METADATA_KEY_BITMAP"
val PREFERENCES_KEY_TITLE = stringPreferencesKey(METADATA_KEY_TITLE)
val PREFERENCES_KEY_ALBUM = stringPreferencesKey(METADATA_KEY_ALBUM)
val PREFERENCES_KEY_STATE_PLAYING = booleanPreferencesKey("$STATE_PLAYING")
val PREFERENCES_KEY_ART_URI = stringPreferencesKey(METADATA_KEY_ALBUM_ART_URI)

@AndroidEntryPoint
class AudioPlayerService : LifecycleOwner, MediaBrowserServiceCompat() {

    private val mLifecycleDispatcher = ServiceLifecycleDispatcher(this)

    @Inject
    lateinit var getUpdateActiveMediaWorkerInfoObservableUseCase: GetUpdateActiveMediaWorkerInfoObservableUseCase

    @Inject
    lateinit var insertActiveMediaUseCase: InsertActiveMediaUseCase

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
    lateinit var shuffleUseCase: ShuffleUseCase

    @Inject
    lateinit var getActiveMediaItemObservableUseCase: GetActiveMediaItemObservableUseCase

    @Inject
    lateinit var getThumbByMediaIdUseCase: GetThumbByMediaIdUseCase

    @Inject
    lateinit var fmpApp: FmpApplication

    private val stateBuilder = Builder()
    private val metadataBuilder = MediaMetadataCompat.Builder()

    private val mediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(applicationContext, TAG)
    }

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    private val activeMediaItem by lazy {
        getActiveMediaItemObservableUseCase()
            .distinctUntilChanged() //This still emits null if ActiveMedia is removed from the db
    }

    private val skipToPreviousAction by lazy {
        //Creates intent and notification action to skip to previous item
        val skipToPreviousIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            applicationContext,
            ACTION_SKIP_TO_PREVIOUS
        )

        NotificationCompat.Action.Builder(
            R.drawable.ic_baseline_skip_previous_24,
            "Skip to Previous",
            skipToPreviousIntent
        ).build()
    }

    private val skipToNextAction by lazy {
        //Creates intent and notification action to skip to next item
        val skipToNextIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            applicationContext,
            ACTION_SKIP_TO_NEXT
        )

        NotificationCompat.Action.Builder(
            R.drawable.ic_baseline_skip_next_24,
            "Skip to Next",
            skipToNextIntent
        ).build()
    }

    private val pauseAction by lazy {
        //Creates intent and notification action to pause
        val pauseIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            applicationContext,
            ACTION_PAUSE
        )

        NotificationCompat.Action.Builder(
            R.drawable.ic_baseline_pause_24,
            "Pause",
            pauseIntent
        ).build()
    }

    private val playAction by lazy {
        //Creates intent and notification action to play
        val playIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            applicationContext,
            ACTION_PLAY
        )

        NotificationCompat.Action.Builder(
            R.drawable.ic_baseline_play_arrow_24,
            "Play",
            playIntent
        ).build()
    }

    private val notificationBuilderLiveData = MutableLiveData<NotificationCompat.Builder>()

    private val widgetUpdateIntent by lazy {
        Intent(this, MediaControlWidgetProvider::class.java).apply {
            action = ACTION_APPWIDGET_UPDATE
        }
    }

    private val mediaStyle by lazy {
        MediaNotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(mediaSessionCompat.sessionToken)
    }

    private val audioMediaSessionCallback = object : MediaSessionCompat.Callback() {
        var syncJob: Job? = null

        private fun startProgressBroadCastLoop() {
            syncJob = lifecycleScope.launch {
                while (mediaSessionCompat.isActive && isActive) {
                    try {
                        if (mediaPlayer.isPlaying){
                            val state = stateBuilder.setState(
                                STATE_PLAYING,
                                mediaPlayer.currentPosition.toLong(),
                                1.0f
                            ).build()

                            mediaSessionCompat.setPlaybackState(state)
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
            endProgressBroadCastLoop()
            onSeekTo(0)
            onPlay()
        }

        override fun onPlay() {
            mediaPlayer.start()
            startProgressBroadCastLoop()
            updateNotification(true)
        }

        override fun onPause() {
            mediaPlayer.pause()
            endProgressBroadCastLoop()

            val state = stateBuilder.setState(
                STATE_PAUSED,
                mediaPlayer.currentPosition.toLong(),
                1.0f
            ).build()

            mediaSessionCompat.setPlaybackState(state)
            updateNotification(false)
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

            val state = stateBuilder.setState(
                STATE_BUFFERING,
                pos,
                1.0f
            ).build()

            mediaSessionCompat.setPlaybackState(state)
        }

        override fun onRewind() {
            onSeekTo(mediaPlayer.currentPosition.toLong() - 10_000)
        }

        override fun onFastForward() {
            onSeekTo(mediaPlayer.currentPosition.toLong() + 30_000)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            when(mediaSessionCompat.controller.repeatMode){
                REPEAT_MODE_NONE -> {
                    mediaSessionCompat.setRepeatMode(REPEAT_MODE_ONE)
                }
                REPEAT_MODE_ONE -> {
                    mediaSessionCompat.setRepeatMode(REPEAT_MODE_NONE)
                }
            }
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            shuffleUseCase()
        }

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            when(command){
                COMMAND_RECONNECT -> {
                    mediaSessionCompat.setMetadata(mediaSessionCompat.controller.metadata)
                    mediaSessionCompat.setPlaybackState(mediaSessionCompat.controller.playbackState)
                }
                COMMAND_REPEAT -> {
                    repeat()
                }
            }
        }

        override fun onStop() {
            super.onStop()
            endProgressBroadCastLoop()
            mediaPlayer.release()
            mediaSessionCompat.release()
            fmpApp.audioSession = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        mLifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    override fun onCreate() {
        mLifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()

        createMediaControlsNotification()

        bindPlayerCompletionListener()
        bindPlayerOnPreparedListener()

        mediaSessionCompat.apply {
            setPlaybackState(getInitialState())
            setCallback(audioMediaSessionCallback)
            isActive = true
        }

        sessionToken = mediaSessionCompat.sessionToken
        fmpApp.audioSession = mediaSessionCompat

        listenForActiveMedia()
        listenForNotificationUpdates()
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
            when(mediaSessionCompat.controller.repeatMode){
                REPEAT_MODE_ONE -> {
                    mediaSessionCompat.controller.sendCommand(COMMAND_REPEAT, null, null)
                }
                else -> {
                    mediaSessionCompat.controller.transportControls.skipToNext()
                }
            }
        }
    }

    private fun bindPlayerOnPreparedListener(){
        val listener = MediaPlayer.OnPreparedListener { player ->
            lifecycleScope.launch {
                val thumbnail = withContext(Dispatchers.Default) {
                    activeMediaItem.value?.let {
                        getThumbByMediaIdUseCase(it.mediaItemId)
                    }
                }

                val metadata = metadataBuilder
                    .putLong(
                        METADATA_KEY_ID,
                        activeMediaItem.value!!.mediaItemId
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_TITLE,
                        activeMediaItem.value?.title
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        activeMediaItem.value?.album
                    )
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        player.duration.toLong()
                    )
                    .putBitmap(
                        METADATA_KEY_BITMAP,
                        thumbnail
                    )
                    .build()

                mediaSessionCompat.setMetadata(metadata)

                updateNotification(true)

                mediaSessionCompat.controller.transportControls.play()
            }
        }

        mediaPlayer.setOnPreparedListener(listener)
    }

    private fun updateNotification(isPlaying: Boolean){
        lifecycleScope.launch {
            val notificationTitle = activeMediaItem.value?.title
            val notificationContent = activeMediaItem.value?.album
            val thumbnail = mediaSessionCompat.controller.metadata.getBitmap(METADATA_KEY_BITMAP)

            val builder = getNotificationBuilder()
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setLargeIcon(thumbnail)
                .addAction(skipToPreviousAction)
                .addAction(if (isPlaying) pauseAction else playAction)
                .addAction(skipToNextAction)
                .setStyle(mediaStyle)

            notificationBuilderLiveData.value = builder
        }
    }

    //Media Button events always start this service
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLifecycleDispatcher.onServicePreSuperOnStart()

        if (activeMediaItem.value?.isAudio == true
            && fmpApp.audioBrowser != null
        ){
            //Handling intents from notification controls
            MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)
        } else {
            onDestroy()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createMediaControlsNotification(){
        startForeground(
            CONTROLS_NOTIFICATION_ID,
            getNotificationBuilder().build()
        )
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        //Creates intent to open app
        val intent = Intent(this, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (FLAG_ONE_SHOT or FLAG_IMMUTABLE)
        } else {
            FLAG_ONE_SHOT
        }

        val openAppIntent = PendingIntent.getActivity(this, 0, intent, flags)

        //To be used to update metadata only
        return NotificationCompat
            .Builder(
                applicationContext,
                AUDIO_CONTROLS_NOTIFICATION_CHANNEL_ID
            ).setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openAppIntent)
    }

    private fun listenForActiveMedia(){
        activeMediaItem.observe(this){
            if (it != null && it.isAudio){
                if (mediaSessionCompat.controller.metadata?.getLong(METADATA_KEY_ID) != it.mediaItemId){
                    mediaSessionCompat.controller.transportControls.playFromUri(it.uri, null)
                }
            }
        }
    }

    private fun listenForNotificationUpdates(){
        notificationBuilderLiveData.observe(this){
            if (it != null){
                with(NotificationManagerCompat.from(baseContext)) {
                    notify(CONTROLS_NOTIFICATION_ID, it.build())
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    applicationContext.dataStore.edit { preferences ->
                        preferences[PREFERENCES_KEY_STATE_PLAYING] = mediaSessionCompat.controller.playbackState.state == STATE_PLAYING
                        activeMediaItem.value?.albumArtUri?.let { artUri ->
                            preferences[PREFERENCES_KEY_ART_URI] = artUri
                        }
                        activeMediaItem.value?.let { item ->
                            item.albumArtUri?.let { artUri ->
                                preferences[PREFERENCES_KEY_ART_URI] = artUri
                            }
                            preferences[PREFERENCES_KEY_TITLE] = item.title
                            preferences[PREFERENCES_KEY_ALBUM] = item.album
                        }
                    }

                    baseContext.sendBroadcast(widgetUpdateIntent)
                }
            }
        }
    }

    override fun getLifecycle(): Lifecycle = mLifecycleDispatcher.lifecycle

    override fun onDestroy() {
        mLifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()

        stopForeground(true)
        mediaSessionCompat.controller.transportControls.stop()
        fmpApp.audioBrowser = null
        stopSelf()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
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

        return BrowserRoot("MY_MEDIA_ROOT_ID", null)

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

        result.sendResult(mediaItems)
    }
}