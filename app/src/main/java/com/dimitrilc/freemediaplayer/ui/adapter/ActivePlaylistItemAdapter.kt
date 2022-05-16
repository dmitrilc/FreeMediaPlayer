package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.ActivePlaylistItemViewBinding
import com.dimitrilc.freemediaplayer.ui.state.folders.items.FolderItemsUiState

private const val TAG = "ACTIVE_PLAYLIST_ITEM_ADAPTER"

class ActivePlaylistItemAdapter(private val dataSet: List<FolderItemsUiState>) :
    ListAdapter<FolderItemsUiState, ActivePlaylistItemAdapter.ActivePlaylistItemViewHolder>(DIFF_CALLBACK) {

    class ActivePlaylistItemViewHolder(val activePlaylistItemViewBinding: ActivePlaylistItemViewBinding) :
        RecyclerView.ViewHolder(activePlaylistItemViewBinding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ActivePlaylistItemViewHolder {
        val activePlaylistItemViewBinding = ActivePlaylistItemViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return ActivePlaylistItemViewHolder(activePlaylistItemViewBinding)
    }

    override fun onBindViewHolder(viewHolder: ActivePlaylistItemViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.activePlaylistItemViewBinding.adapterPos = position
        viewHolder.activePlaylistItemViewBinding.state = item

        val thumb = item.thumbnailLoader(item.thumbnailUri, item.videoId)
        if (thumb != null){
            viewHolder.activePlaylistItemViewBinding
                .imageViewActivePlaylistItemDisplayArt
                .setImageBitmap(thumb)
        } else {
            viewHolder.activePlaylistItemViewBinding
                .imageViewActivePlaylistItemDisplayArt
                .setImageResource(R.drawable.ic_baseline_music_note_24)
        }
    }

    override fun getItemCount() = dataSet.size

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FolderItemsUiState>() {
            override fun areItemsTheSame(
                oldItem: FolderItemsUiState,
                newItem: FolderItemsUiState
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: FolderItemsUiState,
                newItem: FolderItemsUiState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

/*
class ActivePlaylistItemAdapter(private val dataSet: MutableList<MediaItem>) :
    RecyclerView.Adapter<ActivePlaylistItemAdapter.ActivePlaylistItemViewHolder>() {

    class ActivePlaylistItemViewHolder(activePlaylistItemViewBinding: ActivePlaylistItemViewBinding) :
        RecyclerView.ViewHolder(activePlaylistItemViewBinding.root) {
        val titleView = activePlaylistItemViewBinding.textViewActivePlaylistItemTitle
        val albumView = activePlaylistItemViewBinding.textViewActivePlaylistItemAlbum
        val displayArtView = activePlaylistItemViewBinding.imageViewActivePlaylistItemDisplayArt

        init {
            activePlaylistItemViewBinding.root.setOnClickListener {
                //DON'T holder a reference to the fragment
                it.findFragment<ActivePlaylistFragment>()
                    .setActiveMedia(bindingAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ActivePlaylistItemViewHolder {
        val activePlaylistItemViewBinding = ActivePlaylistItemViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return ActivePlaylistItemViewHolder(activePlaylistItemViewBinding)
    }

    override fun onBindViewHolder(viewHolderActive: ActivePlaylistItemViewHolder, position: Int) {
        viewHolderActive.titleView.text = dataSet[position].title
        viewHolderActive.albumView.text = dataSet[position].album
    }

    override fun getItemCount() = dataSet.size

    override fun onViewAttachedToWindow(holder: ActivePlaylistItemViewHolder) {
        super.onViewAttachedToWindow(holder)

        val item = dataSet[holder.bindingAdapterPosition]

*/
/*        holder.displayArtView.findFragment<ActivePlaylistFragment>()
            .onAdapterChildThumbnailLoad(holder.displayArtView, item.albumArtUri!!, item.mediaItemId)*//*

    }

}*/
