package com.dimitrilc.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.dimitrilc.freemediaplayer.entities.ui.ParentPath

@Dao
interface ParentPathDao