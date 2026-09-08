package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun BossAndLoadoutScreen(
  checkedItems: Set<String>,
  onToggleItem: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: กลยุทธ์บอส Warden, 1: เช็กลิสต์ความพร้อม

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = DarkSurface,
      contentColor = HellfireOrange,
      indicator = { tabPositions ->
        SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = HellfireOrange
        )
      },
      modifier = Modifier.fillMaxWidth()
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            "กลยุทธ์บอส Warden",
            fontSize = 13.sp,
            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
          )
        },
        modifier = Modifier.testTag("tab_boss_guide")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            "เช็กลิสต์ของ (${checkedItems.size}/${QuestData.loadoutItems.size})",
            fontSize = 13.sp,
            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
          )
        },
        modifier = Modifier.testTag("tab_loadout_checklist")
      )
    }

    if (selectedTab == 0) {
      WardenBossGuideTab()
    } else {
      LoadoutChecklistTab(
        checkedItems = checkedItems,
        onToggleItem = onToggleItem
      )
    }
  }
}

@Composable
fun WardenBossGuideTab() {
  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // Weapon Allocation Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, HellfireOrange.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "สูตรจำอาวุธสำหรับสู้ Warden:",
            color = HellfireAmber,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
          )
          Spacer(modifier = Modifier.height(10.dp))

          // Blight rule
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = AetherPurple.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, AetherPurple.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
              Text("🟣 Warden's Blight", color = AetherPurple, fontWeight = FontWeight.Black, fontSize = 13.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("ยิง Skull / Shadow Soul / Stinger", color = TextPrimary, fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Mammoth rule
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = DangerRed.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
              Text("🔴 Mammoth (ปืนหลัก)", color = DangerRed, fontWeight = FontWeight.Black, fontSize = 13.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("ยิง Tail (หาง) & จุดอ่อน Weakpoint", color = TextPrimary, fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Phase 1 Card
    item {
      BossPhaseCard(
        phaseBadge = "Phase 1",
        title = "เปิดดาเมจจุดอ่อนหาง",
        steps = listOf(
          "1. ใช้ Warden's Blight ยิงทำลาย Dread Skull ของบอส",
          "2. เมื่อ Skull แตก → Warden จะเกิดอาการชะงัก (Stun)",
          "3. หางของบอสจะกางเปิดคริติคอลจุดอ่อน",
          "4. รีบสลับเป็น Mammoth กระหน่ำยิงอัดหางให้เร็วที่สุด",
          "5. เมื่อบอสฟื้นตัว ให้รีบวิ่งวนรักษาระยะห่างทันที"
        ),
        emergencyWarning = "ถ้าซอมบี้รุม: หยุด DPS บอสทันที หันมายิงเคลียร์มอนรอบตัวและรักษาระยะก่อน"
      )
    }

    // Immune Protocol Card
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF261214)),
        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Dangerous, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("เมื่อ Warden ขึ้นสถานะอมตะ (Immune)", color = DangerRed, fontSize = 14.sp, fontWeight = FontWeight.Black)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text("• หยุดยิง Warden ทันที! (ยิงไม่เข้า เปลืองกระสุน)", color = TextPrimary, fontSize = 12.sp)
          Text("• กวาดสายตามองหา Shadow Souls รอบสนามรบ", color = TextPrimary, fontSize = 12.sp)
          Text("• ใช้ Warden's Blight ฆ่า Shadow Souls ให้หมดทุกตัว", color = TextPrimary, fontSize = 12.sp)
          Text("• เมื่อวิญญาณหมด บอสจะปลดล็อก Immune กลับไปยิงกะโหลก Skull ต่อได้", color = TextSecondary, fontSize = 12.sp)
        }
      }
    }

    // Phase 2+ Card
    item {
      BossPhaseCard(
        phaseBadge = "Phase 2+",
        title = "Warden's Stingers & ความโกลาหล",
        steps = listOf(
          "บอสจะเริ่มปล่อยเหล็กใน Warden's Stingers กระจายทั่วสนาม",
          "ลำดับความสำคัญในการยิง:",
          "1. ถ้า Stinger สะสมเยอะ → ต้องรีบทำลายก่อน",
          "2. Shadow Souls → ฆ่าทันที",
          "3. Dread Skull → ใช้ Blight ยิงแตก",
          "4. หางเปิด → ใช้ Mammoth กระหน่ำยิง"
        ),
        emergencyWarning = "อย่าปล่อยให้ Stingers สะสมบนพื้น เพราะจะทำให้ไม่มีพื้นที่หลบกระสุน"
      )
    }

    // Warden Rush Survival
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("การรับมือเมื่อ Warden พุ่งชน (Rush Attack):", color = WarningAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text("• ห้ามวิ่งหนีเป็นเส้นตรง เพราะบอสเร็วกว่าตัวเรา", color = TextPrimary, fontSize = 12.sp)
          Text("• ใช้สลิง Void Claw / Void Talon Grapple ข้ามไปฝั่งตรงข้าม", color = TextPrimary, fontSize = 12.sp)
          Text("• ระวังอย่าสลิงออกนอกขอบสนามประลองจนตกเหว!", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Prison / Clausura
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("คุกกักขัง Prison / Clausura:", color = PackAPunchCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          Text("• อาจเกิดเงื่อนไขกักขัง: ต้องรีบฆ่ามอนสเตอร์ระดับ Elite หรือเอาตัวรอดตามเวลา", color = TextSecondary, fontSize = 12.sp)
          Text("• หากเล่น Co-op ผู้เล่นอาจถูกแยกขังคนละห้อง ต้องเอาตัวรอดจนประตูปดล็อก", color = TextSecondary, fontSize = 12.sp)
        }
      }
    }

    // Final Phase
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x2200E676),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("🏆 Phase สุดท้ายก่อนจบเควส:", color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Black)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "อย่าคิดว่าชนะเมื่อบอสล้มลงครั้งแรก! ทำลูปซ้ำ: Souls → Skull → Tail → วิ่ง จนกระทั่งคัตซีนจบขึ้นหน้าจอ!",
            color = TextPrimary,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

