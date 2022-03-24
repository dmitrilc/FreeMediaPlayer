package com.example.freemediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderItemViewBinding
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.fragments.folderitems.FolderItemsFragment

private const val TAG = "FILE_ADAPTER"

class MediaFolderItemAdapter(private val dataSet: List<MediaItem>) :
    RecyclerView.Adapter<MediaFolderItemAdapter.FolderItemViewHolder>() {

    class FolderItemViewHolder(folderItemViewBinding: FolderItemViewBinding) :
        RecyclerView.ViewHolder(folderItemViewBinding.root) {
        val titleView = folderItemViewBinding.textViewFolderItemTitle
        val albumView = folderItemViewBinding.textViewFolderItemAlbum
        val displayArtView = folderItemViewBinding.imageViewFolderItemDisplayArt

        init {
            folderItemViewBinding.root.setOnClickListener {
                //DON'T holder a reference to the fragment
                it.findFragment<FolderItemsFragment>()
                    .onFolderItemClicked(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderItemViewHolder {
        val fileViewBinding = FolderItemViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return FolderItemViewHolder(fileViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FolderItemViewHolder, position: Int) {
        viewHolder.titleView.text = dataSet[position].title
        viewHolder.albumView.text = dataSet[position].album
    }

    override fun getItemCount() = dataSet.size

    override fun onViewAttachedToWindow(holder: FolderItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        val item = dataSet[holder.bindingAdapterPosition]

        //TODO Clean up and increase readability
        if (item.isAudio && item.albumArtUri != null){
            holder.displayArtView.findFragment<FolderItemsFragment>()
                .onAdapterChildThumbnailLoad(holder.displayArtView, item.albumArtUri, null)
        } else if (!item.isAudio && item.albumArtUri != null){
            holder.displayArtView.findFragment<FolderItemsFragment>()
                .onAdapterChildThumbnailLoad(holder.displayArtView, item.albumArtUri, item.id)
        }
    }

}