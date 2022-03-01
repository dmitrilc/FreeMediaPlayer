package com.example.freemediaplayer.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.freemediaplayer.entities.MediaItem

@Database(entities = [MediaItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
}