package com.dimitrilc.freemediaplayer.ui.fragments.player

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.view.View
import com.dimitrilc.freemediaplayer.service.AudioPlayerService

private const val TAG = "PLAYER_AUDIO"

class AudioPlayerFragment : PlayerFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isAudioBrowserActive()){
            createAudioBrowser()
        } else {
            audioBrowserConnectionCallback.onConnected()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun isAudioBrowserActive() = appViewModel.audioBrowser.value !== null

    private fun createAudioBrowser() {
        requireActivity().applicationContext.let { context ->
            val audioBrowser = MediaBrowserCompat(
                context,
                ComponentName(context, AudioPlayerService::class.java),
                audioBrowserConnectionCallback,
                null // optional Bundle
            ).apply {
                connect()
            }

            appViewModel.audioBrowser.postValue(audioBrowser)
        }
    }

    private val audioBrowserConnectionCallback = object : ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()

            addMediaControllerToContext()
            syncButtonsToController()
        }
    }

    override fun getMediaController(): MediaControllerCompat {
        val token = appViewModel.audioBrowser.value!!.sessionToken
        return MediaControllerCompat(context, token)
    }

    private fun addMediaControllerToContext(){
        val activity = requireActivity()
        MediaControllerCompat.setMediaController(activity, mediaControllerCompat)
    }

    override fun isAudio() = true
}