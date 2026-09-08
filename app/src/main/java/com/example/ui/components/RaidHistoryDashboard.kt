package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.local.RaidHistoryEntity
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * RaidHistoryDashboard: Displays session statistics, highest round records,
 * Easter Egg completion rate, and logs previous raids stored in the Room database.
 */
@Composable
fun RaidHistoryDashboard(
  historyList: List<RaidHistoryEntity>,
  currentRound: Int,
  completedStepsCount: Int,
  totalStepsCount: Int,
  cleansedTemplesCount: Int,
  onRecordCurrentRun: (isExfilSuccess: Boolean, notes: String) -> Unit,
  onDeleteRecord: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var showRecordDialog by remember { mutableStateOf(false) }
  var isExfilSuccessInput by remember { mutableStateOf(true) }
  var runNotesInput by remember { mutableStateOf("") }

  val highestRound = remember(historyList, currentRound) {
    val previousMax = historyList.maxOfOrNull { it.roundReached } ?: 1
    maxOf(previousMax, currentRound)
  }

  val totalExfilSuccessCount = remember(historyList) {
    historyList.count { it.isExfilSuccess }
  }

  val averageRound = remember(historyList) {
    if (historyList.isEmpty()) 0 else (historyList.map { it.roundReached }.average()).toInt()
  }

  val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

  Column(
    modifier = modifier.fillMaxWidth().padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Summary KPI Cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Highest Round KPI
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x333F2000),
        border = androidx.compose.foundation.BorderStroke(1.dp, HellfireAmber.copy(alpha = 0.5f)),
        modifier = Modifier.weight(1f)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = HellfireAmber, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.height(4.dp))
          Text("รอบสูงสุด (Best)", color = TextSecondary, fontSize = 10.sp)
          Text("ROUND $highestRound", color = HellfireAmber, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
      }

      // Successful Exfils KPI
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x33003010),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
        modifier = Modifier.weight(1f)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.height(4.dp))
          Text("หลบหนีสำเร็จ", color = TextSecondary, fontSize = 10.sp)
          Text("$totalExfilSuccessCount ครั้ง", color = SuccessGreen, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
      }

      // Total Raids Logged
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x331F2A4A),
        border = androidx.compose.foundation.BorderStroke(1.dp, PackAPunchCyan.copy(alpha = 0.5f)),
        modifier = Modifier.weight(1f)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Default.History, contentDescription = null, tint = PackAPunchCyan, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.height(4.dp))
          Text("บันทึกทั้งหมด", color = TextSecondary, fontSize = 10.sp)
          Text("${historyList.size} รอบ", color = PackAPunchCyan, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
      }
    }

    // Save Active Run Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF)),
      modifier = Modifier.fillMaxWidth().testTag("save_raid_card")
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = AetherPurple, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "บันทึกสถิติรอบปัจจุบัน (Active Run)",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Round $currentRound • เควสต์ $completedStepsCount/$totalStepsCount • วิหาร $cleansedTemplesCount/4",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }

          Button(
            onClick = { showRecordDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = AetherPurple, contentColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("open_save_raid_dialog_btn")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("บันทึกสถิติ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // History Log Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Assessment, contentDescription = null, tint = HellfireAmber, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "ประวัติการลงเรด (Raid History)",
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      if (averageRound > 0) {
        Text(
          text = "เฉลี่ย R$averageRound",
          color = TextSecondary,
          fontSize = 11.sp
        )
      }
    }

    // List of Recorded Raids
    if (historyList.isEmpty()) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "ยังไม่มีประวัติการเล่นที่บันทึกไว้",
            color = TextSecondary,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "กด 'บันทึกสถิติ' ด้านบนเพื่อเก็บสถิติจำนวนรอบและภารกิจที่ทำสำเร็จลงฐานข้อมูล Room",
            color = TextMuted,
            fontSize = 10.sp,
            lineHeight = 14.sp
          )
        }
      }
    } else {
      historyList.forEach { raid ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (raid.isExfilSuccess) SuccessGreen.copy(alpha = 0.4f) else Color(0x22FFFFFF)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (raid.isExfilSuccess) SuccessGreen.copy(alpha = 0.2f) else DangerRed.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = if (raid.isExfilSuccess) "EXFIL สำเร็จ" else "ตายในรอบ (KIA)",
                    color = if (raid.isExfilSuccess) SuccessGreen else DangerRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "ROUND ${raid.roundReached}",
                  color = HellfireAmber,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "ภารกิจหลัก: ${raid.stepsCompletedCount} ขั้น • 4 เสาวิหาร: ${raid.templesCleansedCount}/4",
                color = TextSecondary,
                fontSize = 11.sp
              )

              if (raid.notes.isNotBlank()) {
                Text(
                  text = "บันทึก: ${raid.notes}",
                  color = TextMuted,
                  fontSize = 10.sp
                )
              }

              Text(
                text = dateFormat.format(Date(raid.timestamp)),
                color = TextMuted,
                fontSize = 9.sp
              )
            }

            IconButton(
              onClick = { onDeleteRecord(raid.id) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "ลบ", tint = TextMuted, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }

  // Save Record Dialog
  if (showRecordDialog) {
    androidx.compose.ui.window.Dialog(onDismissRequest = { showRecordDialog = false }) {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, AetherPurple.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "บันทึกผลการเล่นรอบนี้",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "รอบที่ไปถึง: ROUND $currentRound (ทำสำเร็จ $completedStepsCount/$totalStepsCount ขั้น)",
            color = HellfireAmber,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Outcome Toggle
          Text("ผลการหลบหนี (Exfil):", color = TextSecondary, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isExfilSuccessInput) SuccessGreen.copy(alpha = 0.25f) else DarkSurfaceVariant,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isExfilSuccessInput) SuccessGreen else Color(0x22FFFFFF)
              ),
              modifier = Modifier
                .weight(1f)
                .clickable { isExfilSuccessInput = true }
            ) {
              Text(
                text = "✓ หลบหนีสำเร็จ",
                color = if (isExfilSuccessInput) SuccessGreen else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (!isExfilSuccessInput) DangerRed.copy(alpha = 0.25f) else DarkSurfaceVariant,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (!isExfilSuccessInput) DangerRed else Color(0x22FFFFFF)
              ),
              modifier = Modifier
                .weight(1f)
                .clickable { isExfilSuccessInput = false }
            ) {
              Text(
                text = "✕ ตายในรอบ (KIA)",
                color = if (!isExfilSuccessInput) DangerRed else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = runNotesInput,
            onValueChange = { runNotesInput = it },
            label = { Text("โน้ตเพิ่มเติม (เช่น ใช้ปืน Ray Gun, เล่นกับเพื่อน)", fontSize = 11.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AetherPurple,
              unfocusedBorderColor = Color(0x33FFFFFF),
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Button(
              onClick = { showRecordDialog = false },
              colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextMuted)
            ) {
              Text("ยกเลิก")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                onRecordCurrentRun(isExfilSuccessInput, runNotesInput)
                showRecordDialog = false
                runNotesInput = ""
              },
              colors = ButtonDefaults.buttonColors(containerColor = AetherPurple, contentColor = Color.White)
            ) {
              Text("บันทึก")
            }
          }
        }
      }
    }
  }
}
