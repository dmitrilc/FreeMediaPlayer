package com.dimitrilc.freemediaplayer.data.worker

import androidx.work.Data

fun getActiveMediaWorkerInputData(playlistIndex: Long, mediaItemId: Long): Data {
    return Data.Builder()
        .putLong(WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX, playlistIndex)
        .putLong(WORKER_DATA_KEY_MEDIA_ITEM_ID, mediaItemId)
        .build()
}