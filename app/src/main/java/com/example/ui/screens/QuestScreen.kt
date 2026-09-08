package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuestStep
import com.example.data.StepCategory
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.HellfireAmber
import com.example.ui.theme.HellfireOrange
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun QuestScreen(
  steps: List<QuestStep>,
  completedStepIds: Set<Int>,
  expandedStepId: Int?,
  selectedCategory: StepCategory,
  searchQuery: String,
  onCategorySelected: (StepCategory) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onStepToggled: (Int) -> Unit,
  onStepExpandToggled: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val quickTags = listOf("PaP", "Cube", "Blight", "Void Claw", "Nexus", "Rex Infernus")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchQueryChanged,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
        .testTag("quest_search_input"),
      placeholder = {
        Text("ค้นหาขั้นตอน, อาวุธ, ปริศนา หรือพื้นที่...", color = TextMuted, fontSize = 14.sp)
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "ค้นหา",
          tint = AetherPurple
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchQueryChanged("") }) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "ล้างการค้นหา",
              tint = TextMuted
            )
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(14.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AetherPurple,
        unfocusedBorderColor = Color(0x33B388FF),
        focusedContainerColor = DarkSurfaceVariant,
        unfocusedContainerColor = DarkSurfaceVariant,
        cursorColor = AetherPurple,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      )
    )

    // Quick Keyword Tag Row
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(quickTags) { tag ->
        val isActive = searchQuery.equals(tag, ignoreCase = true)
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isActive) PackAPunchCyan.copy(alpha = 0.25f) else Color(0x221E1B2E),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) PackAPunchCyan else Color(0x2280D8FF)
          ),
          modifier = Modifier
            .clickable {
              if (isActive) onSearchQueryChanged("") else onSearchQueryChanged(tag)
            }
        ) {
          Text(
            text = "#$tag",
            color = if (isActive) PackAPunchCyan else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Category Filter Chips
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(StepCategory.values()) { category ->
        val isSelected = category == selectedCategory
        FilterChip(
          selected = isSelected,
          onClick = { onCategorySelected(category) },
          label = {
            Text(
              text = category.title,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AetherPurple.copy(alpha = 0.25f),
            selectedLabelColor = AetherPurple,
            containerColor = DarkSurfaceVariant,
            labelColor = TextSecondary
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) AetherPurple else Color(0x22B388FF),
            selectedBorderColor = AetherPurple,
            borderWidth = 1.dp
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("filter_chip_${category.name.lowercase()}")
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Step List
    if (steps.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "ไม่พบขั้นตอนที่ตรงกับคำค้นหา",
            color = TextSecondary,
            fontSize = 15.sp
          )
          Text(
            text = "ลองพิมพ์คำอื่น เช่น PaP, Blight, Whispers หรือ Cube",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(steps, key = { it.id }) { step ->
          val isCompleted = completedStepIds.contains(step.id)
          val isExpanded = expandedStepId == step.id

          QuestStepCard(
            step = step,
            isCompleted = isCompleted,
            isExpanded = isExpanded,
            onToggleComplete = { onStepToggled(step.id) },
            onToggleExpand = { onStepExpandToggled(step.id) }
          )
        }
      }
    }
  }
}

@Composable
fun QuestStepCard(
  step: QuestStep,
  isCompleted: Boolean,
  isExpanded: Boolean,
  onToggleComplete: () -> Unit,
  onToggleExpand: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCompleted) Color(0xFF16231D) else DarkSurfaceCard
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = when {
        isCompleted -> SuccessGreen.copy(alpha = 0.5f)
        isExpanded -> AetherPurple.copy(alpha = 0.7f)
        else -> Color(0x33B388FF)
      }
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag("step_card_${step.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top Header Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleExpand() },
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Step Number Badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = when {
            isCompleted -> SuccessGreen.copy(alpha = 0.2f)
            step.id == 17 -> DangerRed.copy(alpha = 0.2f)
            else -> AetherPurple.copy(alpha = 0.2f)
          },
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
              isCompleted -> SuccessGreen
              step.id == 17 -> DangerRed
              else -> AetherPurple
            }
          ),
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            if (isCompleted) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "สำเร็จแล้ว",
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
              )
            } else {
              Text(
                text = "${step.id}",
                color = if (step.id == 17) DangerRed else AetherPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Title and Area
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = step.title,
            color = if (isCompleted) TextSecondary else TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(2.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = PackAPunchCyan,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = step.area,
              color = PackAPunchCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Completion Checkbox
        Checkbox(
          checked = isCompleted,
          onCheckedChange = { onToggleComplete() },
          colors = CheckboxDefaults.colors(
            checkedColor = SuccessGreen,
            uncheckedColor = TextMuted,
            checkmarkColor = DarkBackground
          ),
          modifier = Modifier.testTag("step_checkbox_${step.id}")
        )

        // Expand Icon
        IconButton(
          onClick = onToggleExpand,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "ย่อ" else "ขยาย",
            tint = TextSecondary
          )
        }
      }

      // Expandable Content
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(Color(0x22FFFFFF))
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Visual Cue & Route
          Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
              Text("📍 จุดสังเกต:", color = AetherPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(step.visualCue, color = TextPrimary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text("🧭 ไปจาก:", color = HellfireAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(step.routeFrom, color = TextPrimary, fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Instructions List
          Text("⚔️ สิ่งที่ต้องทำ:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          step.instructions.forEach { instruction ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text("• ", color = AetherPurple, fontWeight = FontWeight.Black, fontSize = 13.sp)
              Text(
                text = instruction,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // On Completed
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x2200E676),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("✨ เมื่อสำเร็จ: ", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(step.onCompleted, color = TextPrimary, fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Emergency Swarm Warning Box
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = DangerRed.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier
                  .size(16.dp)
                  .padding(top = 1.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = "ถ้าซอมบี้รุม (Emergency Tactic):",
                  color = WarningAmber,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = step.emergencySwarm,
                  color = TextPrimary,
                  fontSize = 12.sp,
                  lineHeight = 16.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Check before moving forward
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("🔍 เช็กก่อนเดินต่อ: ", color = PackAPunchCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(step.checkBeforeNext, color = TextSecondary, fontSize = 12.sp)
            }
          }

          // Optional Note
          if (step.note != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = HellfireOrange.copy(alpha = 0.12f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.Top
              ) {
                Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = null,
                  tint = HellfireAmber,
                  modifier = Modifier
                    .size(14.dp)
                    .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = step.note,
                  color = HellfireAmber,
                  fontSize = 11.sp,
                  lineHeight = 15.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
