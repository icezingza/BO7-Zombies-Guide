package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.GameSessionDao
import com.example.data.local.GameSessionEntity
import com.example.data.local.QuestProgressRepository
import com.example.data.local.QuestStepProgressDao
import com.example.data.local.QuestStepProgressEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QuestStepProgressRoomTest {

  private lateinit var database: AppDatabase
  private lateinit var questDao: QuestStepProgressDao
  private lateinit var sessionDao: GameSessionDao
  private lateinit var repository: QuestProgressRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    questDao = database.questStepProgressDao()
    sessionDao = database.gameSessionDao()
    repository = QuestProgressRepository(questDao, sessionDao)
  }

  @After
  fun teardown() {
    database.close()
  }

  @Test
  fun testInsertAndRetrieveProgress() = runTest {
    questDao.insertOrUpdateProgress(
      QuestStepProgressEntity(stepId = 1, isCompleted = true, completedTimestamp = 123456789L)
    )

    val item = questDao.getProgressByStepId(1)
    assertNotNull(item)
    assertTrue(item!!.isCompleted)
    assertEquals(123456789L, item.completedTimestamp)
  }

  @Test
  fun testToggleStepViaRepository() = runTest {
    repository.setStepCompleted(stepId = 2, isCompleted = true)
    var list = repository.allProgress.first()
    assertEquals(1, list.size)
    assertTrue(list[0].isCompleted)

    repository.toggleStep(stepId = 2, currentlyCompleted = true)
    list = repository.allProgress.first()
    assertEquals(1, list.size)
    assertFalse(list[0].isCompleted)
  }

  @Test
  fun testGameSessionPersistence() = runTest {
    val session = GameSessionEntity(
      id = 1,
      currentRound = 15,
      houseSymbol0 = "T-Cross",
      houseSymbol1 = "Omega",
      houseSymbol2 = "Eye",
      houseSymbol3 = "Dragon",
      lightningDravakar = true,
      lightningNyxara = false,
      lightningCaltheris = true,
      lightningVeytharion = false,
      keepScreenOn = true
    )
    repository.saveSession(session)

    val saved = sessionDao.getSessionSync()
    assertNotNull(saved)
    assertEquals(15, saved!!.currentRound)
    assertEquals("T-Cross", saved.houseSymbol0)
    assertTrue(saved.lightningDravakar)
    assertFalse(saved.lightningNyxara)
    assertTrue(saved.keepScreenOn)
  }

  @Test
  fun testResetAllProgress() = runTest {
    repository.setStepCompleted(stepId = 1, isCompleted = true)
    repository.setStepCompleted(stepId = 2, isCompleted = true)
    repository.saveSession(GameSessionEntity(id = 1, currentRound = 22))
    assertEquals(2, repository.allProgress.first().size)

    repository.resetAllProgress()
    assertEquals(0, repository.allProgress.first().size)
    val session = sessionDao.getSessionSync()
    assertEquals(null, session)
  }
}
