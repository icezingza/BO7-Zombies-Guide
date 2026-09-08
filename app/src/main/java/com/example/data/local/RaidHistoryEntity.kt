package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores completed or saved raid session runs with statistics:
 * - Date timestamp
 * - Highest round reached
 * - Number of main quest steps completed
 * - Status of 4 temples cleansed
 * - Status of Exfil success
 * - Duration in minutes
 */
@Entity(tableName = "raid_session_history")
data class RaidHistoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,

  @ColumnInfo(name = "timestamp")
  val timestamp: Long = System.currentTimeMillis(),

  @ColumnInfo(name = "round_reached")
  val roundReached: Int,

  @ColumnInfo(name = "steps_completed_count")
  val stepsCompletedCount: Int,

  @ColumnInfo(name = "temples_cleansed_count")
  val templesCleansedCount: Int,

  @ColumnInfo(name = "is_exfil_success")
  val isExfilSuccess: Boolean = false,

  @ColumnInfo(name = "duration_minutes")
  val durationMinutes: Int = 0,

  @ColumnInfo(name = "notes")
  val notes: String = ""
)
