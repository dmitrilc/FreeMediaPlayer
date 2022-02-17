package com.example.freemediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.freemediaplayer.databinding.ActivePlaylistItemViewBinding
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.fragments.ActivePlaylistFragment

private const val TAG = "ACTIVE_PLAYLIST_ITEM_ADAPTER"
//TODO Remove contentresolver from dep
class ActivePlaylistItemAdapter(private val dataSet: List<Audio>) :
    RecyclerView.Adapter<ActivePlaylistItemAdapter.ActivePlaylistItemViewHolder>() {

    class ActivePlaylistItemViewHolder(activePlaylistItemViewBinding: ActivePlaylistItemViewBinding) :
        RecyclerView.ViewHolder(activePlaylistItemViewBinding.root) {
        val titleView = activePlaylistItemViewBinding.textViewActivePlaylistItemTitle
        val albumView = activePlaylistItemViewBinding.textViewActivePlaylistItemAlbum
        val displayArtView = activePlaylistItemViewBinding.imageViewActivePlaylistItemDisplayArt

        init {
//            playlistFileViewBinding.root.setOnClickListener {
//                //DON'T holder a reference to the fragment
//                it.findFragment<PlaylistFragment>()
//                    .onAdapterChildClicked(it, bindingAdapterPosition)
//            }
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ActivePlaylistItemViewHolder {
        val activePlaylistItemViewBinding = ActivePlaylistItemViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return ActivePlaylistItemViewHolder(activePlaylistItemViewBinding)
    }

    override fun onBindViewHolder(viewHolderActive: ActivePlaylistItemViewHolder, position: Int) {
        viewHolderActive.titleView.text = dataSet[position].title
        viewHolderActive.albumView.text = dataSet[position].album
    }

    override fun getItemCount() = dataSet.size

    override fun onViewAttachedToWindow(holderActive: ActivePlaylistItemViewHolder) {
        super.onViewAttachedToWindow(holderActive)

        holderActive.displayArtView.findFragment<ActivePlaylistFragment>()
            .onAdapterChildThumbnailLoad(holderActive.displayArtView, holderActive.bindingAdapterPosition)
    }

}