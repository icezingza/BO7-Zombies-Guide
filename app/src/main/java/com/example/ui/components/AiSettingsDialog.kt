package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.network.AiConfig
import com.example.network.AiConfigManager
import com.example.ui.theme.AetherPurple
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.HellfireOrange
import com.example.ui.theme.LightningGold
import com.example.ui.theme.PackAPunchCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun AiSettingsDialog(
  currentConfig: AiConfig,
  onSaveConfig: (AiConfig) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedModel by remember { mutableStateOf(currentConfig.selectedModel) }
  var customApiKey by remember { mutableStateOf(currentConfig.customApiKey) }
  var fallbackApiKey by remember { mutableStateOf(currentConfig.fallbackApiKey) }
  var customEndpointUrl by remember { mutableStateOf(currentConfig.customEndpointUrl) }
  var isSmartFilterEnabled by remember { mutableStateOf(currentConfig.isSmartFilterEnabled) }
  var isTtsEnabled by remember { mutableStateOf(currentConfig.isTtsEnabled) }
  var isVoiceCommandEnabled by remember { mutableStateOf(currentConfig.isVoiceCommandEnabled) }
  var isOledStealthMode by remember { mutableStateOf(currentConfig.isOledStealthMode) }

  // Test Connection status
  var isTesting by remember { mutableStateOf(false) }
  var testResult by remember { mutableStateOf<String?>(null) }
  var testSuccess by remember { mutableStateOf<Boolean?>(null) }
  val coroutineScope = rememberCoroutineScope()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = DarkSurfaceCard,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, PackAPunchCyan.copy(alpha = 0.6f)),
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 20.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(PackAPunchCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = PackAPunchCyan,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "AI ENGINE & API CONFIG",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "สลับโมเดล & สำรอง API Token",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("close_ai_settings_dialog")
          ) {
            Icon(Icons.Default.Close, contentDescription = "ปิด", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.weight(1f, fill = false)
        ) {
          // Model Selection Section
          item {
            Text(
              text = "1. เลือกโมเดล AI ที่ใช้งาน (AI Model)",
              color = LightningGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              AiConfigManager.AVAILABLE_MODELS.forEach { (modelId, desc) ->
                val isSelected = selectedModel == modelId
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) PackAPunchCyan.copy(alpha = 0.15f) else DarkSurfaceVariant,
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) PackAPunchCyan else Color(0x22FFFFFF)
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedModel = modelId }
                    .testTag("model_option_$modelId")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) PackAPunchCyan else Color.Transparent)
                        .border(1.5.dp, if (isSelected) PackAPunchCyan else TextMuted, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = modelId,
                        color = if (isSelected) PackAPunchCyan else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                      )
                      Text(
                        text = desc,
                        color = TextSecondary,
                        fontSize = 10.sp
                      )
                    }
                  }
                }
              }
            }
          }

          // API Key Configuration
          item {
            Text(
              text = "2. ตั้งค่า API Key & Fallback สำรอง",
              color = LightningGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Primary API Key
            OutlinedTextField(
              value = customApiKey,
              onValueChange = { customApiKey = it },
              label = { Text("Primary Gemini API Key (ว่างไว้ = ใช้ค่าเดิมในระบบ)", fontSize = 11.sp) },
              placeholder = { Text("AIzaSy...", fontSize = 11.sp, color = TextMuted) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PackAPunchCyan,
                unfocusedBorderColor = Color(0x44B388FF),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              ),
              modifier = Modifier.fillMaxWidth().testTag("primary_api_key_input")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Fallback API Key (Auto switch when token runs out / 429)
            OutlinedTextField(
              value = fallbackApiKey,
              onValueChange = { fallbackApiKey = it },
              label = { Text("Backup API Key (สลับอัตโนมัติเมื่อ Token หมด)", fontSize = 11.sp) },
              placeholder = { Text("คีย์สำรอง (สำหรับสลับทันทีที่ติด Quota 429)", fontSize = 11.sp, color = TextMuted) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HellfireOrange,
                unfocusedBorderColor = Color(0x44B388FF),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              ),
              modifier = Modifier.fillMaxWidth().testTag("fallback_api_key_input")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Custom Endpoint URL (Sim Studio / Proxy / Gateway)
            OutlinedTextField(
              value = customEndpointUrl,
              onValueChange = { customEndpointUrl = it },
              label = { Text("Custom Gateway URL (Optional / Proxy / Sim Studio)", fontSize = 11.sp) },
              placeholder = { Text("https://generativelanguage.googleapis.com/v1beta/models", fontSize = 11.sp, color = TextMuted) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AetherPurple,
                unfocusedBorderColor = Color(0x44B388FF),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              ),
              modifier = Modifier.fillMaxWidth().testTag("custom_endpoint_input")
            )
          }

          // Tactical Co-pilot Toggles
          item {
            Text(
              text = "3. ปรับแต่งระบบ Co-pilot อัจฉริยะ",
              color = LightningGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Smart Frame Difference Filter
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Smart Frame Filter", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("ตรวจจับการเปลี่ยนมุมจอ ข้ามการเรียก API เมื่อจอนิ่ง ประหยัดโควตา", color = TextSecondary, fontSize = 10.sp)
              }
              Switch(
                checked = isSmartFilterEnabled,
                onCheckedChange = { isSmartFilterEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen, checkedTrackColor = SuccessGreen.copy(alpha = 0.3f))
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Text-to-Speech (TTS)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("เสียงพูดเตือนสด (Audio Co-Pilot)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("ส่งเสียงเตือนคำแนะนำภาษาไทยทันที ไม่ต้องละสายตาจากทีวี PS5", color = TextSecondary, fontSize = 10.sp)
              }
              Switch(
                checked = isTtsEnabled,
                onCheckedChange = { isTtsEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = PackAPunchCyan, checkedTrackColor = PackAPunchCyan.copy(alpha = 0.3f))
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Voice Command Hands-free
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("สั่งงานด้วยเสียง (Voice Commands)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("พูดคำว่า 'สแกน' หรือ 'ถัดไป' ขณะจับจอย DualSense", color = TextSecondary, fontSize = 10.sp)
              }
              Switch(
                checked = isVoiceCommandEnabled,
                onCheckedChange = { isVoiceCommandEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = LightningGold, checkedTrackColor = LightningGold.copy(alpha = 0.3f))
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // OLED Stealth Mode
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("OLED Black Stealth HUD", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("สีดำสนิท 100% ประหยัดแบตเตอรี่ในห้องมืดสำหรับการลงเรดยาว", color = TextSecondary, fontSize = 10.sp)
              }
              Switch(
                checked = isOledStealthMode,
                onCheckedChange = { isOledStealthMode = it },
                colors = SwitchDefaults.colors(checkedThumbColor = AetherPurple, checkedTrackColor = AetherPurple.copy(alpha = 0.3f))
              )
            }
          }

          // Test Connection Row
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Button(
                onClick = {
                  isTesting = true
                  testResult = null
                  testSuccess = null
                  coroutineScope.launch(Dispatchers.IO) {
                    try {
                      val testKey = if (customApiKey.isNotBlank()) customApiKey else currentConfig.activeApiKey
                      val baseUrl = if (customEndpointUrl.isNotBlank()) customEndpointUrl.removeSuffix("/") else "https://generativelanguage.googleapis.com/v1beta/models"
                      val testUrl = "$baseUrl/$selectedModel:generateContent?key=$testKey"
                      
                      val payload = JSONObject().apply {
                        put("contents", JSONArray().put(JSONObject().apply {
                          put("parts", JSONArray().put(JSONObject().put("text", "Ping test. Reply with 'OK'")))
                        }))
                      }

                      val client = OkHttpClient()
                      val req = Request.Builder()
                        .url(testUrl)
                        .post(payload.toString().toRequestBody("application/json".toMediaType()))
                        .build()

                      val resp = client.newCall(req).execute()
                      val success = resp.isSuccessful
                      val bodyStr = resp.body?.string() ?: ""
                      resp.close()

                      withContext(Dispatchers.Main) {
                        isTesting = false
                        testSuccess = success
                        testResult = if (success) "เชื่อมต่อโมเดล $selectedModel สำเร็จ!" else "เชื่อมต่อล้มเหลว (${resp.code}): ${bodyStr.take(100)}"
                      }
                    } catch (e: Exception) {
                      withContext(Dispatchers.Main) {
                        isTesting = false
                        testSuccess = false
                        testResult = "Error: ${e.localizedMessage}"
                      }
                    }
                  }
                },
                enabled = !isTesting,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("test_ai_connection_button")
              ) {
                if (isTesting) {
                  CircularProgressIndicator(color = LightningGold, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("กำลังทดสอบ...", fontSize = 11.sp)
                } else {
                  Text("ทดสอบการเชื่อมต่อโมเดล", fontSize = 11.sp)
                }
              }

              if (testResult != null) {
                Text(
                  text = testResult ?: "",
                  color = if (testSuccess == true) SuccessGreen else DangerRed,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Save & Dismiss Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextSecondary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("ยกเลิก")
          }

          Button(
            onClick = {
              val newConfig = currentConfig.copy(
                selectedModel = selectedModel,
                customApiKey = customApiKey,
                fallbackApiKey = fallbackApiKey,
                customEndpointUrl = customEndpointUrl,
                isSmartFilterEnabled = isSmartFilterEnabled,
                isTtsEnabled = isTtsEnabled,
                isVoiceCommandEnabled = isVoiceCommandEnabled,
                isOledStealthMode = isOledStealthMode
              )
              onSaveConfig(newConfig)
              onDismiss()
            },
            modifier = Modifier.weight(1f).testTag("save_ai_settings_button"),
            colors = ButtonDefaults.buttonColors(containerColor = PackAPunchCyan, contentColor = DarkBackground),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("บันทึกการตั้งค่า", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
