package com.example.freemediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FileViewBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.fragments.FilesFragment

private const val TAG = "FILE_ADAPTER"

//TODO Remove content resolver dep
class FileAdapter(private val dataSet: List<Audio>) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(fileViewBinding: FileViewBinding) :
        RecyclerView.ViewHolder(fileViewBinding.root) {
        val titleView = fileViewBinding.textViewFilesTitle
        val albumView = fileViewBinding.textViewFilesAlbum
        val thumbnailView = fileViewBinding.imageViewFilesSmallDisplayArt

        init {
            fileViewBinding.root.setOnClickListener {
                //DON'T holder a reference to the fragment
                it.findFragment<FilesFragment>()
                    .onAdapterChildClicked(it, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FileViewHolder {
        val fileViewBinding = FileViewBinding.inflate(
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

        holder.thumbnailView.findFragment<FilesFragment>()
            .onAdapterChildThumbnailLoad(holder.thumbnailView, holder.bindingAdapterPosition)
    }

}