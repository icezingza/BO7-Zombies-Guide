package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) providing database operations for quest step progress.
 */
@Dao
interface QuestStepProgressDao {

  @Query("SELECT * FROM quest_step_progress ORDER BY step_id ASC")
  fun getAllProgress(): Flow<List<QuestStepProgressEntity>>

  @Query("SELECT * FROM quest_step_progress WHERE is_completed = 1")
  fun getCompletedProgress(): Flow<List<QuestStepProgressEntity>>

  @Query("SELECT * FROM quest_step_progress WHERE step_id = :stepId LIMIT 1")
  suspend fun getProgressByStepId(stepId: Int): QuestStepProgressEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProgress(progress: QuestStepProgressEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(progressList: List<QuestStepProgressEntity>)

  @Query("UPDATE quest_step_progress SET is_completed = :isCompleted, completed_timestamp = :timestamp WHERE step_id = :stepId")
  suspend fun updateCompletion(stepId: Int, isCompleted: Boolean, timestamp: Long?)

  @Query("DELETE FROM quest_step_progress WHERE step_id = :stepId")
  suspend fun deleteProgressByStepId(stepId: Int)

  @Query("DELETE FROM quest_step_progress")
  suspend fun clearAllProgress()

  @Query("UPDATE quest_step_progress SET is_completed = 0, completed_timestamp = NULL")
  suspend fun resetAllProgress()
}
