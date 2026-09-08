package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database instance for Rex Infernus quest tracker.
 */
@Database(
  entities = [
    QuestStepProgressEntity::class,
    GameSessionEntity::class,
    RaidHistoryEntity::class
  ],
  version = 3,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun questStepProgressDao(): QuestStepProgressDao
  abstract fun gameSessionDao(): GameSessionDao
  abstract fun raidHistoryDao(): RaidHistoryDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "rex_infernus_quest_db"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
