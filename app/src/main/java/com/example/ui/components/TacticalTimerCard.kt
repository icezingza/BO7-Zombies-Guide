package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
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
import kotlinx.coroutines.delay

data class TacticalTimerPreset(
  val id: String,
  val title: String,
  val totalSeconds: Int,
  val warningNote: String,
  val color: Color
)

val defaultTimerPresets = listOf(
  TacticalTimerPreset(
    id = "void_burst",
    title = "Void Burst (คูลดาวน์บอส)",
    totalSeconds = 45,
    warningNote = "บอสจะชาร์จ Void Burst ทุก 45 วิ! รีบหาเสาหลบหลังคูลดาวน์หมด",
    color = DangerRed
  ),
  TacticalTimerPreset(
    id = "pillar_overcharge",
    title = "เสาพลังงาน Overcharge",
    totalSeconds = 30,
    warningNote = "เสาจะปล่อยคลื่นแม่เหล็กไฟฟ้าทำดาเมจแรงสูง รีบถอยห่างก่อนหมดเวลา",
    color = HellfireOrange
  ),
  TacticalTimerPreset(
    id = "aether_rift",
    title = "ประตู Aether Rift ปิดตัว",
    totalSeconds = 60,
    warningNote = "ประตูข้ามมิติจะหายไปใน 60 วินาที ถ้าเก็บเศษหินไม่ทันต้องรอรอบใหม่",
    color = AetherPurple
  )
)

/**
 * TacticalTimerCard: Provides live round progression intelligence, zombie spawn count estimates,
 * and countdown timers for critical boss abilities and event resets with voice/audio warnings.
 */
