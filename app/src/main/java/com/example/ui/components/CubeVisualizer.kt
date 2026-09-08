package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.HellfireOrange
import com.example.ui.theme.LightningGold
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class CubeRune(val symbol: String, val thName: String, val color: Color) {
  FIRE("🔥", "Fire (ไฟ)", HellfireOrange),
  HAND("✋", "Hand (มือ)", AetherPurple),
  WATER("💧", "Water (น้ำ)", PackAPunchCyan),
  FLOWER("🌸", "Flower (ดอกไม้)", Color(0xFFFF69B4)),
  NONE("·", "ว่าง", Color(0x33FFFFFF))
}

data class CubeCheckpoint(
  val number: Int,
  val stepIndex: Int,
  val label: String,
  val objective: String
)

val CUBE_CHECKPOINTS = listOf(
  CubeCheckpoint(1, 5, "CP 1: แยกไฟ", "แยก Fire ออกจากรางขวา"),
  CubeCheckpoint(2, 11, "CP 2: ล็อกน้ำ", "ส่ง Water ลงฐานรางล่าง"),
  CubeCheckpoint(3, 17, "CP 3: จัดดอกไม้", "ล็อก Flower ตำแหน่งปลอดภัย"),
  CubeCheckpoint(4, 23, "CP 4: สำเร็จ 100%", "ประตูกล PaP Crypt เปิดออก!")
)

data class CubeSlotState(
  val horizontalSlot: CubeRune = CubeRune.NONE,
  val leftTrackTop: CubeRune = CubeRune.NONE,
  val leftTrackBottom: CubeRune = CubeRune.NONE,
  val rightTrackTop: CubeRune = CubeRune.NONE,
  val rightTrackBottom: CubeRune = CubeRune.NONE,
  val activeArrowDescription: String = "สถานะตั้งต้น",
  val axis3dHint: String = "แกน Z: ขนานระนาบพื้น",
  val rotationCues: String = "สแตนด์บายลูกบาศก์ รอหมุนแกนแรก"
)

