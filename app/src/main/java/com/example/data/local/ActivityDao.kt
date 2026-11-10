package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
  @Query("SELECT * FROM activities ORDER BY timestamp DESC LIMIT 30")
  fun getRecentActivities(): Flow<List<ActivityEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertActivity(activity: ActivityEntity): Long

  @Query("DELETE FROM activities")
  suspend fun clearAll()
}
