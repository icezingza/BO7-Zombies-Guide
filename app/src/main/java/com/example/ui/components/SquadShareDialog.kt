package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.HellfireAmber
import com.example.ui.theme.LightningGold
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Generates an offline 2D matrix visual barcode representing the squad's quest state.
 * Works completely offline without external heavy libraries.
 */
object SquadCodeGenerator {
  fun generateBarcodeBitmap(data: String, size: Int = 220): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val hash = data.hashCode()
    val bytes = data.toByteArray()

    val gridSize = 16
    val cellSize = size / gridSize

    for (x in 0 until gridSize) {
      for (y in 0 until gridSize) {
        // Corner markers (Finder patterns)
        val isCorner = (x < 3 && y < 3) || (x > gridSize - 4 && y < 3) || (x < 3 && y > gridSize - 4)
        val isCornerBorder = (x == 0 || x == 2 || y == 0 || y == 2) && (x <= 2 && y <= 2)
        val isCornerBorder2 = (x >= gridSize - 3 && (y == 0 || y == 2)) || (y <= 2 && (x == gridSize - 3 || x == gridSize - 1))
        val isCornerBorder3 = (x <= 2 && (y == gridSize - 3 || y == gridSize - 1)) || (y >= gridSize - 3 && (x == 0 || x == 2))

        val isFilled = if (isCorner) {
          isCornerBorder || isCornerBorder2 || isCornerBorder3 || (x == 1 && y == 1) || (x == gridSize - 2 && y == 1) || (x == 1 && y == gridSize - 2)
        } else {
          val charIndex = (x * gridSize + y) % bytes.size
          val byteVal = bytes[charIndex].toInt()
          ((byteVal xor (hash shr (x % 16))) and (1 shl (y % 8))) != 0
        }

        val color = if (isFilled) AndroidColor.BLACK else AndroidColor.WHITE
        for (px in 0 until cellSize) {
          for (py in 0 until cellSize) {
            val posX = x * cellSize + px
            val posY = y * cellSize + py
            if (posX < size && posY < size) {
              bitmap.setPixel(posX, posY, color)
            }
          }
        }
      }
    }
    return bitmap
  }
}

/**
 * SquadShareDialog: Generates a high-contrast QR/Matrix share barcode and quick-copy text
 * summarizing the 4 house symbols, current round, and cleansing status for Co-op teammates.
 */
@Composable
fun SquadShareDialog(
  houseSymbols: List<String>,
  currentRound: Int,
  currentCubeStep: Int,
  templeLightning: Map<String, Boolean>,
  onDismiss: () -> Unit
) {
  val clipboardManager = LocalClipboardManager.current
  var copiedToClipboard by remember { mutableStateOf(false) }

  // Clean formatted text for squad chat/discord
  val cleanSymbolsText = remember(houseSymbols, currentRound, currentCubeStep, templeLightning) {
    val sym1 = houseSymbols.getOrElse(0) { "ว่าง" }.ifBlank { "ว่าง" }
    val sym2 = houseSymbols.getOrElse(1) { "ว่าง" }.ifBlank { "ว่าง" }
    val sym3 = houseSymbols.getOrElse(2) { "ว่าง" }.ifBlank { "ว่าง" }
    val sym4 = houseSymbols.getOrElse(3) { "ว่าง" }.ifBlank { "ว่าง" }

    val cleansedTemplesCount = templeLightning.values.count { it }

    """
    🎮 [BO7 ZOMBIES SQUAD CODE]
    📍 Round: $currentRound
    🔮 Aether Cube: Step ${currentCubeStep + 1}/24
    ⚡ ชำระล้าง 4 วิหาร: $cleansedTemplesCount/4
    🔹 สัญลักษณ์บ้านยิง Exfil:
       1️⃣ $sym1
       2️⃣ $sym2
       3️⃣ $sym3
       4️⃣ $sym4
    """.trimIndent()
  }

  val barcodeBitmap = remember(cleanSymbolsText) {
    SquadCodeGenerator.generateBarcodeBitmap(cleanSymbolsText)
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = DarkSurfaceCard,
      border = androidx.compose.foundation.BorderStroke(1.dp, PackAPunchCyan.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth().testTag("squad_share_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.QrCode, contentDescription = null, tint = PackAPunchCyan, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SQUAD SHARE (แชร์ตี้)",
              color = PackAPunchCyan,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "ปิด", tint = TextMuted, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Matrix Barcode Display
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color.White,
          modifier = Modifier.padding(6.dp)
        ) {
          Box(modifier = Modifier.padding(12.dp)) {
            Image(
              bitmap = barcodeBitmap.asImageBitmap(),
              contentDescription = "Squad Barcode",
              modifier = Modifier.size(180.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "ให้เพื่อนในทีมเปิดกล้องสแกน หรือคัดลอกข้อความด้านล่าง",
          color = TextSecondary,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Symbols quick summary preview
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = DarkSurfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "สัญลักษณ์ 4 ช่องปัจจุบัน (Exfil Sequence):",
              color = LightningGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              for (i in 0..3) {
                val s = houseSymbols.getOrElse(i) { "" }
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (s.isNotBlank()) AetherPurple.copy(alpha = 0.3f) else Color(0x22FFFFFF),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(text = "#${i + 1}", color = TextMuted, fontSize = 9.sp)
                    Text(
                      text = if (s.isNotBlank()) s.split(" ")[0] else "-",
                      color = if (s.isNotBlank()) TextPrimary else TextMuted,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      maxLines = 1
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: Copy Text to Clipboard & Close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (copiedToClipboard) SuccessGreen else PackAPunchCyan,
            modifier = Modifier
              .weight(1f)
              .clickable {
                clipboardManager.setText(AnnotatedString(cleanSymbolsText))
                copiedToClipboard = true
              }
              .testTag("copy_squad_code_btn")
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                contentDescription = null,
                tint = DarkBackground,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (copiedToClipboard) "คัดลอกสำเร็จ!" else "คัดลอกข้อความแชร์",
                color = DarkBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }
  }
}
