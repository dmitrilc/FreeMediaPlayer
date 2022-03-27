package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem

interface ActiveMediaRepository {
    fun insert(activeMediaItem: ActiveMediaItem)
}