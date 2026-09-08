package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RaidHistoryDao {

  @Query("SELECT * FROM raid_session_history ORDER BY timestamp DESC")
  fun getAllRaidHistory(): Flow<List<RaidHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRaidHistory(entity: RaidHistoryEntity): Long

  @Query("DELETE FROM raid_session_history WHERE id = :id")
  suspend fun deleteRaidHistory(id: Long)

  @Query("DELETE FROM raid_session_history")
  suspend fun clearAllHistory()
}
