package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.databinding.FolderFullViewBinding
import com.dimitrilc.freemediaplayer.ui.state.folders.FoldersFullUiState

private const val TAG = "FOLDERS_FULL_ADAPTER"

class FoldersFullAdapter(
    private val dataSet: List<FoldersFullUiState>
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
        val state = dataSet[position]

        viewHolder.folderFullViewBinding.foldersFullUiState = state
        viewHolder.folderFullViewBinding.adapterPosition = position

        viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(
            state.relativePaths,
            position,
            callback = state.onRelativeClick
        )

        //Fixes flickering problem
        viewHolder.folderFullViewBinding.executePendingBindings()
    }

    override fun getItemCount() = dataSet.size

}