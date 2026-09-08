package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.network.AiConfig
import com.example.network.AiConfigManager
import com.example.network.AudioCoPilot
import com.example.network.FrameDifferenceDetector
import com.example.network.GeminiGameplayAnalyzer
import com.example.network.GameplayAnalysisResult
import com.example.network.ThreatLevel
import com.example.network.VoiceAction
import com.example.network.VoiceCommandHandler
import androidx.compose.material.icons.filled.PictureInPicture
import com.example.ui.components.AetherCubeCalculatorDialog
import com.example.ui.components.AiSettingsDialog
import com.example.ui.components.FloatingHudWidget
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
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * CameraScreen: Captures video frames using CameraX ImageAnalysis and runs a live
 * processing loop passing frames to Gemini API for real-time Co-pilot gameplay analysis.
 */
@Composable
fun CameraScreen(
  houseSymbols: List<String> = listOf("", "", "", ""),
  currentRound: Int = 1,
  onApplySymbolDetected: (slotIndex: Int, symbolName: String) -> Unit = { _, _ -> },
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val coroutineScope = rememberCoroutineScope()

  // AI Configuration State (Persistent Preferences)
  val configManager = remember { AiConfigManager(context) }
  val aiConfig by configManager.configState.collectAsState()
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showCubeDialog by remember { mutableStateOf(false) }
  var showFloatingHud by remember { mutableStateOf(false) }

  // Audio Co-Pilot (Text-to-Speech)
  val audioCoPilot = remember { AudioCoPilot(context) }

  // Camera permissions
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

  // Audio Record permission for Voice Commands
  var hasAudioPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    )
  }

  val audioPermissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasAudioPermission = isGranted
  }

  // Camera Controls State
  var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
  var cameraControl by remember { mutableStateOf<Camera?>(null) }
  var isTorchOn by remember { mutableStateOf(false) }

  // Frame Buffer from CameraX
  var latestFrameBitmap by remember { mutableStateOf<Bitmap?>(null) }

  // Processing Loop State
  var isLiveAnalysisActive by remember { mutableStateOf(true) }
  var analysisIntervalMs by remember { mutableLongStateOf(3000L) } // 3 seconds loop
  var isAnalyzing by remember { mutableStateOf(false) }
  var analyzedFramesCount by remember { mutableIntStateOf(0) }

  // Smart Frame Difference Detection Filter State
  var lastAnalyzedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var skippedFramesCount by remember { mutableIntStateOf(0) }
  var currentMotionDiff by remember { mutableFloatStateOf(0.0f) }
  var isScreenStatic by remember { mutableStateOf(false) }

  // Analysis Results
  val analyzer = remember { GeminiGameplayAnalyzer() }
  var latestResult by remember { mutableStateOf<GameplayAnalysisResult?>(null) }
  val history = remember { mutableStateListOf<GameplayAnalysisResult>() }

  // Hands-Free Voice Command System
  var lastVoiceCommandName by remember { mutableStateOf<String?>(null) }
  val voiceCommandHandler = remember {
    VoiceCommandHandler(context) { action ->
      when (action) {
        is VoiceAction.TriggerScan -> {
          lastVoiceCommandName = "สแกนทันที"
          audioCoPilot.speakTacticalAdvice("กำลังสแกนจอภาพ", force = true)
          val currentBitmap = latestFrameBitmap
          if (currentBitmap != null && !isAnalyzing) {
            isAnalyzing = true
            coroutineScope.launch(Dispatchers.IO) {
              val result = analyzer.analyzeFrame(currentBitmap, config = aiConfig)
              latestResult = result
              analyzedFramesCount++
              if (history.size >= 8) history.removeLast()
              history.add(0, result)
              isAnalyzing = false

              if (aiConfig.isTtsEnabled && result.tacticalAdvice.isNotBlank()) {
                val isCrit = result.threatLevel == ThreatLevel.CRITICAL
                audioCoPilot.speakTacticalAdvice(
                  text = if (isCrit) "ภัยวิกฤต! ${result.tacticalAdvice}" else result.tacticalAdvice,
                  isCritical = isCrit
                )
              }
            }
          }
        }
        is VoiceAction.PauseLoop -> {
          lastVoiceCommandName = "หยุดลูปสด"
          isLiveAnalysisActive = false
          audioCoPilot.speakTacticalAdvice("หยุดลูปสดแล้ว", force = true)
        }
        is VoiceAction.ResumeLoop -> {
          lastVoiceCommandName = "เริ่มลูปสด"
          isLiveAnalysisActive = true
          audioCoPilot.speakTacticalAdvice("เริ่มลูปวิเคราะห์สด", force = true)
        }
        is VoiceAction.SaveSymbol -> {
          lastVoiceCommandName = "บันทึกสัญลักษณ์"
          val puzzle = latestResult?.detectedPuzzles?.firstOrNull()
          if (puzzle != null) {
            onApplySymbolDetected(0, puzzle)
            audioCoPilot.speakTacticalAdvice("บันทึกสัญลักษณ์ $puzzle เรียบร้อย", force = true)
          } else {
            audioCoPilot.speakTacticalAdvice("ไม่พบสัญลักษณ์ในจอนี้", force = true)
          }
        }
        is VoiceAction.RequestBossTips -> {
          lastVoiceCommandName = "ขอสูตรบอส"
          audioCoPilot.speakTacticalAdvice("สูตรบอส เล็งยิงคริสตัลที่หน้าอก และระวังระเบิด Void Burst รีบหลบหลังเสา", force = true)
        }
        is VoiceAction.NextStep -> {
          lastVoiceCommandName = "ขั้นตอนถัดไป"
          audioCoPilot.speakTacticalAdvice("รับทราบ ไปขั้นตอนถัดไป", force = true)
        }
        is VoiceAction.Unrecognized -> {
          lastVoiceCommandName = "คำสั่ง: ${action.text}"
        }
      }
    }
  }

  // Manage Voice Command listening lifecycle
  LaunchedEffect(aiConfig.isVoiceCommandEnabled, hasAudioPermission) {
    if (aiConfig.isVoiceCommandEnabled) {
      if (hasAudioPermission) {
        voiceCommandHandler.startListening()
      } else {
        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
      }
    } else {
      voiceCommandHandler.stopListening()
    }
  }

  // Pulsing animation for active live scan
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseAlpha"
  )

  // Request camera permission on launch if needed
  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  // Camera Executor for background image analysis and cleanup
  val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
  DisposableEffect(Unit) {
    onDispose {
      cameraExecutor.shutdown()
      audioCoPilot.shutdown()
      voiceCommandHandler.stopListening()
    }
  }

  // Continuous Video Frame Processing Loop with Smart Motion Filter & Audio Co-Pilot
  LaunchedEffect(isLiveAnalysisActive, analysisIntervalMs, hasCameraPermission, aiConfig) {
    if (!hasCameraPermission) return@LaunchedEffect

    while (isLiveAnalysisActive) {
      val frameToAnalyze = latestFrameBitmap
      if (frameToAnalyze != null && !isAnalyzing) {
        var shouldSkipDueToStaticScreen = false

        // Smart Local Motion / Frame Difference Check
        if (aiConfig.isSmartFilterEnabled && lastAnalyzedBitmap != null) {
          val diff = FrameDifferenceDetector.calculateDifferencePercent(lastAnalyzedBitmap, frameToAnalyze)
          currentMotionDiff = diff
          if (diff < aiConfig.motionThresholdPercent) {
            shouldSkipDueToStaticScreen = true
            isScreenStatic = true
            skippedFramesCount++
          } else {
            isScreenStatic = false
          }
        }

        if (!shouldSkipDueToStaticScreen) {
          isAnalyzing = true
          try {
            lastAnalyzedBitmap = frameToAnalyze.copy(frameToAnalyze.config ?: Bitmap.Config.ARGB_8888, false)
          } catch (_: Exception) {}

          coroutineScope.launch(Dispatchers.IO) {
            val result = analyzer.analyzeFrame(frameToAnalyze, config = aiConfig)
            latestResult = result
            analyzedFramesCount++
            if (history.size >= 8) history.removeLast()
            history.add(0, result)
            isAnalyzing = false

            // Audio Co-Pilot Tactical Alert
            if (aiConfig.isTtsEnabled && result.tacticalAdvice.isNotBlank()) {
              val isCritical = result.threatLevel == ThreatLevel.CRITICAL
              audioCoPilot.speakTacticalAdvice(
                text = if (isCritical) "ภัยวิกฤต! ${result.tacticalAdvice}" else result.tacticalAdvice,
                isCritical = isCritical
              )
            }
          }
        }
      }
      delay(analysisIntervalMs)
    }
  }

  // Theme styling based on OLED Stealth HUD Mode
  val screenBg = if (aiConfig.isOledStealthMode) Color.Black else DarkBackground
  val cardBg = if (aiConfig.isOledStealthMode) Color.Black else DarkSurfaceCard
  val variantBg = if (aiConfig.isOledStealthMode) Color(0xFF090909) else DarkSurfaceVariant

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(screenBg)
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    // Top Bar: Title + Live Status Pill + Quick Action Toolbar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isLiveAnalysisActive) SuccessGreen.copy(alpha = 0.2f) else variantBg,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isLiveAnalysisActive) SuccessGreen else TextMuted
            )
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (isLiveAnalysisActive) SuccessGreen else TextMuted)
                  .alpha(if (isLiveAnalysisActive) pulseAlpha else 1f)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = if (isLiveAnalysisActive) "LIVE VISION AI" else "PAUSED",
                color = if (isLiveAnalysisActive) SuccessGreen else TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = aiConfig.selectedModel.uppercase(),
            color = PackAPunchCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = if (aiConfig.isOledStealthMode) "STEALTH CO-PILOT (OLED)" else "REX INFERNUS CO-PILOT",
          color = TextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Black
        )
      }

      // Quick Actions Toolbar: Torch, Flip, TTS, OLED, Cube, Settings
      Row(verticalAlignment = Alignment.CenterVertically) {
        // OLED Stealth Mode Quick Toggle
        IconButton(
          onClick = {
            configManager.updateConfig(aiConfig.copy(isOledStealthMode = !aiConfig.isOledStealthMode))
          },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (aiConfig.isOledStealthMode) PackAPunchCyan.copy(alpha = 0.2f) else variantBg)
            .testTag("toggle_oled_stealth_button")
        ) {
          Icon(
            imageVector = if (aiConfig.isOledStealthMode) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = "OLED Stealth Mode",
            tint = if (aiConfig.isOledStealthMode) PackAPunchCyan else TextMuted,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Audio Co-Pilot Voice Quick Toggle
        IconButton(
          onClick = {
            val newTts = !aiConfig.isTtsEnabled
            configManager.updateConfig(aiConfig.copy(isTtsEnabled = newTts))
            if (newTts) {
              audioCoPilot.speakTacticalAdvice("ระบบเสียงนำทางเปิดใช้งาน", force = true)
            }
          },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (aiConfig.isTtsEnabled) LightningGold.copy(alpha = 0.2f) else variantBg)
            .testTag("toggle_tts_button")
        ) {
          Icon(
            imageVector = if (aiConfig.isTtsEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.VolumeOff,
            contentDescription = "เสียง Co-Pilot",
            tint = if (aiConfig.isTtsEnabled) LightningGold else TextMuted,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Aether Cube 3D Visualizer Quick Overlay
        IconButton(
          onClick = { showCubeDialog = true },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(AetherPurple.copy(alpha = 0.25f))
            .testTag("open_cube_dialog_button")
        ) {
          Icon(
            imageVector = Icons.Default.ViewInAr,
            contentDescription = "3D Cube Solver",
            tint = AetherPurple,
            modifier = Modifier.size(17.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Mini Overlay / Floating HUD Toggle Button
        IconButton(
          onClick = { showFloatingHud = !showFloatingHud },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (showFloatingHud) PackAPunchCyan.copy(alpha = 0.25f) else variantBg)
            .testTag("toggle_floating_hud_button")
        ) {
          Icon(
            imageVector = Icons.Default.PictureInPicture,
            contentDescription = "Mini Floating HUD",
            tint = if (showFloatingHud) PackAPunchCyan else TextMuted,
            modifier = Modifier.size(17.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Torch toggle
        IconButton(
          onClick = {
            val newTorch = !isTorchOn
            isTorchOn = newTorch
            cameraControl?.cameraControl?.enableTorch(newTorch)
          },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(variantBg)
            .testTag("toggle_torch_button")
        ) {
          Icon(
            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
            contentDescription = "ไฟฉาย",
            tint = if (isTorchOn) LightningGold else TextMuted,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Flip camera
        IconButton(
          onClick = {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
              CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
              CameraSelector.DEFAULT_BACK_CAMERA
            }
          },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(variantBg)
            .testTag("switch_camera_button")
        ) {
          Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "สลับกล้อง",
            tint = TextPrimary,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // AI Engine & API Settings Dialog
        IconButton(
          onClick = { showSettingsDialog = true },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(variantBg)
            .testTag("open_ai_settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "ตั้งค่า AI",
            tint = TextPrimary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // CameraX Video Viewport with HUD Reticle
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(230.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(Color.Black)
        .border(
          width = 2.dp,
          color = when {
            isAnalyzing -> LightningGold
            latestResult?.threatLevel == ThreatLevel.CRITICAL -> DangerRed
            isLiveAnalysisActive -> PackAPunchCyan.copy(alpha = 0.7f)
            else -> Color(0x33B388FF)
          },
          shape = RoundedCornerShape(18.dp)
        ),
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

              // ImageAnalysis for video frames
              val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

              imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                try {
                  val bitmap = imageProxy.toBitmap()
                  latestFrameBitmap = bitmap
                } catch (e: Exception) {
                  e.printStackTrace()
                } finally {
                  imageProxy.close()
                }
              }

              try {
                cameraProvider.unbindAll()
                val boundCamera = cameraProvider.bindToLifecycle(
                  lifecycleOwner,
                  cameraSelector,
                  preview,
                  imageAnalysis
                )
                cameraControl = boundCamera
              } catch (exc: Exception) {
                exc.printStackTrace()
              }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
          },
          modifier = Modifier.fillMaxSize()
        )

        // TV / Monitor Targeting Reticle Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
          val canvasWidth = size.width
          val canvasHeight = size.height
          val boxWidth = canvasWidth * 0.78f
          val boxHeight = canvasHeight * 0.58f
          val left = (canvasWidth - boxWidth) / 2f
          val top = (canvasHeight - boxHeight) / 2f

          // Center targeting frame
          drawRect(
            color = if (isAnalyzing) LightningGold else PackAPunchCyan.copy(alpha = 0.8f),
            topLeft = Offset(left, top),
            size = Size(boxWidth, boxHeight),
            style = Stroke(
              width = 3f,
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 15f), 0f)
            )
          )

          // Center crosshair
          val centerX = canvasWidth / 2f
          val centerY = canvasHeight / 2f
          val crosshairLen = 22f

          drawLine(
            color = Color.White.copy(alpha = 0.85f),
            start = Offset(centerX - crosshairLen, centerY),
            end = Offset(centerX + crosshairLen, centerY),
            strokeWidth = 2.5f
          )
          drawLine(
            color = Color.White.copy(alpha = 0.85f),
            start = Offset(centerX, centerY - crosshairLen),
            end = Offset(centerX, centerY + crosshairLen),
            strokeWidth = 2.5f
          )
        }

        // Live Analyzing Banner Indicator
        if (isAnalyzing) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xDD120E24),
            border = androidx.compose.foundation.BorderStroke(1.dp, LightningGold),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              CircularProgressIndicator(
                color = LightningGold,
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Gemini กำลังวิเคราะห์จอ...",
                color = LightningGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Latency and Frame Counter Pill
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xAA000000),
          modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
        ) {
          Text(
            text = "${analyzedFramesCount} frames | ${latestResult?.latencyMs ?: 0} ms",
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }

        // Mini Floating HUD Overlay (PIP draggable widget)
        if (showFloatingHud) {
          FloatingHudWidget(
            latestResult = latestResult,
            houseSymbols = houseSymbols,
            currentRound = currentRound,
            isLiveActive = isLiveAnalysisActive,
            onToggleLive = { isLiveAnalysisActive = !isLiveAnalysisActive },
            onDismiss = { showFloatingHud = false },
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(top = 10.dp, end = 10.dp)
          )
        }
      } else {
        // Fallback when camera permission is not granted
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(36.dp))
          Spacer(modifier = Modifier.height(8.dp))
          Text("ต้องการสิทธิ์กล้องเพื่ออ่านภาพสด", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            colors = ButtonDefaults.buttonColors(containerColor = AetherPurple)
          ) {
            Text("อนุญาตกล้อง", fontSize = 12.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Controls Row: Pause/Resume Loop + Loop Interval Chips + 1-Tap Manual Snap
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Loop Play / Pause Toggle Button
      Button(
        onClick = { isLiveAnalysisActive = !isLiveAnalysisActive },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isLiveAnalysisActive) DangerRed.copy(alpha = 0.25f) else SuccessGreen,
          contentColor = if (isLiveAnalysisActive) DangerRed else DarkBackground
        ),
        border = if (isLiveAnalysisActive) androidx.compose.foundation.BorderStroke(1.dp, DangerRed) else null,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.testTag("toggle_live_loop_button")
      ) {
        Icon(
          imageVector = if (isLiveAnalysisActive) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (isLiveAnalysisActive) "หยุดลูป" else "เริ่มลูปสด",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Loop Interval Selectors (2s / 3s / 5s)
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(2000L to "2s", 3000L to "3s", 5000L to "5s").forEach { (interval, label) ->
          val isSelected = analysisIntervalMs == interval
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) PackAPunchCyan.copy(alpha = 0.25f) else DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) PackAPunchCyan else Color(0x22FFFFFF)
            ),
            modifier = Modifier
              .clickable { analysisIntervalMs = interval }
              .testTag("interval_chip_$label")
          ) {
            Text(
              text = label,
              color = if (isSelected) PackAPunchCyan else TextSecondary,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Instant Manual Snap Button
      Button(
        onClick = {
          val currentBitmap = latestFrameBitmap
          if (currentBitmap != null && !isAnalyzing) {
            isAnalyzing = true
            coroutineScope.launch(Dispatchers.IO) {
              val result = analyzer.analyzeFrame(currentBitmap, config = aiConfig)
              latestResult = result
              analyzedFramesCount++
              if (history.size >= 8) history.removeLast()
              history.add(0, result)
              isAnalyzing = false

              if (aiConfig.isTtsEnabled && result.tacticalAdvice.isNotBlank()) {
                val isCrit = result.threatLevel == ThreatLevel.CRITICAL
                audioCoPilot.speakTacticalAdvice(
                  text = if (isCrit) "ภัยวิกฤต! ${result.tacticalAdvice}" else result.tacticalAdvice,
                  isCritical = isCrit
                )
              }
            }
          }
        },
        enabled = !isAnalyzing && latestFrameBitmap != null,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = LightningGold,
          contentColor = DarkBackground
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("manual_snap_button")
      ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("สแกนทันที", fontSize = 11.sp, fontWeight = FontWeight.Black)
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Smart Motion Filter & Quota Saver Banner
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = if (isScreenStatic) SuccessGreen.copy(alpha = 0.15f) else variantBg,
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        if (aiConfig.isSmartFilterEnabled) SuccessGreen.copy(alpha = 0.5f) else Color(0x22FFFFFF)
      ),
      modifier = Modifier.fillMaxWidth().testTag("smart_filter_banner")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = if (aiConfig.isSmartFilterEnabled) SuccessGreen else TextMuted,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (aiConfig.isSmartFilterEnabled) "SMART FILTER: ข้าม $skippedFramesCount เฟรม" else "SMART FILTER: ปิดใช้งาน",
                color = if (aiConfig.isSmartFilterEnabled) SuccessGreen else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              if (aiConfig.isSmartFilterEnabled) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Diff: ${"%.1f".format(currentMotionDiff)}% (เกณฑ์ ${aiConfig.motionThresholdPercent.toInt()}%)",
                  color = if (isScreenStatic) SuccessGreen else TextSecondary,
                  fontSize = 10.sp
                )
              }
            }
            Text(
              text = if (isScreenStatic) "⚡ จอนิ่ง หยุดยิง API ประหยัดโควตาและลดความร้อน" else "กำลังตรวจจับการเคลื่อนไหวของจอเกม PS5",
              color = TextMuted,
              fontSize = 9.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (aiConfig.isSmartFilterEnabled) SuccessGreen.copy(alpha = 0.25f) else Color(0x22FFFFFF),
          modifier = Modifier.clickable {
            configManager.updateConfig(aiConfig.copy(isSmartFilterEnabled = !aiConfig.isSmartFilterEnabled))
          }
        ) {
          Text(
            text = if (aiConfig.isSmartFilterEnabled) "เปิดอยู่" else "ปิดอยู่",
            color = if (aiConfig.isSmartFilterEnabled) SuccessGreen else TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }

    // Voice Commands Hands-Free Indicator (if enabled)
    if (aiConfig.isVoiceCommandEnabled) {
      Spacer(modifier = Modifier.height(4.dp))
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = LightningGold.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, LightningGold.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth().testTag("voice_command_hud")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = LightningGold, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "สั่งงานด้วยเสียง (Hands-Free): เปิดใช้งาน",
                color = LightningGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "พูด: \"สแกน\", \"หยุด\", \"ต่อ\", \"จด\", \"สูตรบอส\", \"ต่อไป\"",
                color = TextSecondary,
                fontSize = 9.sp
              )
            }
          }

          if (lastVoiceCommandName != null) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = LightningGold.copy(alpha = 0.25f)
            ) {
              Text(
                text = "คำสั่ง: $lastVoiceCommandName",
                color = LightningGold,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Real-Time Tactical AI Feedback Feed
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.weight(1f)
    ) {
      // Latest Analysis Hero Card
      item {
        val result = latestResult
        if (result != null) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              when (result.threatLevel) {
                ThreatLevel.CRITICAL -> DangerRed
                ThreatLevel.CAUTION -> WarningAmber
                ThreatLevel.LOW -> SuccessGreen
              }
            ),
            modifier = Modifier.fillMaxWidth().testTag("latest_intel_card")
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              // Threat Level & Location Header
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = when (result.threatLevel) {
                    ThreatLevel.CRITICAL -> DangerRed.copy(alpha = 0.2f)
                    ThreatLevel.CAUTION -> WarningAmber.copy(alpha = 0.2f)
                    ThreatLevel.LOW -> SuccessGreen.copy(alpha = 0.2f)
                  }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = when (result.threatLevel) {
                        ThreatLevel.CRITICAL -> Icons.Default.Warning
                        ThreatLevel.CAUTION -> Icons.Default.Warning
                        ThreatLevel.LOW -> Icons.Default.Shield
                      },
                      contentDescription = null,
                      tint = when (result.threatLevel) {
                        ThreatLevel.CRITICAL -> DangerRed
                        ThreatLevel.CAUTION -> WarningAmber
                        ThreatLevel.LOW -> SuccessGreen
                      },
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "THREAT: ${result.threatLevel.name}",
                      color = when (result.threatLevel) {
                        ThreatLevel.CRITICAL -> DangerRed
                        ThreatLevel.CAUTION -> WarningAmber
                        ThreatLevel.LOW -> SuccessGreen
                      },
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Black
                    )
                  }
                }

                Text(
                  text = "📍 ${result.detectedLocation}",
                  color = PackAPunchCyan,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Summary
              Text(
                text = result.summary,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(6.dp))

              // Tactical Advice
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = HellfireAmber.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, HellfireAmber.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = HellfireAmber,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "คำแนะนำยุทธวิธีทันที:",
                      color = HellfireAmber,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = result.tacticalAdvice,
                      color = TextPrimary,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              }

              // Puzzles / Symbols Detected
              if (result.detectedPuzzles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "🔍 วัตถุ/สัญลักษณ์ที่ตรวจพบ:",
                  color = AetherPurple,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                result.detectedPuzzles.forEach { puzzle ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("• $puzzle", color = TextSecondary, fontSize = 12.sp)

                    // Quick apply button
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = PackAPunchCyan.copy(alpha = 0.18f),
                      modifier = Modifier
                        .clickable {
                          onApplySymbolDetected(0, puzzle)
                        }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PackAPunchCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("นำไปใช้", color = PackAPunchCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        } else {
          // Empty State Prompting User
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33B388FF)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = null,
                tint = PackAPunchCyan,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "กำลังรออ่านเฟรมภาพจากจอเกมสด...",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "เล็งกรอบกล้องไปที่หน้าจอทีวี/มอนิเตอร์ PS5 ของคุณ ระบบลูปจะส่งภาพให้ Gemini วิเคราะห์สถานะและปริศนาอัตโนมัติ",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      // History Feed Title
      if (history.size > 1) {
        item {
          Text(
            text = "ประวัติการวิเคราะห์ล่าสุด (${history.size} รอบ):",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        items(history.size - 1) { index ->
          val itemResult = history[index + 1]
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = variantBg,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(
                    when (itemResult.threatLevel) {
                      ThreatLevel.CRITICAL -> DangerRed
                      ThreatLevel.CAUTION -> WarningAmber
                      ThreatLevel.LOW -> SuccessGreen
                    }
                  )
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = itemResult.summary,
                  color = TextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = itemResult.tacticalAdvice,
                  color = TextMuted,
                  fontSize = 10.sp
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${itemResult.latencyMs}ms",
                color = TextMuted,
                fontSize = 9.sp
              )
            }
          }
        }
      }
    }
  }

  // AI Configuration Settings Dialog
  if (showSettingsDialog) {
    AiSettingsDialog(
      currentConfig = aiConfig,
      onSaveConfig = { newConfig ->
        configManager.updateConfig(newConfig)
        showSettingsDialog = false
      },
      onDismiss = { showSettingsDialog = false }
    )
  }

  // Interactive 3D Aether Cube Solver Overlay Dialog
  if (showCubeDialog) {
    AetherCubeCalculatorDialog(
      initialStep = 0,
      onDismiss = { showCubeDialog = false }
    )
  }
}
