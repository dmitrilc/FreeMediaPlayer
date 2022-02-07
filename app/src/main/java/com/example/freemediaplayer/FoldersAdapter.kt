package com.example.freemediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderViewBinding
import com.example.freemediaplayer.fragments.AudioListFragmentDirections
import com.example.freemediaplayer.pojos.FolderData

class FoldersAdapter(private val dataSet: List<FolderData>) :
    RecyclerView.Adapter<FoldersAdapter.FolderViewHolder>() {

    class FolderViewHolder(private val folderViewBinding: FolderViewBinding) :
        RecyclerView.ViewHolder(folderViewBinding.root) {
        val type: TextView = folderViewBinding.textViewMediaType
        val path: TextView = folderViewBinding.textViewMediaLocation
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderViewHolder {
        val folderViewBinding = FolderViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        val navController = viewGroup.findNavController()

        folderViewBinding.cardView.setOnClickListener {
            navController.navigate(AudioListFragmentDirections.actionMusicPathsToFilesPath(folderViewBinding.textViewMediaType.text.toString()))
        }

        return FolderViewHolder(folderViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FolderViewHolder, position: Int) {
        viewHolder.type.text = dataSet[position].type


        //viewHolder.path.text = dataSet[position].path
    }

    override fun getItemCount() = dataSet.size

}
