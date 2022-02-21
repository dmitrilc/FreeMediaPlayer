package com.example.freemediaplayer.adapter
/*

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderFullViewBinding
import com.example.freemediaplayer.fragments.FoldersFullFragment
import com.example.freemediaplayer.pojos.AdapterFolderData

private const val TAG = "FOLDERS_FULL_ADAPTER"

class AudioFoldersFullAdapter(private val dataSet: List<AdapterFolderData>) :
    RecyclerView.Adapter<FoldersFullAdapter.FolderFullViewHolder>() {

    class AudioFolderFullViewHolder(audioFolderFullViewBinding: FolderFullViewBinding) :
        RecyclerView.ViewHolder(audioFolderFullViewBinding.root) {
        private val cardView: CardView = audioFolderFullViewBinding.cardViewFolderFullPath
        val fullPath: TextView = audioFolderFullViewBinding.textViewFolderFullPath
        val relativeRecyclerView: RecyclerView = audioFolderFullViewBinding.recyclerAudioFoldersRelative

        //var isFoldersVisible = true

        //TODO Make sure to unbind onclicklistener
        init {
            cardView.setOnClickListener {
                //DON'T holder a reference to the fragment
                it.findFragment<FoldersFullFragment>()
                    .onFolderFullCardViewClicked(bindingAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AudioFolderFullViewHolder {
        val folderFullViewBinding = FolderFullViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return AudioFolderFullViewHolder(folderFullViewBinding)
    }

    override fun onBindViewHolder(viewHolder: AudioFolderFullViewHolder, position: Int) {
        viewHolder.fullPath.text = dataSet[position].parentPath

        //viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(dataSet[position].relativePaths, position)

        if (dataSet[position].isCollapsed){
            viewHolder.relativeRecyclerView.adapter = null
        } else {
            viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(dataSet[position].relativePaths, position)
        }

        //TODO Better handling with onclick interface when have time
//        viewHolder.cardView.setOnClickListener {
//            viewHolder.isFoldersVisible = !viewHolder.isFoldersVisible
//
//            if (viewHolder.isFoldersVisible) {
//                viewHolder.relativeRecyclerView.adapter = FoldersRelativeAdapter(dataSet[position].relativePaths, position)
//            } else {
//                viewHolder.relativeRecyclerView.adapter = null
//            }
//        }
    }

    override fun getItemCount() = dataSet.size

}*/
