package com.example.freemediaplayer.fragments.audio

import android.content.ComponentName
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.freemediaplayer.fragments.PlayerFragment
import com.example.freemediaplayer.service.AudioPlayerService

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "PLAYER_AUDIO"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//@AndroidEntryPoint
class AudioPlayerFragment : PlayerFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mediaDescBuilder = MediaDescriptionCompat.Builder()

    //Used for Audio player only
    //private var audioBrowser: MediaBrowserCompat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun adaptChildPlayer() {
        if (!isAudioBrowserActive()){
            createAudioBrowser()
        } else {
//            createAudioMediaControllerCompat()
//            addMediaControllerToContext()
//            syncAudioItemsToServiceQueue()
//            syncButtonsToController()
            audioBrowserConnectionCallback.onConnected()
        }
    }

    private fun isAudioBrowserActive() = mediaItemsViewModel.audioBrowser.value !== null

    private fun createAudioBrowser() {
        activity?.applicationContext?.let { context ->
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

    override fun onStop() {
        super.onStop()

        //mediaController.unregisterCallback(controllerCallback)
        //mediaBrowser?.disconnect()

        //If I want to stop the service as well
        //mediaControllerCompat?.transportControls?.stop()
        //mediaBrowser?.disconnect()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AudioPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val audioBrowserConnectionCallback = object : ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()

            createAudioMediaControllerCompat()
            addMediaControllerToContext()
            syncAudioItemsToServiceQueue()
            syncButtonsToController()
        }
    }

    private fun syncAudioItemsToServiceQueue() {
        mediaItemsViewModel.globalPlaylist.observe(viewLifecycleOwner) { playlist ->
            mediaControllerCompat?.let { controller ->
                mediaControllerCompat?.sendCommand("Clear Queue", Bundle(), ResultReceiver(null))

                playlist.forEach { audio ->
                    controller.addQueueItem(
                        mediaDescBuilder
                            .setMediaId(audio.id.toString())
                            .setMediaUri(audio.uri)
                            .setTitle(audio.title)
                            .setDescription(audio.album)
                            .build()
                    )
                }
            }

            syncActiveAudioToController()
        }
    }

    private fun syncActiveAudioToController(){
        val desc = mediaControllerCompat?.metadata?.description

        mediaItemsViewModel.activeMedia.value?.let { item ->
            if (desc?.title != item.title) {
                val bundle = bundleOf(
                    METADATA_KEY_MEDIA_URI to item.uri,
                    METADATA_KEY_TITLE to item.title
                )

                mediaControllerCompat?.transportControls?.playFromUri(item.uri, bundle)
            } else {
                mediaControllerCallbacks.onMetadataChanged(mediaControllerCompat?.metadata)
            }
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