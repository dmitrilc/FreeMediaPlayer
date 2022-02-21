package com.example.freemediaplayer.adapter
/*

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderRelativeViewBinding
import com.example.freemediaplayer.fragments.FoldersFullFragment

private const val TAG = "FOLDERS_RELATIVE_ADAPTER"

class AudioFoldersRelativeAdapter(private val dataSet: List<String>,
                                  private val fullPathPos: Int) :
    RecyclerView.Adapter<FoldersRelativeAdapter.FolderRelativeViewHolder>() {

    class AudioFolderRelativeViewHolder(
        folderRelativeViewBinding: FolderRelativeViewBinding,
        fullPathPos: Int) :
        RecyclerView.ViewHolder(folderRelativeViewBinding.root) {
        val relativePath: TextView = folderRelativeViewBinding.textViewFolderRelativePath

        //TODO Make sure to unbind onclicklistener
        init {
            folderRelativeViewBinding.root.setOnClickListener {
                //DON'T hold a reference to the fragment

                val fragment = it.findFragment<Fragment>().javaClass

                it.findFragment<FoldersFullFragment>()
                    .onFolderRelativeClicked(fullPathPos, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AudioFolderRelativeViewHolder {
        val folderViewBinding = FolderRelativeViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return AudioFolderRelativeViewHolder(folderViewBinding, fullPathPos)
    }

    override fun onBindViewHolder(viewHolder: AudioFolderRelativeViewHolder, position: Int) {
        //viewHolder.fullPath.text = dataSet[position].fullPath
        viewHolder.relativePath.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size

}*/