@Composable
fun TacticalTimerCard(
  currentRound: Int,
  onIncrementRound: () -> Unit,
  onDecrementRound: () -> Unit,
  onSpeakWarning: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedPreset by remember { mutableStateOf(defaultTimerPresets.first()) }
  var remainingSeconds by remember { mutableIntStateOf(selectedPreset.totalSeconds) }
  var isTimerRunning by remember { mutableStateOf(false) }

  // Estimated zombies count for current round (classic BO formula approximation)
  val estimatedZombies = remember(currentRound) {
    val base = when {
      currentRound <= 1 -> 6
      currentRound == 2 -> 8
      currentRound == 3 -> 13
      currentRound == 4 -> 18
      currentRound == 5 -> 24
      else -> (24 + (currentRound - 5) * 6).coerceAtMost(350)
    }
    base
  }

  val packMilestoneAdvice = when {
    currentRound < 5 -> "รอบต้น: เน้นยิงหัวสะสมพ้อยต์ 5,000 เพื่อเปิด Pack-a-Punch"
    currentRound in 5..9 -> "รอบกลาง: แนะนำอัปเกรด Pack Tier 1 + ซื้อ Juggernog ทันที"
    currentRound in 10..15 -> "รอบวิกฤต: อัปเกรด Pack Tier 2 + เคลียร์ 4 เสาวิหารให้ครบก่อนรอบ 15"
    else -> "รอบเลท: ต้องมี Pack Tier 3 + อาวุธมหัศจรรย์ Ray Gun / Wonder Weapon"
  }

  // Timer Tick Loop
  LaunchedEffect(isTimerRunning, remainingSeconds) {
    if (isTimerRunning && remainingSeconds > 0) {
      delay(1000L)
      remainingSeconds--
      if (remainingSeconds == 10) {
        onSpeakWarning("เตือน: อีก 10 วินาที ${selectedPreset.title}")
      } else if (remainingSeconds == 0) {
        isTimerRunning = false
        onSpeakWarning("ระวัง! สกิล ${selectedPreset.title} ทำงานแล้ว!")
      }
    }
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, HellfireAmber.copy(alpha = 0.4f)),
    modifier = modifier.fillMaxWidth().testTag("tactical_timer_card")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Round Tracker & Intelligence
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = HellfireAmber.copy(alpha = 0.2f),
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = HellfireAmber,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "TACTICAL ROUND & EVENT TIMERS",
              color = HellfireAmber,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.8.sp
            )
            Text(
              text = "ระบบจับเวลาสกิลบอสและประมาณการณ์รอบ",
              color = TextSecondary,
              fontSize = 10.sp
            )
          }
        }

        // Stepper for Current Round
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = DarkSurfaceVariant,
          border = androidx.compose.foundation.BorderStroke(1.dp, HellfireAmber.copy(alpha = 0.5f))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            IconButton(
              onClick = onDecrementRound,
              modifier = Modifier.size(24.dp).testTag("timer_round_minus")
            ) {
              Icon(Icons.Default.Remove, contentDescription = "ลดรอบ", tint = TextPrimary, modifier = Modifier.size(14.dp))
            }
            Text(
              text = "R$currentRound",
              color = HellfireAmber,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp)
            )
            IconButton(
              onClick = onIncrementRound,
              modifier = Modifier.size(24.dp).testTag("timer_round_plus")
            ) {
              Icon(Icons.Default.Add, contentDescription = "เพิ่มรอบ", tint = TextPrimary, modifier = Modifier.size(14.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Round Stat banner: estimated zombies & pack-a-punch guide
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0x221F2A4A),
        border = androidx.compose.foundation.BorderStroke(1.dp, PackAPunchCyan.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "ประมาณซอมบี้ในรอบนี้: ~$estimatedZombies ตัว",
                color = PackAPunchCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = packMilestoneAdvice,
              color = TextSecondary,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Preset Selector Chips
      Text(
        text = "เลือกสกิลหรืออีเวนต์ที่ต้องการจับเวลา:",
        color = TextPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        defaultTimerPresets.forEach { preset ->
          val isSelected = preset.id == selectedPreset.id
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) preset.color.copy(alpha = 0.25f) else DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) preset.color else Color(0x22FFFFFF)
            ),
            modifier = Modifier
              .weight(1f)
              .clickable {
                selectedPreset = preset
                remainingSeconds = preset.totalSeconds
                isTimerRunning = false
              }
          ) {
            Column(
              modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = preset.title.split(" ")[0],
                color = if (isSelected) preset.color else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
              Text(
                text = "${preset.totalSeconds}s",
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Live Countdown Display
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (remainingSeconds <= 10 && isTimerRunning) DangerRed else selectedPreset.color.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = selectedPreset.title,
              color = selectedPreset.color,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )

            val progress = remainingSeconds.toFloat() / selectedPreset.totalSeconds
            Text(
              text = "${remainingSeconds}s / ${selectedPreset.totalSeconds}s",
              color = if (remainingSeconds <= 10 && isTimerRunning) DangerRed else TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { remainingSeconds.toFloat() / selectedPreset.totalSeconds },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = if (remainingSeconds <= 10) DangerRed else selectedPreset.color,
            trackColor = Color(0x33FFFFFF)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = selectedPreset.warningNote,
            color = TextMuted,
            fontSize = 10.sp,
            lineHeight = 14.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Control buttons: Start/Pause, Reset, Audio Alert Test
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isTimerRunning) WarningAmber else SuccessGreen,
              modifier = Modifier
                .clickable {
                  if (remainingSeconds == 0) {
                    remainingSeconds = selectedPreset.totalSeconds
                  }
                  isTimerRunning = !isTimerRunning
                }
                .testTag("toggle_timer_run_btn")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (isTimerRunning) Icons.Default.HourglassBottom else Icons.Default.PlayArrow,
                  contentDescription = null,
                  tint = DarkSurface,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isTimerRunning) "หยุดชั่วคราว" else "เริ่มจับเวลา",
                  color = DarkSurface,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0x22FFFFFF),
              modifier = Modifier.clickable {
                isTimerRunning = false
                remainingSeconds = selectedPreset.totalSeconds
              }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("รีเซ็ต", color = TextPrimary, fontSize = 11.sp)
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = LightningGold.copy(alpha = 0.2f),
              modifier = Modifier.clickable {
                onSpeakWarning("ทดสอบเสียงเตือน: ${selectedPreset.title} คูลดาวน์เหลือ 10 วินาที")
              }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = LightningGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ฟังเสียง", color = LightningGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
