package com.dimitrilc.freemediaplayer.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaDao
import com.dimitrilc.freemediaplayer.data.room.dao.GlobalPlaylistDao
import com.dimitrilc.freemediaplayer.data.room.dao.MediaItemDao

@Database(
    entities = [
        MediaItem::class,
        GlobalPlaylistItem::class,
        ActiveMedia::class],
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun globalPlaylistDao(): GlobalPlaylistDao
    abstract fun activeMediaItemDao(): ActiveMediaDao
}