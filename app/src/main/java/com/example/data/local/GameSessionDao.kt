package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {

  @Query("SELECT * FROM game_session_state WHERE id = 1 LIMIT 1")
  fun getSession(): Flow<GameSessionEntity?>

  @Query("SELECT * FROM game_session_state WHERE id = 1 LIMIT 1")
  suspend fun getSessionSync(): GameSessionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveSession(session: GameSessionEntity)

  @Query("DELETE FROM game_session_state")
  suspend fun clearSession()
}
