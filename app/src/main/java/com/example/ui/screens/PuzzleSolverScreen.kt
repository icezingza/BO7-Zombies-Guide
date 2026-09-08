package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuestData
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.HellfireAmber
import com.example.ui.theme.HellfireOrange
import com.example.ui.theme.LightningGold
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun PuzzleSolverScreen(
  currentCubeStep: Int,
  houseSymbols: List<String>,
  templeLightning: Map<String, Boolean>,
  onNextCubeStep: () -> Unit,
  onPrevCubeStep: () -> Unit,
  onResetCube: () -> Unit,
  onUpdateHouseSymbol: (Int, String) -> Unit,
  onToggleLightning: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Cube Solver, 1: House Symbols, 2: 4 Temples

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = DarkSurface,
      contentColor = AetherPurple,
      indicator = { tabPositions ->
        SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = AetherPurple
        )
      },
      modifier = Modifier.fillMaxWidth()
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            "Veytharion Cube",
            fontSize = 12.sp,
            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
          )
        },
        modifier = Modifier.testTag("tab_cube_solver")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            "สัญลักษณ์บ้าน (Exfil)",
            fontSize = 12.sp,
            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
          )
        },
        modifier = Modifier.testTag("tab_house_symbols")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Text(
            "ชำระล้าง 4 วิหาร",
            fontSize = 12.sp,
            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
          )
        },
        modifier = Modifier.testTag("tab_temple_matrix")
      )
    }

    when (selectedTab) {
      0 -> VeytharionCubeTab(
        currentStep = currentCubeStep,
        onNext = onNextCubeStep,
        onPrev = onPrevCubeStep,
        onReset = onResetCube
      )
      1 -> HouseSymbolsTab(
        symbols = houseSymbols,
        onUpdateSymbol = onUpdateHouseSymbol
      )
      2 -> TemplesCleansingTab(
        templeLightning = templeLightning,
        onToggleLightning = onToggleLightning
      )
    }
  }
}

