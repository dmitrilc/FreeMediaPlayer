package com.example.freemediaplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderFullViewBinding
import com.example.freemediaplayer.entities.ui.ParentPathWithRelativePaths

private const val TAG = "FOLDERS_FULL_ADAPTER"

class FoldersFullAdapter(
    private val dataSet: List<ParentPathWithRelativePaths>,
    private val callback: (Int)->Unit) :
    RecyclerView.Adapter<FoldersFullAdapter.FolderFullViewHolder>() {

    class FolderFullViewHolder(folderFullViewBinding: FolderFullViewBinding, private val callback: (Int)->Unit) :
        RecyclerView.ViewHolder(folderFullViewBinding.root) {
        private val cardView: CardView = folderFullViewBinding.cardViewFolderFullPath
        val fullPath: TextView = folderFullViewBinding.textViewFolderFullPath
        val relativeRecyclerView: RecyclerView = folderFullViewBinding.recyclerAudioFoldersRelative

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
        viewHolder.fullPath.text = dataSet[position].parentPath.parentPath
        viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(dataSet[position].relativePaths, position)

        if (dataSet[position].parentPath.isExpanded){
            viewHolder.relativeRecyclerView.visibility = View.VISIBLE
        } else {
            viewHolder.relativeRecyclerView.visibility = View.GONE
        }
    }

    override fun getItemCount() = dataSet.size

}