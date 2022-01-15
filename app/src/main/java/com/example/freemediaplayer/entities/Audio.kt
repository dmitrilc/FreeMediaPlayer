package com.example.freemediaplayer.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio")
data class Audio(
    val title: String?,
    val artist: String?,
    //val year: String?,
    val album: String?,
    val uri: String?
    //val isMusic: Boolean = false,
    //val isPodcast: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0 //set default value to 0 so Room will generate Id
}
