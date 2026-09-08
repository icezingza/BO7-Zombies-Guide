package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.GameplayAnalysisResult
import com.example.network.ThreatLevel
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.HellfireAmber
import com.example.ui.theme.LightningGold
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import kotlin.math.roundToInt

/**
 * FloatingHudWidget: A draggable, collapsible Picture-in-Picture style HUD overlay
 * that stays on screen showing the latest Gemini AI tactical advice and current Exfil symbols.
 * Perfect for PS5 players glancing at their phone placed next to the TV.
 */
@Composable
fun FloatingHudWidget(
  latestResult: GameplayAnalysisResult?,
  houseSymbols: List<String>,
  currentRound: Int,
  isLiveActive: Boolean,
  onToggleLive: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(true) }
  var offsetX by remember { mutableFloatStateOf(0f) }
  var offsetY by remember { mutableFloatStateOf(0f) }

  Box(
    modifier = modifier
      .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          offsetX += dragAmount.x
          offsetY += dragAmount.y
        }
      }
      .testTag("floating_hud_widget")
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = DarkSurfaceCard.copy(alpha = 0.95f),
      border = androidx.compose.foundation.BorderStroke(
        1.5.dp,
        when (latestResult?.threatLevel) {
          ThreatLevel.CRITICAL -> DangerRed
          ThreatLevel.CAUTION -> WarningAmber
          else -> PackAPunchCyan
        }
      ),
      shadowElevation = 8.dp,
      modifier = Modifier.width(if (isExpanded) 280.dp else 120.dp)
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        // Drag Handle & Title Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isExpanded = !isExpanded }
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isLiveActive) SuccessGreen else DangerRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isExpanded) "CO-PILOT HUD" else "R$currentRound HUD",
              color = PackAPunchCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { isExpanded = !isExpanded },
              modifier = Modifier.size(22.dp)
            ) {
              Icon(
                imageVector = Icons.Default.OpenInFull,
                contentDescription = "ย่อ/ขยาย",
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
              )
            }
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(22.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "ปิด",
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }

        if (isExpanded) {
          Spacer(modifier = Modifier.height(6.dp))

          // Current Threat & Location
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "📍 ${latestResult?.detectedLocation ?: "กำลังสแกน..."}",
              color = TextSecondary,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              modifier = Modifier.weight(1f)
            )
            Text(
              text = "R$currentRound",
              color = HellfireAmber,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Tactical Advice Banner
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = when (latestResult?.threatLevel) {
              ThreatLevel.CRITICAL -> DangerRed.copy(alpha = 0.2f)
              ThreatLevel.CAUTION -> WarningAmber.copy(alpha = 0.2f)
              else -> DarkSurface
            },
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = latestResult?.tacticalAdvice?.ifBlank { "จัดตำแหน่งมือถือส่องจอทีวี PS5 เพื่อรับคำแนะนำสด" }
                ?: "จัดตำแหน่งมือถือส่องจอทีวี PS5 เพื่อรับคำแนะนำสด",
              color = when (latestResult?.threatLevel) {
                ThreatLevel.CRITICAL -> DangerRed
                ThreatLevel.CAUTION -> WarningAmber
                else -> TextPrimary
              },
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(6.dp),
              lineHeight = 14.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          // 4 Exfil Symbols Mini Preview
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            for (i in 0..3) {
              val s = houseSymbols.getOrElse(i) { "" }
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (s.isNotBlank()) LightningGold.copy(alpha = 0.2f) else Color(0x18FFFFFF),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = if (s.isNotBlank()) s.split(" ")[0] else "-",
                  color = if (s.isNotBlank()) LightningGold else TextMuted,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }
        }
      }
    }
  }
}
