package com.dimitrilc.freemediaplayer.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.RemoteViews
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.media.session.MediaButtonReceiver
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByUriUseCase
import com.dimitrilc.freemediaplayer.service.PREFERENCES_KEY_ALBUM
import com.dimitrilc.freemediaplayer.service.PREFERENCES_KEY_ART_URI
import com.dimitrilc.freemediaplayer.service.PREFERENCES_KEY_STATE_PLAYING
import com.dimitrilc.freemediaplayer.service.PREFERENCES_KEY_TITLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widgetData")
private const val TAG = "MEDIA_CONTROL_WIDGET"

@AndroidEntryPoint
class MediaControlWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var getThumbByUriUseCase: GetThumbByUriUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetIds: IntArray? = intent?.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)

        if (appWidgetIds == null || appWidgetIds.isEmpty()) {
            context?.let {
                val componentName = ComponentName(
                    context,
                    MediaControlWidgetProvider::class.java
                )

                val appWidgetManager = AppWidgetManager.getInstance(context)

                //Calls super for Hilt to perform injections
                super.onReceive(context, intent)

                onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName))
            }
        } else {
            super.onReceive(context, intent)
        }
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        context?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val preferences = context.dataStore.data.first()
                val title: String = preferences[PREFERENCES_KEY_TITLE] ?: "No Title"
                val album: String = preferences[PREFERENCES_KEY_ALBUM] ?: "No Album"
                val isPlaying: Boolean = preferences[PREFERENCES_KEY_STATE_PLAYING] ?: false
                val artUri: String? = preferences[PREFERENCES_KEY_ART_URI]
                val thumb = getThumbByUriUseCase(artUri, null)

                val remoteViews = RemoteViews(
                    context.packageName,
                    R.layout.widget_view
                ).apply {
                    setTextViewText(
                        R.id.textView_widgetTitle,
                        title
                    )

                    setTextViewText(
                        R.id.textView_widgetAlbum,
                        album
                    )

                    setImageViewResource(
                        R.id.imageButton_widgetPlayPause,
                        if (isPlaying) R.drawable.ic_baseline_pause_24
                        else R.drawable.ic_baseline_play_arrow_24
                    )

                    setImageViewBitmap(
                        R.id.imageView_widgetAlbumArt,
                        thumb
                    )
                }

                bindOnClickIntents(remoteViews, context)

                appWidgetManager?.updateAppWidget(appWidgetIds, remoteViews)
            }
        }
    }

    private fun bindOnClickIntents(remoteViews: RemoteViews, context: Context){
        remoteViews.setOnClickPendingIntent(
            R.id.imageButton_widgetPlayPause,
            createPauseIntent(context)
        )

        remoteViews.setOnClickPendingIntent(
            R.id.imageButton_widgetSeekPrevious,
            createSkipToPreviousIntent(context)
        )

        remoteViews.setOnClickPendingIntent(
            R.id.imageButton_widgetSeekNext,
            createSkipToNextIntent(context)
        )
    }

    private fun createPauseIntent(context: Context): PendingIntent {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_PLAY_PAUSE
        )
    }

    private fun createSkipToPreviousIntent(context: Context): PendingIntent {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
    }

    private fun createSkipToNextIntent(context: Context): PendingIntent {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    }
}