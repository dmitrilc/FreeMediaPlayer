package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.ActivePlaylistItemViewBinding
import com.dimitrilc.freemediaplayer.ui.state.folders.items.FolderItemsUiState

private const val TAG = "ACTIVE_PLAYLIST_ITEM_ADAPTER"

class ActivePlaylistItemAdapter(private val dataSet: List<FolderItemsUiState>) :
    RecyclerView.Adapter<ActivePlaylistItemAdapter.ActivePlaylistItemViewHolder>() {

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
        viewHolder.activePlaylistItemViewBinding.state = item

        viewHolder.activePlaylistItemViewBinding.activePlaylistItemView.setOnClickListener {
            //The binding adapter position needs to be dynamically resolved at runtime because the dataset has changed.
            val pos = viewHolder.bindingAdapterPosition
            dataSet[pos].onClick.invoke(pos)
        }

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
}