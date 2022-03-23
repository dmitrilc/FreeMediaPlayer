package com.example.freemediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.ActivePlaylistItemViewBinding
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.fragments.ActivePlaylistFragment

private const val TAG = "ACTIVE_PLAYLIST_ITEM_ADAPTER"

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

        holder.displayArtView.findFragment<ActivePlaylistFragment>()
            .onAdapterChildThumbnailLoad(holder.displayArtView, item.albumArtUri!!, item.id)
    }

}

//TODO Remove duplicate code between this Adapter and FolderItems