object CubeStateCalculator {
  // Approximate slot distribution at each of the 24 steps
  fun getStateForStep(stepIndex: Int): CubeSlotState {
    return when (stepIndex) {
      0 -> CubeSlotState(CubeRune.NONE, CubeRune.FIRE, CubeRune.HAND, CubeRune.WATER, CubeRune.FLOWER, "เริ่มต้น: F, H ในรางซ้าย / W, Fl ในรางขวา", "แกน X/Y: เริ่มต้น", "เตรียมบิดแกน X ขึ้น ⬆️")
      1 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.HAND, CubeRune.WATER, CubeRune.FLOWER, "F เลื่อนขึ้นรางแนวนอน ⬆️", "บิดแกน X ขึ้น 1 ครั้ง ⬆️", "เลื่อน Fire ขึ้นรางขนาน")
      2 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.WATER, CubeRune.FLOWER, "H เลื่อนขึ้นรางแนวนอน ⬆️", "บิดแกน X ขึ้น อีก 1 ครั้ง ⬆️", "Hand ตามขึ้นรางบน")
      3 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.FLOWER, CubeRune.WATER, "สลับตำแหน่งรางขวา 🔄", "หมุนแกน Y ขวา 1 ครั้ง ➡️", "สลับ Fl และ W ในรางขวา")
      4 -> CubeSlotState(CubeRune.NONE, CubeRune.NONE, CubeRune.HAND, CubeRune.FIRE, CubeRune.FLOWER, "F เลื่อนลงรางขวา ⬇️", "บิดแกน X ลง 1 ครั้ง ⬇️", "Fire ลงสู่รางขวาปลอดภัย")
      5 -> CubeSlotState(CubeRune.NONE, CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.FLOWER, "สลับตำแหน่งรางซ้าย 🔄 (ผ่าน CP 1)", "หมุนแกน Y ซ้าย 1 ครั้ง ⬅️", "Checkpoint 1 สำเร็จ! แยกไฟเรียบร้อย")
      6 -> CubeSlotState(CubeRune.WATER, CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.FLOWER, "W เลื่อนขึ้นรางแนวนอน ⬆️", "บิดแกน X ขึ้น 1 ครั้ง ⬆️", "Water ขึ้นรางแนวนอน")
      7 -> CubeSlotState(CubeRune.WATER, CubeRune.HAND, CubeRune.NONE, CubeRune.FLOWER, CubeRune.FIRE, "สลับตำแหน่งรางขวา 🔄", "หมุนแกน Y ขวา 1 ครั้ง ➡️", "สลับ Flower และ Fire")
      8 -> CubeSlotState(CubeRune.WATER, CubeRune.NONE, CubeRune.NONE, CubeRune.HAND, CubeRune.FLOWER, "H เลื่อนลงรางขวา ⬇️", "บิดแกน X ลง 1 ครั้ง ⬇️", "Hand ลงมาอยู่หน้า Fire")
      9 -> CubeSlotState(CubeRune.NONE, CubeRune.NONE, CubeRune.NONE, CubeRune.WATER, CubeRune.HAND, "W เลื่อนลงรางขวา ⬇️", "บิดแกน X ลง 1 ครั้ง ⬇️", "Water ประกบด้านล่าง")
      10 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.NONE, CubeRune.WATER, CubeRune.HAND, "F นำขึ้นรางแนวนอน ⬆️", "บิดแกน X ขึ้น 1 ครั้ง ⬆️", "Fire ลอยตัวขึ้นสู่รางบน")
      11 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.NONE, CubeRune.WATER, CubeRune.FIRE, "H นำขึ้นรางแนวนอน ⬆️ (ผ่าน CP 2)", "หมุนแกน Y สลับข้าง 🔄", "Checkpoint 2 สำเร็จ! จัดฐานน้ำเรียบร้อย")
      12 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.NONE, CubeRune.WATER, CubeRune.FIRE, "สลับตำแหน่งรางซ้าย 🔄", "หมุนแกน Y ซ้าย 1 ครั้ง ⬅️", "สลับบล็อกนำทาง")
      13 -> CubeSlotState(CubeRune.FIRE, CubeRune.HAND, CubeRune.NONE, CubeRune.WATER, CubeRune.NONE, "H เลื่อนลงรางซ้าย ⬇️", "บิดแกน X ลง 1 ครั้ง ⬇️", "Hand ลงรางซ้าย")
      14 -> CubeSlotState(CubeRune.NONE, CubeRune.FIRE, CubeRune.HAND, CubeRune.WATER, CubeRune.NONE, "F เลื่อนลงรางซ้าย ⬇️", "บิดแกน X ลง 1 ครั้ง ⬇️", "Fire ลงรางซ้าย")
      15 -> CubeSlotState(CubeRune.FLOWER, CubeRune.FIRE, CubeRune.HAND, CubeRune.WATER, CubeRune.NONE, "Fl เลื่อนขึ้นรางแนวนอน ⬆️", "บิดแกน X ขึ้น 1 ครั้ง ⬆️", "Flower ลอยขึ้นรางแนวนอน")
      16 -> CubeSlotState(CubeRune.HAND, CubeRune.FIRE, CubeRune.FLOWER, CubeRune.WATER, CubeRune.NONE, "H เลื่อนขึ้นรางแนวนอน ⬆️", "บิดแกน X ขึ้น 1 ครั้ง ⬆️", "Hand ลอยขึ้นรางบน")
      17 -> CubeSlotState(CubeRune.HAND, CubeRune.FIRE, CubeRune.FLOWER, CubeRune.NONE, CubeRune.WATER, "สลับตำแหน่งรางขวา 🔄 (ผ่าน CP 3)", "หมุนแกน Y ขวา 1 ครั้ง ➡️", "Checkpoint 3 สำเร็จ! ล็อกดอกไม้เสร็จ")
      18 -> CubeSlotState(CubeRune.HAND, CubeRune.FIRE, CubeRune.NONE, CubeRune.FLOWER, CubeRune.WATER, "Fl เลื่อนลงรางขวา ⬇️", "บิดแกน X ลง 1 ครั้ง ⬇️", "Flower ลงรางขวา")
      19 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.FLOWER, CubeRune.WATER, "สลับตำแหน่งรางซ้าย 🔄", "หมุนแกน Y ซ้าย 1 ครั้ง ⬅️", "สลับ Fire และ Hand")
      20 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.HAND, CubeRune.FLOWER, CubeRune.WATER, "F นำขึ้นรางแนวนอน ⬆️", "บิดแกน X ขึ้น 1 ครั้ง ⬆️", "Fire ขั้นตอนสุดท้าย")
      21 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.HAND, CubeRune.WATER, CubeRune.FLOWER, "สลับตำแหน่งรางขวา 🔄", "หมุนแกน Y ขวา 1 ครั้ง ➡️", "จัดตำแหน่งจบ")
      22 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.NONE, CubeRune.HAND, CubeRune.FLOWER, "H เลื่อนลงรางขวา ⬇️", "บิดแกน X ลง 1 ครั้ง ⬇️", "Hand ลงตำแหน่งสมบูรณ์")
      23 -> CubeSlotState(CubeRune.NONE, CubeRune.NONE, CubeRune.NONE, CubeRune.FIRE, CubeRune.HAND, "F เลื่อนลงรางขวา → แก้สำเร็จ 100%! 🎉", "แกน XYZ ล็อกเข้าที่! 🎉", "Checkpoint 4 สมบูรณ์ 100% ปลดล็อก PaP!")
      else -> CubeSlotState()
    }
  }
}

