package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem

interface GlobalPlaylistRoomDataSource {
    fun replacePlaylist(playlist: List<GlobalPlaylistItem>)
}