package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.databinding.FolderRelativeViewBinding
import com.dimitrilc.freemediaplayer.ui.state.FoldersRelativeUiState

private const val TAG = "FOLDERS_RELATIVE_ADAPTER"

class FoldersRelativeAdapter(private val uiState: FoldersRelativeUiState,
                             private val fullPathPos: Int) :
    RecyclerView.Adapter<FoldersRelativeAdapter.FolderRelativeViewHolder>() {

    class FolderRelativeViewHolder(
        val folderRelativeViewBinding: FolderRelativeViewBinding) :
        RecyclerView.ViewHolder(folderRelativeViewBinding.root) {
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderRelativeViewHolder {
        val folderViewBinding = FolderRelativeViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return FolderRelativeViewHolder(folderViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FolderRelativeViewHolder, position: Int) {
        viewHolder.folderRelativeViewBinding.fullPathPos = fullPathPos
        viewHolder.folderRelativeViewBinding.bindingAdapterPos = position
        viewHolder.folderRelativeViewBinding.foldersRelativeUiState = uiState
    }

    override fun getItemCount() = uiState.path.size

}