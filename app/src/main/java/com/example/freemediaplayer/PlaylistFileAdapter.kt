package com.example.freemediaplayer

import android.content.ContentResolver
import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.PlaylistFileViewBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.fragments.FilesFragment
import com.example.freemediaplayer.fragments.PlaylistFragment
import java.io.FileNotFoundException

private const val TAG = "PLAYLIST_FILE_ADAPTER"
//TODO Remove contentresolver from dep
class PlaylistFileAdapter(private val dataSet: List<Audio>) :
    RecyclerView.Adapter<PlaylistFileAdapter.PlaylistFileViewHolder>() {

    class PlaylistFileViewHolder(playlistFileViewBinding: PlaylistFileViewBinding) :
        RecyclerView.ViewHolder(playlistFileViewBinding.root) {
        val titleView = playlistFileViewBinding.textViewPlaylistFileTitle
        val albumView = playlistFileViewBinding.textViewPlaylistFileAlbum
        val thumbnailView = playlistFileViewBinding.imageViewPlaylistFileSmallDisplayArt

        init {
//            playlistFileViewBinding.root.setOnClickListener {
//                //DON'T holder a reference to the fragment
//                it.findFragment<PlaylistFragment>()
//                    .onAdapterChildClicked(it, bindingAdapterPosition)
//            }
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PlaylistFileViewHolder {
        val playlistFileViewBinding = PlaylistFileViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return PlaylistFileViewHolder(playlistFileViewBinding)
    }

    override fun onBindViewHolder(viewHolder: PlaylistFileViewHolder, position: Int) {
        viewHolder.titleView.text = dataSet[position].title
        viewHolder.albumView.text = dataSet[position].album
    }

    override fun getItemCount() = dataSet.size

    override fun onViewAttachedToWindow(holder: PlaylistFileViewHolder) {
        super.onViewAttachedToWindow(holder)

        holder.thumbnailView.findFragment<PlaylistFragment>()
            .onAdapterChildThumbnailLoad(holder.thumbnailView, holder.bindingAdapterPosition)
    }

}