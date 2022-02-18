package com.example.freemediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FolderItemViewBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.fragments.FolderItemsFragment

private const val TAG = "FILE_ADAPTER"

//TODO Remove content resolver dep
class FileAdapter(private val dataSet: List<Audio>) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(folderItemViewBinding: FolderItemViewBinding) :
        RecyclerView.ViewHolder(folderItemViewBinding.root) {
        val titleView = folderItemViewBinding.textViewFolderItemTitle
        val albumView = folderItemViewBinding.textViewFolderItemAlbum
        val displayArtView = folderItemViewBinding.imageViewFolderItemDisplayArt

        init {
            folderItemViewBinding.root.setOnClickListener {
                //DON'T holder a reference to the fragment
                it.findFragment<FolderItemsFragment>()
                    .onFolderItemClicked(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FileViewHolder {
        val fileViewBinding = FolderItemViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return FileViewHolder(fileViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FileViewHolder, position: Int) {
        viewHolder.titleView.text = dataSet[position].title
        viewHolder.albumView.text = dataSet[position].album
    }

    override fun getItemCount() = dataSet.size

    override fun onViewAttachedToWindow(holder: FileViewHolder) {
        super.onViewAttachedToWindow(holder)

        holder.displayArtView.findFragment<FolderItemsFragment>()
            .onAdapterChildThumbnailLoad(holder.displayArtView, dataSet[holder.bindingAdapterPosition])
    }

}