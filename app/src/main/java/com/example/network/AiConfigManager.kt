package com.example.network

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AiConfig(
  val selectedModel: String = "gemini-3.5-flash",
  val customApiKey: String = "",
  val fallbackApiKey: String = "",
  val customEndpointUrl: String = "",
  val isSmartFilterEnabled: Boolean = true,
  val motionThresholdPercent: Float = 5.0f,
  val isTtsEnabled: Boolean = true,
  val isVoiceCommandEnabled: Boolean = false,
  val isOledStealthMode: Boolean = false
) {
  val activeApiKey: String
    get() = if (customApiKey.isNotBlank()) customApiKey else BuildConfig.GEMINI_API_KEY
}

class AiConfigManager(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("ai_copilot_config", Context.MODE_PRIVATE)

  private val _configState = MutableStateFlow(loadConfig())
  val configState: StateFlow<AiConfig> = _configState.asStateFlow()

  companion object {
    val AVAILABLE_MODELS = listOf(
      "gemini-3.5-flash" to "Gemini 3.5 Flash (เร็วที่สุด แนะนำสำหรับ Co-pilot สด)",
      "gemini-3.5-pro" to "Gemini 3.5 Pro (วิเคราะห์ลึก ถอดรหัสภาพซับซ้อน)",
      "gemini-2.5-flash" to "Gemini 2.5 Flash (สำรอง โควตาเสถียร)",
      "gemini-2.5-pro" to "Gemini 2.5 Pro (โมเดลเรือธงรุ่นสำรอง)"
    )
  }

  private fun loadConfig(): AiConfig {
    return AiConfig(
      selectedModel = prefs.getString("selected_model", "gemini-3.5-flash") ?: "gemini-3.5-flash",
      customApiKey = prefs.getString("custom_api_key", "") ?: "",
      fallbackApiKey = prefs.getString("fallback_api_key", "") ?: "",
      customEndpointUrl = prefs.getString("custom_endpoint_url", "") ?: "",
      isSmartFilterEnabled = prefs.getBoolean("smart_filter", true),
      motionThresholdPercent = prefs.getFloat("motion_threshold", 5.0f),
      isTtsEnabled = prefs.getBoolean("tts_enabled", true),
      isVoiceCommandEnabled = prefs.getBoolean("voice_cmd_enabled", false),
      isOledStealthMode = prefs.getBoolean("oled_stealth", false)
    )
  }

  fun updateConfig(newConfig: AiConfig) {
    prefs.edit()
      .putString("selected_model", newConfig.selectedModel)
      .putString("custom_api_key", newConfig.customApiKey)
      .putString("fallback_api_key", newConfig.fallbackApiKey)
      .putString("custom_endpoint_url", newConfig.customEndpointUrl)
      .putBoolean("smart_filter", newConfig.isSmartFilterEnabled)
      .putFloat("motion_threshold", newConfig.motionThresholdPercent)
      .putBoolean("tts_enabled", newConfig.isTtsEnabled)
      .putBoolean("voice_cmd_enabled", newConfig.isVoiceCommandEnabled)
      .putBoolean("oled_stealth", newConfig.isOledStealthMode)
      .apply()
    _uiConfigUpdate(newConfig)
  }

  fun setSelectedModel(model: String) {
    val updated = _configState.value.copy(selectedModel = model)
    updateConfig(updated)
  }

  fun setCustomApiKey(key: String) {
    val updated = _configState.value.copy(customApiKey = key.trim())
    updateConfig(updated)
  }

  fun setFallbackApiKey(key: String) {
    val updated = _configState.value.copy(fallbackApiKey = key.trim())
    updateConfig(updated)
  }

  fun setCustomEndpoint(url: String) {
    val updated = _configState.value.copy(customEndpointUrl = url.trim())
    updateConfig(updated)
  }

  fun toggleSmartFilter() {
    val updated = _configState.value.copy(isSmartFilterEnabled = !_configState.value.isSmartFilterEnabled)
    updateConfig(updated)
  }

  fun toggleTts() {
    val updated = _configState.value.copy(isTtsEnabled = !_configState.value.isTtsEnabled)
    updateConfig(updated)
  }

  fun toggleVoiceCommand() {
    val updated = _configState.value.copy(isVoiceCommandEnabled = !_configState.value.isVoiceCommandEnabled)
    updateConfig(updated)
  }

  fun toggleOledStealthMode() {
    val updated = _configState.value.copy(isOledStealthMode = !_configState.value.isOledStealthMode)
    updateConfig(updated)
  }

  private fun _uiConfigUpdate(newConfig: AiConfig) {
    _configState.value = newConfig
  }
}
