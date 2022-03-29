package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: ActiveMedia)

    @Query("SELECT * FROM active_item")
    suspend fun getOnce(): ActiveMedia

    @Query("SELECT * FROM active_item")
    fun getObservable(): Flow<ActiveMedia>

}