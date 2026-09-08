package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuestData
import com.example.data.QuestStep
import com.example.data.StepCategory
import com.example.data.local.AppDatabase
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
    AppDatabase.getDatabase(application).questStepProgressDao()
  )
) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(GuideUiState())
  val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      repository.allProgress.collect { progressList ->
        val completedIds = progressList.filter { it.isCompleted }.map { it.stepId }.toSet()
        _uiState.update { it.copy(completedSteps = completedIds) }
      }
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
  }

  fun toggleTempleLightning(templeId: String) {
    _uiState.update { state ->
      val currentVal = state.templeLightning[templeId] ?: false
      val updated = state.templeLightning.toMutableMap()
      updated[templeId] = !currentVal
      state.copy(templeLightning = updated)
    }
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
  }

  fun resetAllProgress() {
    viewModelScope.launch {
      repository.resetAllProgress()
    }
    _uiState.update {
      it.copy(
        completedSteps = emptySet(),
        currentCubeStepIndex = 0,
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