@Composable
fun VeytharionCubeTab(
  currentStep: Int,
  onNext: () -> Unit,
  onPrev: () -> Unit,
  onReset: () -> Unit
) {
  val moves = QuestData.cubeMoves
  val activeMove = moves.getOrElse(currentStep) { moves.first() }
  var showFullFormula by remember { mutableStateOf(false) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // Danger Rule Banner
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = DangerRed.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = DangerRed,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "กฎสำคัญที่สุดของ Cube:",
              color = DangerRed,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "ห้ามให้ Fire (ไฟ) สัมผัสกับ Water (น้ำ) หรือ Flower (ดอกไม้) เด็ดขาด!",
              color = TextPrimary,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    // Interactive Step Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, AetherPurple.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Step Counter Pill
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = AetherPurple.copy(alpha = 0.2f),
              border = androidx.compose.foundation.BorderStroke(1.dp, AetherPurple)
            ) {
              Text(
                text = "ขั้นตอนที่ ${currentStep + 1} / ${moves.size}",
                color = AetherPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }

            IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "เริ่มใหม่",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Big Action Code Display
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF)),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp)
          ) {
            Box(
              modifier = Modifier.padding(vertical = 18.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = activeMove.code,
                color = if (currentStep == moves.size - 1) SuccessGreen else LightningGold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Move Description
          Text(
            text = activeMove.description,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = activeMove.ruleCheck,
            color = TextMuted,
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Next / Prev Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            OutlinedButton(
              onClick = onPrev,
              enabled = currentStep > 0,
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("cube_prev_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextPrimary,
                disabledContentColor = TextMuted
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (currentStep > 0) Color(0x66B388FF) else Color(0x22FFFFFF)
              )
            ) {
              Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("ก่อนหน้า")
            }

            Button(
              onClick = onNext,
              enabled = currentStep < moves.size - 1,
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("cube_next_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = AetherPurple,
                contentColor = DarkBackground,
                disabledContainerColor = DarkSurfaceVariant,
                disabledContentColor = TextMuted
              )
            ) {
              Text("ถัดไป", fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }

    // Toggle full formula button
    item {
      OutlinedButton(
        onClick = { showFullFormula = !showFullFormula },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PackAPunchCyan)
      ) {
        Icon(Icons.Default.Extension, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (showFullFormula) "ซ่อนสูตรทั้งหมด 24 ขั้น" else "ดูสูตรย่อทั้งหมด 24 ขั้น")
      }
    }

    // Full 24 steps formula expansion
    if (showFullFormula) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "สูตรแบบเต็ม (Full Sequence):",
              color = PackAPunchCyan,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            moves.forEach { move ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${move.stepNumber}. ",
                  color = AetherPurple,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.width(26.dp)
                )
                Text(
                  text = move.code,
                  color = LightningGold,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.width(80.dp)
                )
                Text(
                  text = move.description,
                  color = TextSecondary,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun HouseSymbolsTab(
  symbols: List<String>,
  onUpdateSymbol: (Int, String) -> Unit
) {
  val presetSymbols = listOf(
    "กะโหลก (Skull)", "พระจันทร์ (Moon)", "ดวงอาทิตย์ (Sun)",
    "งูพิษ (Serpent)", "เปลวไฟ (Flame)", "มีดสั้น (Dagger)",
    "มงกุฎ (Crown)", "ดวงตา (Eye)"
  )
  var activeSlotIndex by remember { mutableIntStateOf(0) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x2200E5FF),
        border = androidx.compose.foundation.BorderStroke(1.dp, PackAPunchCyan.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = PackAPunchCyan,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "บันทึกลำดับสัญลักษณ์สีน้ำเงิน (Step 11B)",
              color = PackAPunchCyan,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "คำตอบสุ่มทุกเกม! จดลำดับ 1 → 2 → 3 → 4 ไว้เพื่อยิงตอน Exfil Round",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // 4 Slots Row
    item {
      Text(
        text = "ลำดับการยิง (1 → 2 → 3 → 4):",
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        for (i in 0..3) {
          val isSelected = activeSlotIndex == i
          val symbolValue = symbols.getOrElse(i) { "" }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) AetherPurple.copy(alpha = 0.25f) else DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) AetherPurple else Color(0x33FFFFFF)
            ),
            modifier = Modifier
              .weight(1f)
              .clickable { activeSlotIndex = i }
              .testTag("symbol_slot_$i")
          ) {
            Column(
              modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "ลำดับ #${i + 1}",
                color = if (isSelected) AetherPurple else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = if (symbolValue.isNotEmpty()) symbolValue.split(" ")[0] else "ว่าง",
                color = if (symbolValue.isNotEmpty()) LightningGold else TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }

    // Preset Selector
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "เลือกสัญลักษณ์สำหรับ [ลำดับ #${activeSlotIndex + 1}]:",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))
          presetSymbols.chunked(2).forEach { rowSymbols ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowSymbols.forEach { sym ->
                OutlinedButton(
                  onClick = {
                    onUpdateSymbol(activeSlotIndex, sym)
                    if (activeSlotIndex < 3) activeSlotIndex++
                  },
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary
                  ),
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF))
                ) {
                  Text(sym, fontSize = 11.sp, maxLines = 1)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Clear button for this slot
          OutlinedButton(
            onClick = { onUpdateSymbol(activeSlotIndex, "") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
          ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("ล้างลำดับ #${activeSlotIndex + 1}")
          }
        }
      }
    }
  }
}

@Composable
fun TemplesCleansingTab(
  templeLightning: Map<String, Boolean>,
  onToggleLightning: (String) -> Unit
) {
  val temples = QuestData.temples

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // Formula Header
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, LightningGold.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "สูตรกลางการชำระล้างวิหาร:",
            color = LightningGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Item → เผา Brazier → เอา Astral Flame → ยิงมอน Mist → ลากเข้า Titan Trap → ยืน Plate → ยิง Charged Blight สวน Beam → ฆ่าเติม Soul → ยิงหน้า Statue → Lockdown ฆ่า 4 Shadow Souls → ยิงหน้า Statue → Purple Orb → ฝนตก → Astral Flame → ฟ้าผ่า ⚡",
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
        }
      }
    }

    // 4 Temple Cards
    items(temples) { temple ->
      val isLightningDone = templeLightning[temple.id] ?: false

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isLightningDone) Color(0xFF1B2319) else DarkSurfaceCard
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isLightningDone) LightningGold else Color(0x33B388FF)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Temple Name + Lightning Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = temple.name,
              color = if (isLightningDone) LightningGold else TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black
            )

            // Lightning Toggle Button
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isLightningDone) LightningGold.copy(alpha = 0.25f) else DarkSurfaceVariant,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isLightningDone) LightningGold else Color(0x44FFFFFF)
              ),
              modifier = Modifier
                .clickable { onToggleLightning(temple.id) }
                .testTag("temple_lightning_btn_${temple.id}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Bolt,
                  contentDescription = null,
                  tint = if (isLightningDone) LightningGold else TextMuted,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isLightningDone) "ฟ้าผ่าแล้ว" else "ยังไม่ผ่า",
                  color = if (isLightningDone) LightningGold else TextMuted,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(Color(0x22FFFFFF))
          )

          // Item Info
          Row {
            Text("🗡️ ไอเทม: ", color = AetherPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(temple.itemName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Text(temple.itemMethod, color = TextSecondary, fontSize = 11.sp)

          // Trap & Plate
          Row {
            Text("🪤 Titan Trap: ", color = HellfireAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(temple.titanSide, color = TextPrimary, fontSize = 12.sp)
          }

          Row {
            Text("👣 ท่าบน Plate: ", color = PackAPunchCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(temple.plateAction, color = TextPrimary, fontSize = 12.sp)
          }

          Row {
            Text("🎯 เล็ง Beam: ", color = LightningGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(temple.beamTarget, color = TextPrimary, fontSize = 12.sp)
          }

          // Inspection Alert
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x15FFFFFF),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "⚠️ หมายเหตุ: ${temple.inspectionAlert}",
              color = TextMuted,
              fontSize = 10.sp,
              modifier = Modifier.padding(6.dp)
            )
          }
        }
      }
    }
  }
}