/**
 * Visual interactive step-by-step schematic for the Veytharion Cube Puzzle.
 */
@Composable
fun Cube3DVisualizer(
  currentStep: Int,
  modifier: Modifier = Modifier,
  onSelectStep: ((Int) -> Unit)? = null
) {
  val state = CubeStateCalculator.getStateForStep(currentStep)

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = DarkSurfaceVariant,
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF)),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Status Bar: Isolation Constraint Verification
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "สถานะปลอดภัย: ไฟ แยกจาก น้ำ/ดอกไม้",
            color = SuccessGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = PackAPunchCyan.copy(alpha = 0.2f)
        ) {
          Text(
            text = "STEP ${currentStep + 1}/24",
            color = PackAPunchCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Checkpoint Milestones Strip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        CUBE_CHECKPOINTS.forEach { cp ->
          val isPassed = currentStep >= cp.stepIndex
          val isCurrent = currentStep in (if (cp.number == 1) 0..cp.stepIndex else (CUBE_CHECKPOINTS[cp.number - 2].stepIndex + 1)..cp.stepIndex)
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = when {
              isPassed -> SuccessGreen.copy(alpha = 0.2f)
              isCurrent -> LightningGold.copy(alpha = 0.25f)
              else -> Color(0x15FFFFFF)
            },
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              when {
                isPassed -> SuccessGreen
                isCurrent -> LightningGold
                else -> Color(0x22FFFFFF)
              }
            ),
            modifier = Modifier
              .weight(1f)
              .clickable(enabled = onSelectStep != null) {
                onSelectStep?.invoke(cp.stepIndex)
              }
          ) {
            Column(
              modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "CP ${cp.number}",
                color = if (isPassed || isCurrent) TextPrimary else TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3D Axis Directional Cue Indicator Card
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0x33000000),
        border = androidx.compose.foundation.BorderStroke(1.dp, AetherPurple.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(AetherPurple.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ViewInAr,
              contentDescription = null,
              tint = AetherPurple,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "ทิศทาง 3D: ${state.axis3dHint}",
              color = LightningGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = state.rotationCues,
              color = TextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Schematic Visualization of Cube Rails
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0F0B18), RoundedCornerShape(12.dp))
          .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // TOP HORIZONTAL TRACK
        Text("รางแนวนอน (Horizontal Rail)", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .background(Color(0x33000000), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0x44B388FF), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          RuneSlotView(rune = state.horizontalSlot, label = "ช่องกลาง")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TWO VERTICAL RAILS (LEFT & RIGHT)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          // LEFT TRACK
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .background(Color(0x33000000), RoundedCornerShape(10.dp))
              .border(1.dp, Color(0x33B388FF), RoundedCornerShape(10.dp))
              .padding(8.dp)
          ) {
            Text("รางซ้าย (L)", color = AetherPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            RuneSlotView(rune = state.leftTrackTop, label = "บน")
            Spacer(modifier = Modifier.height(4.dp))
            RuneSlotView(rune = state.leftTrackBottom, label = "ล่าง")
          }

          // CENTER DIRECTIONAL ARROW
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 18.dp)
          ) {
            Text(
              text = if (state.activeArrowDescription.contains("สลับ")) "🔄" else "➡️",
              fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "เลื่อนตำแหน่ง",
              color = LightningGold,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // RIGHT TRACK
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .background(Color(0x33000000), RoundedCornerShape(10.dp))
              .border(1.dp, Color(0x33B388FF), RoundedCornerShape(10.dp))
              .padding(8.dp)
          ) {
            Text("รางขวา (R)", color = PackAPunchCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            RuneSlotView(rune = state.rightTrackTop, label = "บน")
            Spacer(modifier = Modifier.height(4.dp))
            RuneSlotView(rune = state.rightTrackBottom, label = "ล่าง")
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Current Step Movement Hint
      Text(
        text = "⚡ ท่าที่ต้องทำ: ${state.activeArrowDescription}",
        color = LightningGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun RuneSlotView(rune: CubeRune, label: String) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (rune != CubeRune.NONE) rune.color.copy(alpha = 0.2f) else Color(0x15FFFFFF),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (rune != CubeRune.NONE) rune.color else Color(0x22FFFFFF)
    ),
    modifier = Modifier.size(52.dp, 38.dp)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = rune.symbol, fontSize = 14.sp)
      if (rune != CubeRune.NONE) {
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = rune.name.take(1),
          color = rune.color,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}

/**
 * Standalone Interactive Aether Cube Calculator Dialog for overlaying on CameraScreen or any view.
 */
@Composable
fun AetherCubeCalculatorDialog(
  initialStep: Int = 0,
  onDismiss: () -> Unit
) {
  var currentStep by remember { mutableIntStateOf(initialStep.coerceIn(0, 23)) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = DarkSurfaceCard,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, AetherPurple),
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 24.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ViewInAr, contentDescription = null, tint = AetherPurple, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AETHER CUBE 3D SOLVER",
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Black
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).testTag("close_cube_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "ปิด", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visualizer with step jump callback
        Cube3DVisualizer(
          currentStep = currentStep,
          onSelectStep = { selected -> currentStep = selected }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { if (currentStep > 0) currentStep-- },
            enabled = currentStep > 0,
            modifier = Modifier.weight(1f).height(44.dp).testTag("cube_dialog_prev"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("ก่อนหน้า", fontSize = 12.sp)
          }

          IconButton(
            onClick = { currentStep = 0 },
            modifier = Modifier.size(44.dp)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "รีเซ็ต", tint = TextSecondary)
          }

          Button(
            onClick = { if (currentStep < 23) currentStep++ },
            enabled = currentStep < 23,
            modifier = Modifier.weight(1f).height(44.dp).testTag("cube_dialog_next"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AetherPurple, contentColor = DarkSurfaceCard)
          ) {
            Text("ถัดไป", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}
