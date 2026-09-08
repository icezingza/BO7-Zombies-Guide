package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuestData
import com.example.data.QuestStep
import com.example.data.StepCategory
import com.example.data.local.AppDatabase
import com.example.data.local.GameSessionEntity
import com.example.data.local.QuestProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GuideUiState(
  val completedSteps: Set<Int> = emptySet(),
  val selectedCategory: StepCategory = StepCategory.ALL,
  val searchQuery: String = "",
  val expandedStepId: Int? = null,
  val currentCubeStepIndex: Int = 0,
  val currentRound: Int = 1,
  val keepScreenOn: Boolean = true,
  val houseSymbols: List<String> = listOf("", "", "", ""),
  val templeLightning: Map<String, Boolean> = mapOf(
    "dravakar" to false,
    "nyxara" to false,
    "caltheris" to false,
    "veytharion" to false
  ),
  val checkedLoadoutItems: Set<String> = emptySet(),
  val activeTab: Int = 0 // 0: Main Quest, 1: Quick Assist, 2: Puzzle Solver, 3: Boss & Gear
)

class ZombiesGuideViewModel(
  application: Application,
  private val repository: QuestProgressRepository = QuestProgressRepository(
    AppDatabase.getDatabase(application).questStepProgressDao(),
    AppDatabase.getDatabase(application).gameSessionDao()
  )
) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(GuideUiState())
  val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

  init {
    // 1. Observe completed quest steps from Room
    viewModelScope.launch {
      repository.allProgress.collect { progressList ->
        val completedIds = progressList.filter { it.isCompleted }.map { it.stepId }.toSet()
        _uiState.update { it.copy(completedSteps = completedIds) }
      }
    }

    // 2. Observe game session states (Round, Exfil, Temples, Screen On) from Room
    viewModelScope.launch {
      repository.sessionState.collect { session ->
        if (session != null) {
          val checkedItems = if (session.checkedLoadoutCsv.isEmpty()) {
            emptySet()
          } else {
            session.checkedLoadoutCsv.split(",").filter { it.isNotEmpty() }.toSet()
          }
          _uiState.update {
            it.copy(
              currentRound = session.currentRound,
              keepScreenOn = session.keepScreenOn,
              houseSymbols = listOf(
                session.houseSymbol0,
                session.houseSymbol1,
                session.houseSymbol2,
                session.houseSymbol3
              ),
              templeLightning = mapOf(
                "dravakar" to session.lightningDravakar,
                "nyxara" to session.lightningNyxara,
                "caltheris" to session.lightningCaltheris,
                "veytharion" to session.lightningVeytharion
              ),
              checkedLoadoutItems = checkedItems
            )
          }
        }
      }
    }
  }

  private fun persistSession() {
    viewModelScope.launch {
      val state = _uiState.value
      val session = GameSessionEntity(
        id = 1,
        currentRound = state.currentRound,
        keepScreenOn = state.keepScreenOn,
        houseSymbol0 = state.houseSymbols.getOrElse(0) { "" },
        houseSymbol1 = state.houseSymbols.getOrElse(1) { "" },
        houseSymbol2 = state.houseSymbols.getOrElse(2) { "" },
        houseSymbol3 = state.houseSymbols.getOrElse(3) { "" },
        lightningDravakar = state.templeLightning["dravakar"] ?: false,
        lightningNyxara = state.templeLightning["nyxara"] ?: false,
        lightningCaltheris = state.templeLightning["caltheris"] ?: false,
        lightningVeytharion = state.templeLightning["veytharion"] ?: false,
        checkedLoadoutCsv = state.checkedLoadoutItems.joinToString(",")
      )
      repository.saveSession(session)
    }
  }

  fun setActiveTab(index: Int) {
    _uiState.update { it.copy(activeTab = index) }
  }

  fun setCategory(category: StepCategory) {
    _uiState.update { it.copy(selectedCategory = category) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun toggleKeepScreenOn() {
    _uiState.update { it.copy(keepScreenOn = !it.keepScreenOn) }
    persistSession()
  }

  fun incrementRound() {
    _uiState.update { it.copy(currentRound = (it.currentRound + 1).coerceAtMost(999)) }
    persistSession()
  }

  fun decrementRound() {
    _uiState.update { it.copy(currentRound = (it.currentRound - 1).coerceAtLeast(1)) }
    persistSession()
  }

  fun toggleStepCompletion(stepId: Int) {
    val isCurrentlyCompleted = _uiState.value.completedSteps.contains(stepId)
    viewModelScope.launch {
      repository.toggleStep(stepId, isCurrentlyCompleted)
    }
  }

  fun toggleStepExpand(stepId: Int) {
    _uiState.update { state ->
      val newExpanded = if (state.expandedStepId == stepId) null else stepId
      state.copy(expandedStepId = newExpanded)
    }
  }

  fun nextCubeStep() {
    _uiState.update { state ->
      val next = (state.currentCubeStepIndex + 1).coerceAtMost(QuestData.cubeMoves.size - 1)
      state.copy(currentCubeStepIndex = next)
    }
  }

  fun prevCubeStep() {
    _uiState.update { state ->
      val prev = (state.currentCubeStepIndex - 1).coerceAtLeast(0)
      state.copy(currentCubeStepIndex = prev)
    }
  }

  fun resetCubeSteps() {
    _uiState.update { it.copy(currentCubeStepIndex = 0) }
  }

  fun updateHouseSymbol(index: Int, symbol: String) {
    _uiState.update { state ->
      val updated = state.houseSymbols.toMutableList()
      if (index in updated.indices) {
        updated[index] = symbol
      }
      state.copy(houseSymbols = updated)
    }
    persistSession()
  }

  fun toggleTempleLightning(templeId: String) {
    _uiState.update { state ->
      val currentVal = state.templeLightning[templeId] ?: false
      val updated = state.templeLightning.toMutableMap()
      updated[templeId] = !currentVal
      state.copy(templeLightning = updated)
    }
    persistSession()
  }

  fun toggleLoadoutItem(itemId: String) {
    _uiState.update { state ->
      val newItems = if (state.checkedLoadoutItems.contains(itemId)) {
        state.checkedLoadoutItems - itemId
      } else {
        state.checkedLoadoutItems + itemId
      }
      state.copy(checkedLoadoutItems = newItems)
    }
    persistSession()
  }

  fun resetAllProgress() {
    viewModelScope.launch {
      repository.resetAllProgress()
    }
    _uiState.update {
      it.copy(
        completedSteps = emptySet(),
        currentCubeStepIndex = 0,
        currentRound = 1,
        houseSymbols = listOf("", "", "", ""),
        templeLightning = mapOf(
          "dravakar" to false,
          "nyxara" to false,
          "caltheris" to false,
          "veytharion" to false
        ),
        checkedLoadoutItems = emptySet()
      )
    }
  }

  fun filterSteps(): List<QuestStep> {
    val query = _uiState.value.searchQuery.trim().lowercase()
    val category = _uiState.value.selectedCategory

    return QuestData.steps.filter { step ->
      val matchesCat = (category == StepCategory.ALL || step.category == category)
      val matchesQuery = if (query.isEmpty()) true else {
        step.title.lowercase().contains(query) ||
        step.area.lowercase().contains(query) ||
        step.visualCue.lowercase().contains(query) ||
        step.emergencySwarm.lowercase().contains(query) ||
        step.instructions.any { it.lowercase().contains(query) }
      }
      matchesCat && matchesQuery
    }
  }
}
