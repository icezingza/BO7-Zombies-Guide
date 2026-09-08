package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.HellfireAmber
import com.example.ui.theme.HellfireOrange
import com.example.ui.theme.LightningGold
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HeroBanner(
  completedStepsCount: Int,
  totalStepsCount: Int,
  lightningCount: Int,
  onResetClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  val progress = if (totalStepsCount > 0) completedStepsCount.toFloat() / totalStepsCount else 0f
  val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
      .background(DarkBackground)
  ) {
    // Background Hero Artwork
    Image(
      painter = painterResource(id = R.drawable.img_rex_banner),
      contentDescription = "Rex Infernus Banner Artwork",
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp),
      contentScale = ContentScale.Crop
    )

    // Dark Gradient Overlay
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0x770C0A14),
              Color(0xCC0C0A14),
              Color(0xFF0C0A14)
            )
          )
        )
    )

    // Foreground Content
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = HellfireOrange.copy(alpha = 0.2f),
              modifier = Modifier.border(1.dp, HellfireOrange.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            ) {
              Text(
                text = "COD BLACK OPS 7",
                color = HellfireAmber,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = PackAPunchCyan.copy(alpha = 0.15f)
            ) {
              Text(
                text = "ZOMBIES",
                color = PackAPunchCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "REX INFERNUS",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
          )

          Text(
            text = "Live Main Quest & Survival Guide",
            color = AetherPurple,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }

        IconButton(
          onClick = onResetClicked,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33FFFFFF))
            .testTag("reset_progress_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "รีเซ็ตความคืบหน้า",
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Stats Badges Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Step progress pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0x33231F3A),
          modifier = Modifier
            .weight(1f)
            .border(1.dp, Color(0x33B388FF), RoundedCornerShape(12.dp))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SuccessGreen,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "ภารกิจหลัก",
                color = TextSecondary,
                fontSize = 10.sp
              )
              Text(
                text = "$completedStepsCount / $totalStepsCount ขั้น (${(progress * 100).toInt()}%)",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // 4 Temples Lightning pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0x33231F3A),
          modifier = Modifier
            .weight(1f)
            .border(1.dp, LightningGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = null,
              tint = LightningGold,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "สายฟ้า 4 วิหาร",
                color = TextSecondary,
                fontSize = 10.sp
              )
              Text(
                text = "$lightningCount / 4 สำเร็จ",
                color = LightningGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress Bar
      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = AetherPurple,
        trackColor = Color(0x33FFFFFF)
      )
    }
  }
}
