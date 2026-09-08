package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.HellfireAmber
import com.example.ui.theme.LightningGold
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

/**
 * PS5 Lens Viewfinder component:
 * Opens CameraX back-facing lens, displays a reticle alignment grid targeting TV/Monitor screen,
 * and allows 1-tap snapshot scanning for House Symbols or Cube state with simulated AI detection.
 */
@Composable
fun Ps5LensScanner(
  currentHouseSymbols: List<String>,
  onSymbolDetected: (slotIndex: Int, symbolName: String) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val coroutineScope = rememberCoroutineScope()

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasCameraPermission = isGranted
  }

  var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
  var isScanning by remember { mutableStateOf(false) }
  var scanTargetSlot by remember { mutableIntStateOf(0) }
  var detectionResult by remember { mutableStateOf<String?>(null) }
  var ttsMessage by remember { mutableStateOf<String?>(null) }

  // Check if camera permission is needed
  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(16.dp)
  ) {
    // Header Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = PackAPunchCyan.copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, PackAPunchCyan)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Tv,
              contentDescription = null,
              tint = PackAPunchCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "PS5 SCREEN LENS",
              color = PackAPunchCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      IconButton(
        onClick = onClose,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(DarkSurfaceVariant)
          .testTag("close_lens_button")
      ) {
        Icon(Icons.Default.Close, contentDescription = "ปิดกล้อง", tint = TextPrimary)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Instruction Banner
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color(0x221E1B2E),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = null,
          tint = LightningGold,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "เล็งกรอบเลนส์ไปที่หน้าจอ PS5/TV ให้สัญลักษณ์หินหรือกล่อง Cube อยู่ตรงกลาง แล้วกดถ่าย",
          color = TextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Camera Preview Viewport or Permission fallback
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .clip(RoundedCornerShape(20.dp))
        .background(Color.Black)
        .border(2.dp, if (isScanning) LightningGold else AetherPurple.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
      contentAlignment = Alignment.Center
    ) {
      if (hasCameraPermission) {
        AndroidView(
          factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
              layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
              )
              scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
              val cameraProvider = cameraProviderFuture.get()
              val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
              }

              val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

              val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

              try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                  lifecycleOwner,
                  cameraSelector,
                  preview,
                  capture
                )
                imageCapture = capture
              } catch (exc: Exception) {
                exc.printStackTrace()
              }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
          },
          modifier = Modifier.fillMaxSize()
        )

        // TV Reticle Target Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
          val canvasWidth = size.width
          val canvasHeight = size.height
          val boxWidth = canvasWidth * 0.72f
          val boxHeight = canvasHeight * 0.45f
          val left = (canvasWidth - boxWidth) / 2f
          val top = (canvasHeight - boxHeight) / 2f

          // Outer darkened mask
          // Center targeting frame
          drawRect(
            color = if (isScanning) Color(0x66FFD700) else Color(0x5500E5FF),
            topLeft = Offset(left, top),
            size = Size(boxWidth, boxHeight),
            style = Stroke(
              width = 4f,
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 15f), 0f)
            )
          )

          // Center crosshair
          val centerX = canvasWidth / 2f
          val centerY = canvasHeight / 2f
          val crosshairLen = 20f

          drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(centerX - crosshairLen, centerY),
            end = Offset(centerX + crosshairLen, centerY),
            strokeWidth = 3f
          )
          drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(centerX, centerY - crosshairLen),
            end = Offset(centerX, centerY + crosshairLen),
            strokeWidth = 3f
          )
        }

        // Scanning Animation or Status Overlay
        if (isScanning) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD120E24),
            border = androidx.compose.foundation.BorderStroke(1.dp, LightningGold),
            modifier = Modifier.align(Alignment.Center)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              CircularProgressIndicator(
                color = LightningGold,
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "กำลังอ่านสัญลักษณ์จากจอ PS5...",
                color = LightningGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      } else {
        // Permission Request Fallback View
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = DangerRed,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "จำเป็นต้องใช้สิทธิ์กล้องเพื่อส่องจอ PS5",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "แอปต้องการกล้องหลังเพื่ออ่านสัญลักษณ์หินและกล่อง Cube บนทีวีของคุณ",
            color = TextSecondary,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            colors = ButtonDefaults.buttonColors(containerColor = AetherPurple)
          ) {
            Text("อนุญาตให้ใช้กล้อง")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 4 Exfil Slots Selector (Choose which slot to fill with the next photo scan)
    Text(
      text = "เลือกช่องที่จะบันทึกจากการสแกน (Slot 1 - 4):",
      color = TextPrimary,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      for (i in 0..3) {
        val isTarget = scanTargetSlot == i
        val currentSym = currentHouseSymbols.getOrElse(i) { "" }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isTarget) AetherPurple.copy(alpha = 0.35f) else DarkSurfaceCard,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isTarget) AetherPurple else Color(0x33FFFFFF)
          ),
          modifier = Modifier
            .weight(1f)
            .clickable { scanTargetSlot = i }
            .testTag("lens_slot_choice_$i")
        ) {
          Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "เสา #${i + 1}",
              color = if (isTarget) PackAPunchCyan else TextMuted,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = if (currentSym.isNotEmpty()) currentSym.split(" ")[0] else "ว่าง",
              color = if (currentSym.isNotEmpty()) LightningGold else TextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Audio Callout Bar
    AnimatedVisibility(visible = ttsMessage != null) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0x3300E676),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 10.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = ttsMessage ?: "",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Capture Trigger Button (Large one-handed button)
    Button(
      onClick = {
        if (!isScanning) {
          isScanning = true
          detectionResult = null

          // Trigger simulated smart CV detection matching Rex Infernus symbol set
          coroutineScope.launch {
            delay(1200) // Simulate fast image analysis
            val candidateSymbols = listOf(
              "กะโหลก (Skull)", "พระจันทร์ (Moon)", "ดวงอาทิตย์ (Sun)",
              "งูพิษ (Serpent)", "เปลวไฟ (Flame)", "มีดสั้น (Dagger)",
              "มงกุฎ (Crown)", "ดวงตา (Eye)"
            )
            // Pick a symbol for demo detection or cycle intelligently
            val detected = candidateSymbols[scanTargetSlot % candidateSymbols.size]

            onSymbolDetected(scanTargetSlot, detected)
            detectionResult = "ตรวจพบ: $detected"
            ttsMessage = "ตรวจพบเสา #${scanTargetSlot + 1}: ${detected.split(" ")[0]}"

            isScanning = false

            // Auto-advance to next empty slot
            if (scanTargetSlot < 3) {
              scanTargetSlot++
            }
          }
        }
      },
      enabled = !isScanning,
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = HellfireAmber,
        contentColor = DarkBackground
      ),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("snap_ps5_screen_button")
    ) {
      Icon(
        imageVector = Icons.Default.CameraAlt,
        contentDescription = null,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = if (isScanning) "กำลังวิเคราะห์จอ..." else "ถ่ายเพื่ออ่านสัญลักษณ์ (Snap Symbol)",
        fontSize = 15.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}
