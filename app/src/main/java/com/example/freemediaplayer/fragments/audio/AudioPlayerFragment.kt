package com.example.freemediaplayer.fragments.audio

import android.content.ComponentName
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.view.View
import com.example.freemediaplayer.fragments.PlayerFragment
import com.example.freemediaplayer.service.AudioPlayerService
import com.example.freemediaplayer.service.PLAY_SELECTED

private const val TAG = "PLAYER_AUDIO"

class AudioPlayerFragment : PlayerFragment() {

    override fun adaptChildPlayer() {
        binding.videoViewPlayer.visibility = View.INVISIBLE

        if (!isAudioBrowserActive()){
            createAudioBrowser()
        } else {
            createAudioMediaControllerCompat()
            addMediaControllerToContext()
            syncButtonsToController()
            audioBrowserConnectionCallback.onConnected()
        }
    }

    private fun playSelected(){
        mediaControllerCompat!!.sendCommand(PLAY_SELECTED, null, null)
    }

    private fun isAudioBrowserActive() = mediaItemsViewModel.audioBrowser.value !== null

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

            mediaItemsViewModel.audioBrowser.postValue(audioBrowser)
        }
    }

    private val audioBrowserConnectionCallback = object : ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()

            createAudioMediaControllerCompat()
            addMediaControllerToContext()
            syncButtonsToController()

            playSelected()
        }
    }

    private fun createAudioMediaControllerCompat() {
        mediaItemsViewModel.audioBrowser.value?.sessionToken?.let {
            mediaControllerCompat = MediaControllerCompat(
                context,
                it
            )
        }
    }

    private fun addMediaControllerToContext(){
        activity?.let {
            MediaControllerCompat.setMediaController(it, mediaControllerCompat)
        }
    }
}