package com.dimitrilc.freemediaplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.databinding.FolderFullViewBinding
import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState

private const val TAG = "FOLDERS_FULL_ADAPTER"

class FoldersFullAdapter(
    private val dataSet: List<FoldersUiState>,
    private val callback: (Int)->Unit) :
    RecyclerView.Adapter<FoldersFullAdapter.FolderFullViewHolder>() {

    class FolderFullViewHolder(val folderFullViewBinding: FolderFullViewBinding, private val callback: (Int)->Unit) :
        RecyclerView.ViewHolder(folderFullViewBinding.root) {
        private val cardView: CardView = folderFullViewBinding.cardViewFolderFullPath
        //val fullPath: TextView = folderFullViewBinding.textViewFolderFullPath
        val relativeRecyclerView: RecyclerView = folderFullViewBinding.recyclerFoldersRelative

        init {
            cardView.setOnClickListener {
                callback(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderFullViewHolder {
        val folderFullViewBinding = FolderFullViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return FolderFullViewHolder(folderFullViewBinding, callback)
    }

    override fun onBindViewHolder(viewHolder: FolderFullViewHolder, position: Int) {
        //viewHolder.fullPath.text = dataSet[position].parentPath
        viewHolder.folderFullViewBinding.foldersUiState = dataSet[position]
        viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(dataSet[position].relativePaths, position)

        if (dataSet[position].isExpanded){
            viewHolder.relativeRecyclerView.visibility = View.VISIBLE
        } else {
            viewHolder.relativeRecyclerView.visibility = View.GONE
        }
    }

    override fun getItemCount() = dataSet.size

}