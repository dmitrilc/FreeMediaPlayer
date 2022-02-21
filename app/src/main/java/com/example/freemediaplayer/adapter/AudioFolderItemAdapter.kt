package com.example.freemediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderItemViewBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.fragments.AudioFolderItemsFragment

private const val TAG = "FILE_ADAPTER"

//TODO Remove content resolver dep
class AudioFolderItemAdapter(private val dataSet: List<Audio>) :
    RecyclerView.Adapter<AudioFolderItemAdapter.AudioFolderItemViewHolder>() {

    class AudioFolderItemViewHolder(audioFolderItemViewBinding: FolderItemViewBinding) :
        RecyclerView.ViewHolder(audioFolderItemViewBinding.root) {
        val titleView = audioFolderItemViewBinding.textViewFolderItemTitle
        val albumView = audioFolderItemViewBinding.textViewFolderItemAlbum
        val displayArtView = audioFolderItemViewBinding.imageViewFolderItemDisplayArt

        init {
            audioFolderItemViewBinding.root.setOnClickListener {
                //DON'T holder a reference to the fragment
                it.findFragment<AudioFolderItemsFragment>()
                    .onFolderItemClicked(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AudioFolderItemViewHolder {
        val fileViewBinding = FolderItemViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return AudioFolderItemViewHolder(fileViewBinding)
    }

    override fun onBindViewHolder(viewHolder: AudioFolderItemViewHolder, position: Int) {
        viewHolder.titleView.text = dataSet[position].title
        viewHolder.albumView.text = dataSet[position].album
    }

    override fun getItemCount() = dataSet.size

    override fun onViewAttachedToWindow(holder: AudioFolderItemViewHolder) {
        super.onViewAttachedToWindow(holder)

        holder.displayArtView.findFragment<AudioFolderItemsFragment>()
            .onAdapterChildThumbnailLoad(holder.displayArtView, dataSet[holder.bindingAdapterPosition])
    }

}