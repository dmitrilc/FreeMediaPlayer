package com.example.freemediaplayer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.FileViewBinding
import com.example.freemediaplayer.fragments.AudioListFragmentDirections
import com.example.freemediaplayer.fragments.FilesFragmentDirections
import com.example.freemediaplayer.pojos.FileData

private const val TAG = "FILES_ADAPTER"

class FilesAdapter(private val dataSet: List<FileData>) :
    RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {

    class FileViewHolder(private val fileViewBinding: FileViewBinding) :
        RecyclerView.ViewHolder(fileViewBinding.root), View.OnClickListener {
        val fileName = fileViewBinding.textViewFileName
        val album = fileViewBinding.textViewAlbum

        init {
            fileViewBinding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val navController = v?.findNavController()

            Log.d(TAG, bindingAdapterPosition.toString())

            //TODO Clean up
            navController
                ?.navigate(
                FilesFragmentDirections
                    .actionFilesPathToAudioPlayerFragment(bindingAdapterPosition))
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FileViewHolder {
        val fileViewBinding = FileViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

//        val navController = viewGroup.findNavController()
//
//        fileViewBinding.root.setOnClickListener {
//            navController.navigate(FilesFragmentDirections.actionFilesPathToAudioPlayerFragment(fileViewBinding.textViewFileName.toString()))
//        }

        return FileViewHolder(fileViewBinding)
    }

    override fun onBindViewHolder(viewHolder: FileViewHolder, position: Int) {
        viewHolder.fileName.text = dataSet[position].displayName
        viewHolder.album.text = "Sample Album"

        Log.d(TAG, dataSet[position].displayName)
    }

    override fun getItemCount() = dataSet.size

}