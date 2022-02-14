package com.example.freemediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderViewBinding
import com.example.freemediaplayer.fragments.AudioListFragment
import com.example.freemediaplayer.pojos.FolderData

private const val TAG = "FOLDERS_ADAPTER"

class FoldersAdapter(private val dataSet: List<FolderData>) :
    RecyclerView.Adapter<FoldersAdapter.FolderViewHolder>() {

    class FolderViewHolder(folderViewBinding: FolderViewBinding) :
        RecyclerView.ViewHolder(folderViewBinding.root) {
        val type: TextView = folderViewBinding.textViewMediaType
        val path: TextView = folderViewBinding.textViewMediaLocation

        //TODO Make sure to unbind onclicklistener
        init {
            folderViewBinding.root.setOnClickListener {
                //DON'T holder a reference to the fragment
                it.findFragment<AudioListFragment>()
                    .onAdapterChildClicked(it, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderViewHolder {
        val folderViewBinding = FolderViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return FolderViewHolder(folderViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FolderViewHolder, position: Int) {
        viewHolder.type.text = dataSet[position].type
    }

    override fun getItemCount() = dataSet.size

}