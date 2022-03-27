package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem

interface ActiveMediaRoomDataSource {
    fun insert(activeMediaItem: ActiveMediaItem)
}