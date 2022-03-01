package com.example.freemediaplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderFullViewBinding
import com.example.freemediaplayer.pojos.AdapterFolderData

private const val TAG = "FOLDERS_FULL_ADAPTER"

class FoldersFullAdapter(private val dataSet: List<AdapterFolderData>) :
    RecyclerView.Adapter<FoldersFullAdapter.FolderFullViewHolder>() {

    class FolderFullViewHolder(folderFullViewBinding: FolderFullViewBinding) :
        RecyclerView.ViewHolder(folderFullViewBinding.root) {
        private val cardView: CardView = folderFullViewBinding.cardViewFolderFullPath
        val fullPath: TextView = folderFullViewBinding.textViewFolderFullPath
        val relativeRecyclerView: RecyclerView = folderFullViewBinding.recyclerAudioFoldersRelative

        init {
            cardView.setOnClickListener {
                if (relativeRecyclerView.isVisible){
                    relativeRecyclerView.visibility = View.GONE
                } else {
                    relativeRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderFullViewHolder {
        val folderFullViewBinding = FolderFullViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return FolderFullViewHolder(folderFullViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FolderFullViewHolder, position: Int) {
        viewHolder.fullPath.text = dataSet[position].parentPath
        viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(dataSet[position].relativePaths, position)
    }

    override fun getItemCount() = dataSet.size

}