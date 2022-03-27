package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.databinding.FolderItemViewBinding
import com.dimitrilc.freemediaplayer.ui.fragments.folderitems.FolderItemsFragment
import com.dimitrilc.freemediaplayer.ui.state.FolderItemsUiState

private const val TAG = "FILE_ADAPTER"

class MediaFolderItemAdapter(private val dataSet: List<FolderItemsUiState>) :
    RecyclerView.Adapter<MediaFolderItemAdapter.FolderItemViewHolder>() {

    class FolderItemViewHolder(folderItemViewBinding: FolderItemViewBinding) :
        RecyclerView.ViewHolder(folderItemViewBinding.root) {
        val titleView = folderItemViewBinding.textViewFolderItemTitle
        val albumView = folderItemViewBinding.textViewFolderItemAlbum
        val displayArtView = folderItemViewBinding.imageViewFolderItemDisplayArt

        init {
            folderItemViewBinding.root.setOnClickListener {
                //DON'T hold a reference to the fragment
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
        val item = dataSet[position]

        viewHolder.titleView.text = item.title
        viewHolder.albumView.text = item.album

        if (item.thumbnail != null){
            viewHolder.displayArtView.setImageBitmap(item.thumbnail)
        }

    }

    override fun getItemCount() = dataSet.size
}