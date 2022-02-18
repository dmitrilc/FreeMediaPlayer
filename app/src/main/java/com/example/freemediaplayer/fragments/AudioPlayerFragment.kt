package com.example.freemediaplayer.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.freemediaplayer.R
import com.example.freemediaplayer.databinding.FragmentAudioPlayerBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.viewmodel.AudioPlayerViewModel
import com.example.freemediaplayer.viewmodel.AudiosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "AUDIO_PLAYER_FRAGMENT"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AudioPlayerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val args: AudioPlayerFragmentArgs by navArgs()

    private val audiosViewModel: AudiosViewModel by activityViewModels()
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModels()

    private var mediaPlayer: MediaPlayer? = null

    //TODO Remove if not needed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)

        audiosViewModel.activeAudio.observe(viewLifecycleOwner){ audio ->
            if (mediaPlayer === null){
                createNewPlayer(audio)
            } else {
                updateOldPlayer(audio)
            }

            postNewPlayerMaxToViewModel()
            syncTitleToPlayer(audio)
            setThumbnail(audio)
        }

        audiosViewModel.currentPlaylist.observe(viewLifecycleOwner){
            audiosViewModel.postActiveAudio(args.activeAudio)
        }

        audiosViewModel.allAudiosLiveData.value?.let {
            audiosViewModel.postCurrentPlaylist(it, args.folderLocation)
        }

        return binding.root
    }

    private fun updateOldPlayer(audio: Audio){
        context?.let { context ->
            mediaPlayer?.let { player ->
                player.reset()
                player.setDataSource(context, audio.uri)
                player.prepare()
                player.start()
                syncPlayerToGlobalLooping()
            }
        }
    }

    private fun createNewPlayer(audio: Audio){
        context?.let {
            mediaPlayer = MediaPlayer.create(it, audio.uri)
        }

        mediaPlayer?.let {
            it.start()
            syncPlayerPositionToViewModel()
            syncSeekBarToPlayer()
            syncPlayPauseButtonToPlayer()
            syncReplayButtonToPlayer()
            syncReplay10ButtonToPlayer()
            syncForward30ButtonToPlayer()

            prepShuffleButton()
            prepSeekPreviousButton()
            prepSeekNextButton()

            prepPlaylistButton()
            setOnCompletionListener()
        }
    }

    private fun syncTitleToPlayer(audio: Audio){
        binding.textViewAudioPlayerTitle.text = audio.title
    }

    private fun setThumbnail(audio: Audio){
        val thumbnail = audiosViewModel.loadedThumbnails.value?.get(audio.album)
        binding.imageViewAudioPlayerDisplayArt.setImageBitmap(thumbnail)
    }

    private fun prepSeekNextButton(){
        binding.imageButtonAudioPlayerSeekForward.setOnClickListener {
            //TODO Improve readability
            if (audiosViewModel.activeAudio.value === audiosViewModel.currentPlaylist.value?.last()){
                audiosViewModel.postActiveAudio(0)
            } else {
                val currentAudioIndex = audiosViewModel.currentPlaylist.value?.indexOf(audiosViewModel.activeAudio.value)
                if (currentAudioIndex != null) {
                    audiosViewModel.postActiveAudio(currentAudioIndex + 1)
                }
            }
        }
    }

    //TODO Remove duplicate code from this and seeknext
    private fun prepSeekPreviousButton(){
        binding.imageButtonAudioPlayerSeekBackward.setOnClickListener {
            if (audiosViewModel.activeAudio.value === audiosViewModel.currentPlaylist.value?.first()){
                audiosViewModel.currentPlaylist.value?.lastIndex?.let {
                    audiosViewModel.postActiveAudio(it)
                }
            } else {
                val currentAudioIndex = audiosViewModel.currentPlaylist.value?.indexOf(audiosViewModel.activeAudio.value)
                if (currentAudioIndex != null) {
                    audiosViewModel.postActiveAudio(currentAudioIndex - 1)
                }
            }
        }
    }

    private fun prepShuffleButton(){
        //TODO Test if need to post value to livedata
        binding.imageButtonAudioPlayerShuffle.setOnClickListener {
            audiosViewModel.currentPlaylist.value?.shuffled()
        }
    }

    private fun prepPlaylistButton() {
        val navController = findNavController()

        binding.imageButtonAudioPlayerPlaylist.setOnClickListener {
            navController.navigate(AudioPlayerFragmentDirections.actionAudioPlayerPathToActivePlaylistPath())
        }
    }

    private fun postNewPlayerMaxToViewModel(){
        mediaPlayer?.let {
            audioPlayerViewModel.maxPlayerDuration.postValue(it.duration)
        }
    }

    private fun syncPlayerPositionToViewModel() {
        lifecycleScope.launch {
            while (mediaPlayer !== null) {
                mediaPlayer?.let {
                    try {
                        if (it.isPlaying) {
                            audioPlayerViewModel.playerPosition.postValue(it.currentPosition)
                        }
                    } catch (e: IllegalStateException) {
                        //TODO Implement
                    }
                }
                delay(1000)
            }
        }
    }

    private fun syncSeekBarToPlayer() {
        audioPlayerViewModel.playerPosition.observe(viewLifecycleOwner){
            binding.seekBarAudioPlayerSeekBar.progress = it
        }

        audioPlayerViewModel.maxPlayerDuration.observe(viewLifecycleOwner){
            binding.seekBarAudioPlayerSeekBar.max = it
        }

        val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        binding.seekBarAudioPlayerSeekBar.setOnSeekBarChangeListener(seekBarListener)
    }

    private fun syncPlayPauseButtonToPlayer() {
        audioPlayerViewModel.isPlaying.observe(viewLifecycleOwner){ isPlaying ->
            if (isPlaying){
                mediaPlayer?.start()
                binding.imageButtonAudioPlayerPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
            } else {
                mediaPlayer?.pause()
                binding.imageButtonAudioPlayerPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }

        binding.imageButtonAudioPlayerPlayPause.setOnClickListener {
            val inverse = audioPlayerViewModel.isPlaying.value?.not()
            audioPlayerViewModel.isPlaying.postValue(inverse)
        }
    }

    private fun setOnCompletionListener(){
        mediaPlayer?.setOnCompletionListener {
            //TODO Handle cases like looping/playlist next
            if(audioPlayerViewModel.isGlobalSelfLooping.value == false){
                val inverse = audioPlayerViewModel.isPlaying.value?.not()
                audioPlayerViewModel.isPlaying.postValue(inverse)
            }
        }
    }

    //Because reset clears looping value
    private fun syncPlayerToGlobalLooping(){
        audioPlayerViewModel.isGlobalSelfLooping.postValue(audioPlayerViewModel.isGlobalSelfLooping.value)
    }

    private fun syncReplayButtonToPlayer() {
        audioPlayerViewModel.isGlobalSelfLooping.observe(viewLifecycleOwner) { isLooping ->
            if (isLooping) {
                binding.imageButtonAudioPlayerReplayInfinite.setImageResource(R.drawable.ic_baseline_repeat_one_24)
            } else {
                binding.imageButtonAudioPlayerReplayInfinite.setImageResource(R.drawable.ic_baseline_repeat_24)
            }

            mediaPlayer?.isLooping = isLooping
        }

        binding.imageButtonAudioPlayerReplayInfinite.setOnClickListener {
            val inverse = audioPlayerViewModel.isGlobalSelfLooping.value?.not()
            audioPlayerViewModel.isGlobalSelfLooping.postValue(inverse)
        }
    }

    //Also syncs to ProgressBar
    private fun syncReplay10ButtonToPlayer() {
        binding.imageButtonAudioPlayerReplay10.setOnClickListener {
            mediaPlayer?.let { player ->
                player.seekTo(player.currentPosition - 10_000)
                binding.seekBarAudioPlayerSeekBar.progress = player.currentPosition
            }
        }
    }

    //Also syncs to ProgressBar
    private fun syncForward30ButtonToPlayer() {
        binding.imageButtonAudioPlayerForward30.setOnClickListener {
            mediaPlayer?.let { player ->
                player.seekTo(player.currentPosition + 30_000)
                binding.seekBarAudioPlayerSeekBar.progress = player.currentPosition
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AudioPlayerFragment.
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

    override fun onStop() {
        super.onStop()

        mediaPlayer?.release()
        mediaPlayer = null
    }
}