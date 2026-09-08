package com.example.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern abstracting Room database operations for both
 * quest step progress and live session states (Round, Exfil Symbols, Temples, Screen On).
 */
class QuestProgressRepository(
  private val questDao: QuestStepProgressDao,
  private val sessionDao: GameSessionDao
) {

  val allProgress: Flow<List<QuestStepProgressEntity>> = questDao.getAllProgress()
  val completedProgress: Flow<List<QuestStepProgressEntity>> = questDao.getCompletedProgress()
  val sessionState: Flow<GameSessionEntity?> = sessionDao.getSession()

  suspend fun getProgressForStep(stepId: Int): QuestStepProgressEntity? {
    return questDao.getProgressByStepId(stepId)
  }

  suspend fun setStepCompleted(stepId: Int, isCompleted: Boolean) {
    val timestamp = if (isCompleted) System.currentTimeMillis() else null
    questDao.insertOrUpdateProgress(
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

  suspend fun saveSession(session: GameSessionEntity) {
    sessionDao.saveSession(session)
  }

  suspend fun resetAllProgress() {
    questDao.clearAllProgress()
    sessionDao.clearSession()
  }
}
