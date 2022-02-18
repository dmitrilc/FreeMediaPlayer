package com.example.freemediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderRelativeViewBinding
import com.example.freemediaplayer.fragments.AudioFoldersFragment

private const val TAG = "FOLDERS_RELATIVE_ADAPTER"

class FoldersRelativeAdapter(private val dataSet: List<String>,
                             private val fullPathPos: Int) :
    RecyclerView.Adapter<FoldersRelativeAdapter.FolderRelativeViewHolder>() {

    class FolderRelativeViewHolder(folderRelativeViewBinding: FolderRelativeViewBinding, parentPos: Int) :
        RecyclerView.ViewHolder(folderRelativeViewBinding.root) {
        val relativePath: TextView = folderRelativeViewBinding.textViewFolderRelativePath

        //TODO Make sure to unbind onclicklistener
        init {
            folderRelativeViewBinding.root.setOnClickListener {
                //DON'T hold a reference to the fragment
                it.findFragment<AudioFoldersFragment>()
                    .onFolderRelativeClicked(parentPos, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderRelativeViewHolder {
        val folderViewBinding = FolderRelativeViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return FolderRelativeViewHolder(folderViewBinding, fullPathPos)
    }

    override fun onBindViewHolder(viewHolder: FolderRelativeViewHolder, position: Int) {
        //viewHolder.fullPath.text = dataSet[position].fullPath
        viewHolder.relativePath.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size

}