package com.example.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern abstracting Room database operations for quest step progress.
 */
class QuestProgressRepository(
  private val dao: QuestStepProgressDao
) {

  val allProgress: Flow<List<QuestStepProgressEntity>> = dao.getAllProgress()

  val completedProgress: Flow<List<QuestStepProgressEntity>> = dao.getCompletedProgress()

  suspend fun getProgressForStep(stepId: Int): QuestStepProgressEntity? {
    return dao.getProgressByStepId(stepId)
  }

  suspend fun setStepCompleted(stepId: Int, isCompleted: Boolean) {
    val timestamp = if (isCompleted) System.currentTimeMillis() else null
    dao.insertOrUpdateProgress(
      QuestStepProgressEntity(
        stepId = stepId,
        isCompleted = isCompleted,
        completedTimestamp = timestamp
      )
    )
  }

  suspend fun toggleStep(stepId: Int, currentlyCompleted: Boolean) {
    setStepCompleted(stepId, !currentlyCompleted)
  }

  suspend fun resetAllProgress() {
    dao.clearAllProgress()
  }
}
