package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.databinding.FolderFullViewBinding
import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState
import java.util.function.IntConsumer

private const val TAG = "FOLDERS_FULL_ADAPTER"

class FoldersFullAdapter(
    private val dataSet: List<FoldersUiState>,
    private val callback: IntConsumer //using pre-made Java SAM because I am lazy to create new type
    ) : RecyclerView.Adapter<FoldersFullAdapter.FolderFullViewHolder>() {

    class FolderFullViewHolder(val folderFullViewBinding: FolderFullViewBinding):
        RecyclerView.ViewHolder(folderFullViewBinding.root) {
        val relativeRecyclerView: RecyclerView = folderFullViewBinding.recyclerFoldersRelative
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderFullViewHolder {
        val folderFullViewBinding = FolderFullViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        folderFullViewBinding.callback = callback

        return FolderFullViewHolder(folderFullViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FolderFullViewHolder, position: Int) {
        viewHolder.folderFullViewBinding.adapterPosition = position
        viewHolder.folderFullViewBinding.foldersUiState = dataSet[position]
        viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(dataSet[position].relativePaths, position)

        //Fixes flickering problem
        viewHolder.folderFullViewBinding.executePendingBindings()
    }

    override fun getItemCount() = dataSet.size

}