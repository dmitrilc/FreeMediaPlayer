package com.dimitrilc.freemediaplayer.ui.fragments.player

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.FragmentAudioPlayerBinding
import com.dimitrilc.freemediaplayer.hilt.FmpApplication
import com.dimitrilc.freemediaplayer.service.AudioPlayerService
import com.dimitrilc.freemediaplayer.service.COMMAND_RECONNECT
import com.dimitrilc.freemediaplayer.ui.viewmodel.player.AudioPlayerAction
import com.dimitrilc.freemediaplayer.ui.viewmodel.player.AudioPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "AUDIO_PLAYER_FRAG"

@AndroidEntryPoint
class AudioPlayerFragment : Fragment() {

    @Inject
    lateinit var fmpApp: FmpApplication

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val audioPlayerViewModel: AudioPlayerViewModel by viewModels()

    private val audioBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()

            setMediaController()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fmpApp.audioBrowser == null){
            createAudioBrowser()
        } else {
            setMediaController()
        }

        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = audioPlayerViewModel

        binding.seekBarPlayerSeekBar.setOnSeekBarChangeListener(
            audioPlayerViewModel.seekBarChangeListener
        )

        audioPlayerViewModel.uiState.observe(viewLifecycleOwner){
            if (it.thumbnail != null){
                binding.imageViewAlbumArt.setImageBitmap(it.thumbnail)
            } else {
                binding.imageViewAlbumArt.setImageResource(R.drawable.ic_baseline_music_note_24)
            }
        }

        audioPlayerViewModel.navigator = {
            findNavController().navigate(AudioPlayerFragmentDirections.actionAudioPlayerPathToActivePlaylistPath())
        }

        return binding.root
    }

    private fun createAudioBrowser() {
        requireContext().applicationContext.let {
            val audioBrowser = MediaBrowserCompat(
                it,
                ComponentName(it, AudioPlayerService::class.java),
                audioBrowserConnectionCallback,
                null // optional Bundle
            )

            (requireActivity().application as FmpApplication).audioBrowser = audioBrowser
        }

        connectAudioBrowser()
    }

    private fun connectAudioBrowser(){
        (requireActivity().application as FmpApplication).audioBrowser?.connect()
    }

    private fun setMediaController() {
        fmpApp.audioBrowser?.let {
            audioPlayerViewModel.controller = MediaControllerCompat(fmpApp, it.sessionToken)

            val controllerCallback = object : MediaControllerCompat.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                    audioPlayerViewModel.accept(AudioPlayerAction.ServiceAction.MetadataChanged(metadata))
                }

                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    audioPlayerViewModel.accept(AudioPlayerAction.ServiceAction.PlaybackStateChanged(state))
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    audioPlayerViewModel.accept(AudioPlayerAction.ServiceAction.SetRepeatMode(repeatMode))
                }
            }

            audioPlayerViewModel.controller?.registerCallback(controllerCallback)

            audioPlayerViewModel.controller?.sendCommand(COMMAND_RECONNECT, null, null)
        }
    }

}