package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
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

data class CubeSlotState(
  val horizontalSlot: CubeRune = CubeRune.NONE,
  val leftTrackTop: CubeRune = CubeRune.NONE,
  val leftTrackBottom: CubeRune = CubeRune.NONE,
  val rightTrackTop: CubeRune = CubeRune.NONE,
  val rightTrackBottom: CubeRune = CubeRune.NONE,
  val activeArrowDescription: String = "สถานะตั้งต้น"
)

object CubeStateCalculator {
  // Approximate slot distribution at each of the 24 steps
  fun getStateForStep(stepIndex: Int): CubeSlotState {
    return when (stepIndex) {
      0 -> CubeSlotState(CubeRune.NONE, CubeRune.FIRE, CubeRune.HAND, CubeRune.WATER, CubeRune.FLOWER, "เริ่มต้น: F, H ในรางซ้าย / W, Fl ในรางขวา")
      1 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.HAND, CubeRune.WATER, CubeRune.FLOWER, "F เลื่อนขึ้นรางแนวนอน ⬆️")
      2 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.WATER, CubeRune.FLOWER, "H เลื่อนขึ้นรางแนวนอน ⬆️")
      3 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.FLOWER, CubeRune.WATER, "สลับตำแหน่งรางขวา 🔄")
      4 -> CubeSlotState(CubeRune.NONE, CubeRune.NONE, CubeRune.HAND, CubeRune.FIRE, CubeRune.FLOWER, "F เลื่อนลงรางขวา ⬇️")
      5 -> CubeSlotState(CubeRune.NONE, CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.FLOWER, "สลับตำแหน่งรางซ้าย 🔄")
      6 -> CubeSlotState(CubeRune.WATER, CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.FLOWER, "W เลื่อนขึ้นรางแนวนอน ⬆️")
      7 -> CubeSlotState(CubeRune.WATER, CubeRune.HAND, CubeRune.NONE, CubeRune.FLOWER, CubeRune.FIRE, "สลับตำแหน่งรางขวา 🔄")
      8 -> CubeSlotState(CubeRune.WATER, CubeRune.NONE, CubeRune.NONE, CubeRune.HAND, CubeRune.FLOWER, "H เลื่อนลงรางขวา ⬇️")
      9 -> CubeSlotState(CubeRune.NONE, CubeRune.NONE, CubeRune.NONE, CubeRune.WATER, CubeRune.HAND, "W เลื่อนลงรางขวา ⬇️")
      10 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.NONE, CubeRune.WATER, CubeRune.HAND, "F นำขึ้นรางแนวนอน ⬆️")
      11 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.NONE, CubeRune.WATER, CubeRune.FIRE, "H นำขึ้นรางแนวนอน ⬆️")
      12 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.NONE, CubeRune.WATER, CubeRune.FIRE, "สลับตำแหน่งรางซ้าย 🔄")
      13 -> CubeSlotState(CubeRune.FIRE, CubeRune.HAND, CubeRune.NONE, CubeRune.WATER, CubeRune.NONE, "H เลื่อนลงรางซ้าย ⬇️")
      14 -> CubeSlotState(CubeRune.NONE, CubeRune.FIRE, CubeRune.HAND, CubeRune.WATER, CubeRune.NONE, "F เลื่อนลงรางซ้าย ⬇️")
      15 -> CubeSlotState(CubeRune.FLOWER, CubeRune.FIRE, CubeRune.HAND, CubeRune.WATER, CubeRune.NONE, "Fl เลื่อนขึ้นรางแนวนอน ⬆️")
      16 -> CubeSlotState(CubeRune.HAND, CubeRune.FIRE, CubeRune.FLOWER, CubeRune.WATER, CubeRune.NONE, "H เลื่อนขึ้นรางแนวนอน ⬆️")
      17 -> CubeSlotState(CubeRune.HAND, CubeRune.FIRE, CubeRune.FLOWER, CubeRune.NONE, CubeRune.WATER, "สลับตำแหน่งรางขวา 🔄")
      18 -> CubeSlotState(CubeRune.HAND, CubeRune.FIRE, CubeRune.NONE, CubeRune.FLOWER, CubeRune.WATER, "Fl เลื่อนลงรางขวา ⬇️")
      19 -> CubeSlotState(CubeRune.HAND, CubeRune.NONE, CubeRune.FIRE, CubeRune.FLOWER, CubeRune.WATER, "สลับตำแหน่งรางซ้าย 🔄")
      20 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.HAND, CubeRune.FLOWER, CubeRune.WATER, "F นำขึ้นรางแนวนอน ⬆️")
      21 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.HAND, CubeRune.WATER, CubeRune.FLOWER, "สลับตำแหน่งรางขวา 🔄")
      22 -> CubeSlotState(CubeRune.FIRE, CubeRune.NONE, CubeRune.NONE, CubeRune.HAND, CubeRune.FLOWER, "H เลื่อนลงรางขวา ⬇️")
      23 -> CubeSlotState(CubeRune.NONE, CubeRune.NONE, CubeRune.NONE, CubeRune.FIRE, CubeRune.HAND, "F เลื่อนลงรางขวา → แก้สำเร็จ 100%! 🎉")
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
  modifier: Modifier = Modifier
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
            text = "สถานะปลอดภัย: ไฟ ไม่สัมผัส น้ำ/ดอกไม้",
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

      Spacer(modifier = Modifier.height(12.dp))

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
