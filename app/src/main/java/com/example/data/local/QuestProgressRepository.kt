package com.example.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern abstracting Room database operations for both
 * quest step progress and live session states (Round, Exfil Symbols, Temples, Screen On).
 */
class QuestProgressRepository(
  private val questDao: QuestStepProgressDao,
  private val sessionDao: GameSessionDao,
  private val raidDao: RaidHistoryDao? = null
) {

  val allProgress: Flow<List<QuestStepProgressEntity>> = questDao.getAllProgress()
  val completedProgress: Flow<List<QuestStepProgressEntity>> = questDao.getCompletedProgress()
  val sessionState: Flow<GameSessionEntity?> = sessionDao.getSession()
  val allRaidHistory: Flow<List<RaidHistoryEntity>> = raidDao?.getAllRaidHistory() ?: kotlinx.coroutines.flow.flowOf(emptyList())

  suspend fun recordRaidSession(
    roundReached: Int,
    stepsCompletedCount: Int,
    templesCleansedCount: Int,
    isExfilSuccess: Boolean,
    durationMinutes: Int = 0,
    notes: String = ""
  ) {
    raidDao?.insertRaidHistory(
      RaidHistoryEntity(
        roundReached = roundReached,
        stepsCompletedCount = stepsCompletedCount,
        templesCleansedCount = templesCleansedCount,
        isExfilSuccess = isExfilSuccess,
        durationMinutes = durationMinutes,
        notes = notes
      )
    )
  }

  suspend fun deleteRaid(id: Long) {
    raidDao?.deleteRaidHistory(id)
  }

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
