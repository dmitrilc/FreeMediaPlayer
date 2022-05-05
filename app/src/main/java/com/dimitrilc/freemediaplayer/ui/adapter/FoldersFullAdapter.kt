package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.databinding.FolderFullViewBinding
import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState

private const val TAG = "FOLDERS_FULL_ADAPTER"

class FoldersFullAdapter(
    private val uiState: FoldersUiState
    ) : RecyclerView.Adapter<FoldersFullAdapter.FolderFullViewHolder>() {

    class FolderFullViewHolder(val folderFullViewBinding: FolderFullViewBinding):
        RecyclerView.ViewHolder(folderFullViewBinding.root) {
        val relativeRecyclerView = folderFullViewBinding.recyclerFoldersRelative
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderFullViewHolder {
        val folderFullViewBinding = FolderFullViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return FolderFullViewHolder(folderFullViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FolderFullViewHolder, position: Int) {
        viewHolder.folderFullViewBinding.foldersFullUiState = uiState.fullFolders[position]
        viewHolder.folderFullViewBinding.adapterPosition = position
        viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(
            uiState.fullFolders[position].relativePath,
            position)

        //Fixes flickering problem
        viewHolder.folderFullViewBinding.executePendingBindings()
    }

    override fun getItemCount() = uiState.fullFolders.size

}