@Composable
fun BossPhaseCard(
  phaseBadge: String,
  title: String,
  steps: List<String>,
  emergencyWarning: String
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = AetherPurple.copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, AetherPurple)
        ) {
          Text(phaseBadge, color = AetherPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(10.dp))

      steps.forEach { step ->
        Text(step, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.padding(vertical = 2.dp))
      }

      Spacer(modifier = Modifier.height(8.dp))

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = WarningAmber.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "⚠️ $emergencyWarning",
          color = WarningAmber,
          fontSize = 11.sp,
          modifier = Modifier.padding(8.dp)
        )
      }
    }
  }
}

@Composable
fun LoadoutChecklistTab(
  checkedItems: Set<String>,
  onToggleItem: (String) -> Unit
) {
  val items = QuestData.loadoutItems
  val essentialCount = items.count { it.isBossEssential }
  val checkedEssentialCount = items.count { it.isBossEssential && checkedItems.contains(it.id) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // Readiness Summary Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (checkedEssentialCount == essentialCount) SuccessGreen else HellfireOrange),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ความพร้อมก่อนลงสู่ Nexus Core",
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (checkedEssentialCount == essentialCount) "พร้อมสู้ 100%" else "ยังไม่ครบ",
              color = if (checkedEssentialCount == essentialCount) SuccessGreen else HellfireAmber,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "ไอเทมจำเป็น: $checkedEssentialCount / $essentialCount อย่าง | รวมทั้งหมด: ${checkedItems.size} / ${items.size}",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
      }
    }

    // Loadout checklist items
    items(items) { item ->
      val isChecked = checkedItems.contains(item.id)

      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isChecked) Color(0xFF15201A) else DarkSurfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isChecked) SuccessGreen.copy(alpha = 0.5f) else Color(0x22B388FF)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggleItem(item.id) },
            colors = CheckboxDefaults.colors(
              checkedColor = SuccessGreen,
              uncheckedColor = TextMuted,
              checkmarkColor = DarkBackground
            ),
            modifier = Modifier.testTag("loadout_checkbox_${item.id}")
          )

          Spacer(modifier = Modifier.width(8.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = item.name,
                color = if (isChecked) TextSecondary else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              if (item.isBossEssential) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = DangerRed.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = "จำเป็น",
                    color = DangerRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = item.detail,
              color = TextMuted,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  }
}
