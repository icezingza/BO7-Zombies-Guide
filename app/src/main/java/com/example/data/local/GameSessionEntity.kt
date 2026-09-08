package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores the ongoing session data including:
 * - Current zombie round
 * - Exfil 4-symbol sequence
 * - 4 Temples lightning state
 * - Screen on preference
 * - Checked loadout items
 */
@Entity(tableName = "game_session_state")
data class GameSessionEntity(
  @PrimaryKey
  val id: Int = 1, // Singleton session row

  @ColumnInfo(name = "current_round")
  val currentRound: Int = 1,

  @ColumnInfo(name = "house_symbol_0")
  val houseSymbol0: String = "",

  @ColumnInfo(name = "house_symbol_1")
  val houseSymbol1: String = "",

  @ColumnInfo(name = "house_symbol_2")
  val houseSymbol2: String = "",

  @ColumnInfo(name = "house_symbol_3")
  val houseSymbol3: String = "",

  @ColumnInfo(name = "lightning_dravakar")
  val lightningDravakar: Boolean = false,

  @ColumnInfo(name = "lightning_nyxara")
  val lightningNyxara: Boolean = false,

  @ColumnInfo(name = "lightning_caltheris")
  val lightningCaltheris: Boolean = false,

  @ColumnInfo(name = "lightning_veytharion")
  val lightningVeytharion: Boolean = false,

  @ColumnInfo(name = "checked_loadout_csv")
  val checkedLoadoutCsv: String = "",

  @ColumnInfo(name = "keep_screen_on")
  val keepScreenOn: Boolean = true
)
