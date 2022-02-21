package com.example.freemediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderItemViewBinding
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.fragments.VideoFolderItemsFragment

class VideoFolderItemAdapter(private val dataSet: List<Video>) :
    RecyclerView.Adapter<VideoFolderItemAdapter.VideoFolderItemViewHolder>() {

    class VideoFolderItemViewHolder(videoFolderItemViewBinding: FolderItemViewBinding) :
        RecyclerView.ViewHolder(videoFolderItemViewBinding.root) {
        val titleView = videoFolderItemViewBinding.textViewFolderItemTitle
        val albumView = videoFolderItemViewBinding.textViewFolderItemAlbum
        val displayArtView = videoFolderItemViewBinding.imageViewFolderItemDisplayArt

        init {
            videoFolderItemViewBinding.root.setOnClickListener {
                //DON'T holder a reference to the fragment
                it.findFragment<VideoFolderItemsFragment>()
                    .onFolderItemClicked(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VideoFolderItemViewHolder {
        val fileViewBinding = FolderItemViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return VideoFolderItemViewHolder(fileViewBinding)
    }

    override fun onBindViewHolder(viewHolder: VideoFolderItemViewHolder, position: Int) {
        viewHolder.titleView.text = dataSet[position].title
        viewHolder.albumView.text = dataSet[position].album
    }

    override fun getItemCount() = dataSet.size

    override fun onViewAttachedToWindow(holder: VideoFolderItemViewHolder) {
        super.onViewAttachedToWindow(holder)

        holder.displayArtView.findFragment<VideoFolderItemsFragment>()
            .onAdapterChildThumbnailLoad(holder.displayArtView, dataSet[holder.bindingAdapterPosition])
    }

}