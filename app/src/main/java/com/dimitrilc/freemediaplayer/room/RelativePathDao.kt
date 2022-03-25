package com.dimitrilc.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.dimitrilc.freemediaplayer.entities.ui.ParentPath
import com.dimitrilc.freemediaplayer.entities.ui.RelativePath

@Dao
interface RelativePathDao