package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
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
  private lateinit var dao: QuestStepProgressDao
  private lateinit var repository: QuestProgressRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    dao = database.questStepProgressDao()
    repository = QuestProgressRepository(dao)
  }

  @After
  fun teardown() {
    database.close()
  }

  @Test
  fun testInsertAndRetrieveProgress() = runTest {
    dao.insertOrUpdateProgress(
      QuestStepProgressEntity(stepId = 1, isCompleted = true, completedTimestamp = 123456789L)
    )

    val item = dao.getProgressByStepId(1)
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
  fun testResetAllProgress() = runTest {
    repository.setStepCompleted(stepId = 1, isCompleted = true)
    repository.setStepCompleted(stepId = 2, isCompleted = true)
    assertEquals(2, repository.allProgress.first().size)

    repository.resetAllProgress()
    assertEquals(0, repository.allProgress.first().size)
  }
}
