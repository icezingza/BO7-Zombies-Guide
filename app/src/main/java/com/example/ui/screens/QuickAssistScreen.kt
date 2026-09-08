package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.QuickLocationGuide
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
fun QuickAssistScreen(
  modifier: Modifier = Modifier
) {
  var selectedSubTab by remember { mutableIntStateOf(0) } // 0: ถามระหว่างเล่น, 1: สูตรเหลือบเดียว
  var selectedLocationId by remember { mutableStateOf(QuestData.quickLocations.first().id) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    // Sub tabs
    TabRow(
      selectedTabIndex = selectedSubTab,
      containerColor = DarkSurface,
      contentColor = AetherPurple,
      indicator = { tabPositions ->
        SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
          color = AetherPurple
        )
      },
      modifier = Modifier.fillMaxWidth()
    ) {
      Tab(
        selected = selectedSubTab == 0,
        onClick = { selectedSubTab = 0 },
        text = {
          Text(
            "ถามระหว่างเล่น (Live Assist)",
            fontSize = 13.sp,
            fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal
          )
        },
        modifier = Modifier.testTag("subtab_live_assist")
      )
      Tab(
        selected = selectedSubTab == 1,
        onClick = { selectedSubTab = 1 },
        text = {
          Text(
            "สูตรเหลือบเดียว (Speed Run)",
            fontSize = 13.sp,
            fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal
          )
        },
        modifier = Modifier.testTag("subtab_speed_flow")
      )
    }

    if (selectedSubTab == 0) {
      LiveAssistContent(
        locations = QuestData.quickLocations,
        selectedLocationId = selectedLocationId,
        onLocationSelected = { selectedLocationId = it }
      )
    } else {
      SpeedFlowContent()
    }
  }
}

@Composable
fun LiveAssistContent(
  locations: List<QuickLocationGuide>,
  selectedLocationId: String,
  onLocationSelected: (String) -> Unit
) {
  val activeLocation = locations.find { it.id == selectedLocationId } ?: locations.first()

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // Quick Rule Alert
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x22B388FF),
        border = androidx.compose.foundation.BorderStroke(1.dp, AetherPurple.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.DirectionsRun,
            contentDescription = null,
            tint = HellfireAmber,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "กฎทอง: ถ้าหลงทางให้กลับมาที่ Nexus Forge ก่อนเสมอ!",
              color = HellfireAmber,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "เลือกพื้นที่ปัจจุบันด้านล่างเพื่อรับกลยุทธ์และวิธีเอาตัวรอดทันที",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // Location Selector Chips
    item {
      Text(
        text = "คุณกำลังอยู่ที่พื้นที่ไหน?",
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(locations) { loc ->
          val isSelected = loc.id == selectedLocationId
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) AetherPurple.copy(alpha = 0.25f) else DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) AetherPurple else Color(0x22B388FF)
            ),
            modifier = Modifier
              .clickable { onLocationSelected(loc.id) }
              .testTag("location_chip_${loc.id}")
          ) {
            Text(
              text = loc.areaName.split(" ")[0], // short name
              color = if (isSelected) AetherPurple else TextSecondary,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Active Location Tactical Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AetherPurple.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Title
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = PackAPunchCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = activeLocation.areaName,
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Black
            )
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(Color(0x22FFFFFF))
          )

          // 1. Look For
          Row(verticalAlignment = Alignment.Top) {
            Icon(
              imageVector = Icons.Default.Visibility,
              contentDescription = null,
              tint = AetherPurple,
              modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("มองหา (Look For):", color = AetherPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(activeLocation.lookFor, color = TextPrimary, fontSize = 13.sp)
            }
          }

          // 2. What to do
          Row(verticalAlignment = Alignment.Top) {
            Icon(
              imageVector = Icons.Default.CrisisAlert,
              contentDescription = null,
              tint = HellfireOrange,
              modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("ต้องทำ (What to do):", color = HellfireOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(activeLocation.whatToDo, color = TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
            }
          }

          // 3. Return to
          Row(verticalAlignment = Alignment.Top) {
            Icon(
              imageVector = Icons.Default.Loop,
              contentDescription = null,
              tint = PackAPunchCyan,
              modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("เสร็จแล้วกลับ (Return to):", color = PackAPunchCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(activeLocation.returnTo, color = TextSecondary, fontSize = 13.sp)
            }
          }

          // 4. Emergency Swarm Protocol
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = DangerRed.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier
                  .size(18.dp)
                  .padding(top = 1.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "🚨 ถ้าซอมบี้รุมในจุดนี้:",
                  color = WarningAmber,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = activeLocation.ifSwarmed,
                  color = TextPrimary,
                  fontSize = 12.sp,
                  lineHeight = 16.sp
                )
              }
            }
          }

          // 5. Key Items
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🎒 ไอเทมประจำจุด: ", color = TextMuted, fontSize = 11.sp)
            Text(activeLocation.keyItems, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun SpeedFlowContent() {
  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "⚡ เส้นทางจำสั้น / สูตรเหลือบเดียว",
            color = LightningGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "เหมาะสำหรับเปิดดูอย่างรวดเร็วระหว่างวิ่งเปลี่ยนรอบ ลำดับขั้นตอนตามเข็มนาฬิกา",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
      }
    }

    items(QuestData.speedFlowNodes.size) { index ->
      val (nodeName, nodeDetail) = QuestData.speedFlowNodes[index]
      val isLast = index == QuestData.speedFlowNodes.size - 1

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        // Timeline Dot & Line
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.width(28.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = if (isLast) DangerRed else AetherPurple,
            modifier = Modifier.size(12.dp)
          ) {}
          if (!isLast) {
            Box(
              modifier = Modifier
                .width(2.dp)
                .height(48.dp)
                .background(Color(0x33B388FF))
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Content
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = DarkSurfaceVariant,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "${index + 1}. $nodeName",
              color = if (isLast) DangerRed else PackAPunchCyan,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = nodeDetail,
              color = TextPrimary,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          }
        }
      }
    }
  }
}
