package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.databinding.FolderItemViewBinding
import com.dimitrilc.freemediaplayer.ui.state.folders.items.FolderItemsUiState

private const val TAG = "FILE_ADAPTER"

class MediaFolderItemAdapter(private val dataSet: List<FolderItemsUiState>) :
    RecyclerView.Adapter<MediaFolderItemAdapter.FolderItemViewHolder>() {

    class FolderItemViewHolder(val folderItemViewBinding: FolderItemViewBinding) :
        RecyclerView.ViewHolder(folderItemViewBinding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderItemViewHolder {
        val fileViewBinding = FolderItemViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return FolderItemViewHolder(fileViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FolderItemViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.folderItemViewBinding.state = item
        viewHolder.folderItemViewBinding.adapterPos = position
        
        val thumb = item.thumbnailLoader(item.thumbnailUri, item.videoId)
        if (thumb != null){
            viewHolder.folderItemViewBinding.imageViewFolderItemDisplayArt.setImageBitmap(thumb)
        } else {
            viewHolder.folderItemViewBinding.imageViewFolderItemDisplayArt.setImageResource(R.drawable.ic_baseline_music_note_24)
        }
    }

    override fun getItemCount() = dataSet.size
}