package com.example.freemediaplayer.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.freemediaplayer.Audio
import com.example.freemediaplayer.Video

@Database(entities = [Audio::class, Video::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
    abstract fun videoDao(): VideoDao
}