package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuestData
import com.example.ui.components.HeroBanner
import com.example.ui.screens.BossAndLoadoutScreen
import com.example.ui.screens.PuzzleSolverScreen
import com.example.ui.screens.QuestScreen
import com.example.ui.screens.QuickAssistScreen
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ZombiesGuideViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: ZombiesGuideViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val uiState by viewModel.uiState.collectAsState()
        var showResetDialog by remember { mutableStateOf(false) }

        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
          bottomBar = {
            NavigationBar(
              containerColor = DarkSurface,
              modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .testTag("bottom_nav_bar")
            ) {
              NavigationBarItem(
                selected = uiState.activeTab == 0,
                onClick = { viewModel.setActiveTab(0) },
                icon = { Icon(Icons.Default.Assignment, contentDescription = "เควสหลัก") },
                label = { Text("เควสหลัก", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = DarkBackground,
                  selectedTextColor = AetherPurple,
                  indicatorColor = AetherPurple,
                  unselectedIconColor = TextMuted,
                  unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_item_quest")
              )

              NavigationBarItem(
                selected = uiState.activeTab == 1,
                onClick = { viewModel.setActiveTab(1) },
                icon = { Icon(Icons.Default.FlashOn, contentDescription = "เร่งด่วน") },
                label = { Text("เร่งด่วน", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = DarkBackground,
                  selectedTextColor = PackAPunchCyan,
                  indicatorColor = PackAPunchCyan,
                  unselectedIconColor = TextMuted,
                  unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_item_quick")
              )

              NavigationBarItem(
                selected = uiState.activeTab == 2,
                onClick = { viewModel.setActiveTab(2) },
                icon = { Icon(Icons.Default.Extension, contentDescription = "พัซเซิล") },
                label = { Text("พัซเซิล", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = DarkBackground,
                  selectedTextColor = AetherPurple,
                  indicatorColor = AetherPurple,
                  unselectedIconColor = TextMuted,
                  unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_item_puzzle")
              )

              NavigationBarItem(
                selected = uiState.activeTab == 3,
                onClick = { viewModel.setActiveTab(3) },
                icon = { Icon(Icons.Default.Security, contentDescription = "บอส & ของ") },
                label = { Text("บอส & ของ", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = DarkBackground,
                  selectedTextColor = Color(0xFFFF6D00),
                  indicatorColor = Color(0xFFFF6D00),
                  unselectedIconColor = TextMuted,
                  unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_item_boss")
              )
            }
          }
        ) { innerPadding ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
              .statusBarsPadding()
          ) {
            // Header Hero Banner with live quest progress
            HeroBanner(
              completedStepsCount = uiState.completedSteps.size,
              totalStepsCount = QuestData.steps.size,
              lightningCount = uiState.templeLightning.values.count { it },
              onResetClicked = { showResetDialog = true }
            )

            // Screen content based on activeTab
            Box(modifier = Modifier.weight(1f)) {
              when (uiState.activeTab) {
                0 -> QuestScreen(
                  steps = viewModel.filterSteps(),
                  completedStepIds = uiState.completedSteps,
                  expandedStepId = uiState.expandedStepId,
                  selectedCategory = uiState.selectedCategory,
                  searchQuery = uiState.searchQuery,
                  onCategorySelected = { viewModel.setCategory(it) },
                  onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                  onStepToggled = { viewModel.toggleStepCompletion(it) },
                  onStepExpandToggled = { viewModel.toggleStepExpand(it) }
                )
                1 -> QuickAssistScreen()
                2 -> PuzzleSolverScreen(
                  currentCubeStep = uiState.currentCubeStepIndex,
                  houseSymbols = uiState.houseSymbols,
                  templeLightning = uiState.templeLightning,
                  onNextCubeStep = { viewModel.nextCubeStep() },
                  onPrevCubeStep = { viewModel.prevCubeStep() },
                  onResetCube = { viewModel.resetCubeSteps() },
                  onUpdateHouseSymbol = { index, symbol -> viewModel.updateHouseSymbol(index, symbol) },
                  onToggleLightning = { viewModel.toggleTempleLightning(it) }
                )
                3 -> BossAndLoadoutScreen(
                  checkedItems = uiState.checkedLoadoutItems,
                  onToggleItem = { viewModel.toggleLoadoutItem(it) }
                )
              }
            }
          }

          // Reset Progress Dialog
          if (showResetDialog) {
            AlertDialog(
              onDismissRequest = { showResetDialog = false },
              title = {
                Text(
                  text = "รีเซ็ตความคืบหน้ารอบนี้?",
                  color = TextPrimary,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              },
              text = {
                Text(
                  text = "ระบบจะล้างเครื่องหมายขั้นตอนที่ทำสำเร็จ สัญลักษณ์บ้าน และสถานะสายฟ้าวิหาร เพื่อเริ่มรอบใหม่ (New Game)",
                  color = TextSecondary,
                  fontSize = 13.sp
                )
              },
              confirmButton = {
                TextButton(
                  onClick = {
                    viewModel.resetAllProgress()
                    showResetDialog = false
                  },
                  colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
                ) {
                  Text("รีเซ็ตทั้งหมด", fontWeight = FontWeight.Bold)
                }
              },
              dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                  Text("ยกเลิก", color = TextSecondary)
                }
              },
              containerColor = DarkSurfaceCard,
              shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
          }
        }
      }
    }
  }
}
