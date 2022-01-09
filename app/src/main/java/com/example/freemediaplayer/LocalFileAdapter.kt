package com.example.freemediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderViewBinding

class LocalFileAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<LocalFileAdapter.FileViewHolder>() {

    class FileViewHolder(private val folderViewBinding: FolderViewBinding) :
        RecyclerView.ViewHolder(folderViewBinding.root) {
        val textView: TextView = folderViewBinding.textViewDirectoryPath
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FileViewHolder {
        val folderViewBinding = FolderViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return FileViewHolder(folderViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FileViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size

}
