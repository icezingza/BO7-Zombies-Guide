package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity to persist the completion progress of Rex Infernus main quest steps.
 */
@Entity(tableName = "quest_step_progress")
data class QuestStepProgressEntity(
  @PrimaryKey
  @ColumnInfo(name = "step_id")
  val stepId: Int,

  @ColumnInfo(name = "is_completed")
  val isCompleted: Boolean = false,

  @ColumnInfo(name = "completed_timestamp")
  val completedTimestamp: Long? = null
)
