package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem

interface GlobalPlaylistRepository {
    fun replacePlaylist(playlist: List<GlobalPlaylistItem>)
}