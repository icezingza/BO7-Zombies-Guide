package com.example.network

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

enum class ThreatLevel {
  LOW,
  CAUTION,
  CRITICAL
}

data class GameplayAnalysisResult(
  val summary: String,
  val threatLevel: ThreatLevel,
  val detectedLocation: String,
  val detectedPuzzles: List<String>,
  val tacticalAdvice: String,
  val rawResponse: String,
  val timestamp: Long = System.currentTimeMillis(),
  val latencyMs: Long = 0,
  val isSimulated: Boolean = false,
  val activeModel: String = "gemini-3.5-flash",
  val wasFallbackTriggered: Boolean = false
)

/**
 * Service to analyze live gameplay frames from CameraX using the Gemini API.
 * Follows the guidelines for REST integration with 60-second timeouts,
 * image optimization (scaling and compression), and secure API key access via BuildConfig.
 */
class GeminiGameplayAnalyzer(
  private val apiKey: String = BuildConfig.GEMINI_API_KEY
) {

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  companion object {
    private const val TAG = "GeminiAnalyzer"
    // Recommended model for multimodal tasks per guidelines
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
  }

  /**
   * Compresses and resizes a bitmap to keep payload size optimal for fast API turnarounds.
   */
  private fun optimizeBitmapForApi(original: Bitmap, maxDimension: Int = 640): String {
    val width = original.width
    val height = original.height
    val scale = if (width > maxDimension || height > maxDimension) {
      val maxSide = maxOf(width, height).toFloat()
      maxDimension / maxSide
    } else {
      1.0f
    }

    val scaledBitmap = if (scale < 1.0f) {
      Bitmap.createScaledBitmap(
        original,
        (width * scale).toInt(),
        (height * scale).toInt(),
        true
      )
    } else {
      original
    }

    val outputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
  }

  /**
   * Analyzes a video frame bitmap via the Gemini API.
   * Dynamically supports custom models, fallback API keys when quota is exceeded (HTTP 429/403),
   * and custom endpoint proxies.
   */
  suspend fun analyzeFrame(
    frameBitmap: Bitmap,
    config: AiConfig? = null,
    customPrompt: String? = null
  ): GameplayAnalysisResult = withContext(Dispatchers.IO) {
    val startTime = System.currentTimeMillis()
    val primaryKey = if (config != null && config.activeApiKey.isNotBlank()) config.activeApiKey else apiKey
    val model = config?.selectedModel?.ifBlank { MODEL_NAME } ?: MODEL_NAME
    val fallbackKey = config?.fallbackApiKey?.trim() ?: ""
    val customEndpoint = config?.customEndpointUrl?.trim() ?: ""

    // If API key is missing or set to placeholder, provide an intelligent simulated response
    if (primaryKey.isBlank() || primaryKey == "MY_GEMINI_API_KEY") {
      Log.w(TAG, "Gemini API key is not configured. Providing fallback analysis.")
      return@withContext generateFallbackAnalysis(startTime, activeModel = model)
    }

    try {
      val base64Image = optimizeBitmapForApi(frameBitmap)

      val prompt = customPrompt ?: """
        You are a real-time tactical AI Co-pilot for a player playing Call of Duty: Black Ops 7 Zombies on the map 'Rex Infernus'.
        Analyze this camera view of the user's TV/monitor screen.
        Identify:
        1. CURRENT_LOCATION: The area shown (e.g., Dravakar Temple, Nyxara, PaP Crypt, Nexus Forge, Boss Lair, or General Tunnel).
        2. THREAT_LEVEL: LOW, CAUTION, or CRITICAL (based on swarm size, boss presence, health).
        3. PUZZLES_OR_SYMBOLS: Any visible puzzle elements, stones, house runes (Moon, Skull, Sun, Serpent, Flame, Dagger, Crown, Eye), or Aether Cube faces.
        4. TACTICAL_ADVICE: Brief actionable instruction in Thai for the player right now.
        
        Respond strictly in this JSON format:
        {
          "summary": "Short 1-line tactical summary",
          "threatLevel": "LOW | CAUTION | CRITICAL",
          "location": "Name of area",
          "puzzles": ["Pillars/Symbols detected or None"],
          "tacticalAdvice": "Clear Thai command for what to do next"
        }
      """.trimIndent()

      // Construct JSON payload
      val rootJson = JSONObject()
      val contentsArray = JSONArray()
      val contentObj = JSONObject()
      val partsArray = JSONArray()

      // Text prompt part
      val textPart = JSONObject()
      textPart.put("text", prompt)
      partsArray.put(textPart)

      // Image part
      val imagePart = JSONObject()
      val inlineDataObj = JSONObject()
      inlineDataObj.put("mimeType", "image/jpeg")
      inlineDataObj.put("data", base64Image)
      imagePart.put("inlineData", inlineDataObj)
      partsArray.put(imagePart)

      contentObj.put("parts", partsArray)
      contentsArray.put(contentObj)
      rootJson.put("contents", contentsArray)

      // Request configuration
      val genConfig = JSONObject()
      genConfig.put("temperature", 0.4)
      rootJson.put("generationConfig", genConfig)

      val baseUrl = if (customEndpoint.isNotBlank()) {
        customEndpoint.removeSuffix("/")
      } else {
        BASE_URL
      }

      var requestUrl = "$baseUrl/$model:generateContent?key=$primaryKey"
      val requestBody = rootJson.toString().toRequestBody(jsonMediaType)

      var request = Request.Builder()
        .url(requestUrl)
        .post(requestBody)
        .addHeader("Content-Type", "application/json")
        .build()

      var response = okHttpClient.newCall(request).execute()
      var usedFallback = false

      // Automatic Fallback Check: If Quota/Rate Limit (429 or 403) and fallback key exists
      if ((response.code == 429 || response.code == 403) && fallbackKey.isNotBlank() && fallbackKey != primaryKey) {
        Log.w(TAG, "Primary API Key quota exhausted (HTTP ${response.code}). Automatically switching to Fallback API Key!")
        requestUrl = "$baseUrl/$model:generateContent?key=$fallbackKey"
        request = Request.Builder()
          .url(requestUrl)
          .post(requestBody)
          .addHeader("Content-Type", "application/json")
          .build()
        response.close()
        response = okHttpClient.newCall(request).execute()
        usedFallback = true
      }

      val latency = System.currentTimeMillis() - startTime

      if (!response.isSuccessful) {
        val errorBody = response.body?.string() ?: "Unknown error"
        Log.e(TAG, "Gemini API request failed (${response.code}): $errorBody")
        return@withContext generateFallbackAnalysis(startTime, errorReason = "HTTP ${response.code}", activeModel = model)
      }

      val responseBodyString = response.body?.string() ?: ""
      val responseJson = JSONObject(responseBodyString)
      val candidates = responseJson.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val content = firstCandidate?.optJSONObject("content")
      val parts = content?.optJSONArray("parts")
      val textResponse = parts?.optJSONObject(0)?.optString("text") ?: ""

      parseJsonResponse(textResponse, latency, model, usedFallback)
    } catch (e: Exception) {
      Log.e(TAG, "Exception during Gemini frame analysis", e)
      generateFallbackAnalysis(startTime, errorReason = e.localizedMessage, activeModel = model)
    }
  }

  private fun parseJsonResponse(
    rawText: String,
    latency: Long,
    activeModel: String = MODEL_NAME,
    wasFallbackTriggered: Boolean = false
  ): GameplayAnalysisResult {
    try {
      // Find JSON block if wrapped in markdown ```json ... ```
      val cleanJson = if (rawText.contains("{") && rawText.contains("}")) {
        val start = rawText.indexOf("{")
        val end = rawText.lastIndexOf("}") + 1
        rawText.substring(start, end)
      } else {
        rawText
      }

      val json = JSONObject(cleanJson)
      val summary = json.optString("summary", "วิเคราะห์หน้าจอสำเร็จ")
      val threatStr = json.optString("threatLevel", "LOW").uppercase()
      val threat = when {
        threatStr.contains("CRITICAL") -> ThreatLevel.CRITICAL
        threatStr.contains("CAUTION") -> ThreatLevel.CAUTION
        else -> ThreatLevel.LOW
      }
      val location = json.optString("location", "Rex Infernus Zone")
      val puzzlesList = mutableListOf<String>()
      val puzzlesArray = json.optJSONArray("puzzles")
      if (puzzlesArray != null) {
        for (i in 0 until puzzlesArray.length()) {
          puzzlesList.add(puzzlesArray.getString(i))
        }
      } else {
        val singlePuzzle = json.optString("puzzles", "")
        if (singlePuzzle.isNotBlank()) puzzlesList.add(singlePuzzle)
      }
      val advice = json.optString("tacticalAdvice", "เคลื่อนที่ต่อเนื่องและรักษาระยะห่าง")

      return GameplayAnalysisResult(
        summary = summary,
        threatLevel = threat,
        detectedLocation = location,
        detectedPuzzles = puzzlesList,
        tacticalAdvice = advice,
        rawResponse = rawText,
        latencyMs = latency,
        isSimulated = false,
        activeModel = activeModel,
        wasFallbackTriggered = wasFallbackTriggered
      )
    } catch (e: Exception) {
      Log.w(TAG, "Failed to parse structured JSON, returning raw text", e)
      return GameplayAnalysisResult(
        summary = "ตรวจจับภาพหน้าจอเกมสด",
        threatLevel = ThreatLevel.LOW,
        detectedLocation = "Rex Infernus",
        detectedPuzzles = listOf("ตรวจสอบสัญลักษณ์บนจอ"),
        tacticalAdvice = rawText.take(150),
        rawResponse = rawText,
        latencyMs = latency,
        isSimulated = false,
        activeModel = activeModel,
        wasFallbackTriggered = wasFallbackTriggered
      )
    }
  }

  /**
   * Fallback generator when offline, testing, or before API key is entered.
   */
  private fun generateFallbackAnalysis(
    startTime: Long,
    errorReason: String? = null,
    activeModel: String = MODEL_NAME
  ): GameplayAnalysisResult {
    val latency = System.currentTimeMillis() - startTime
    val mockLocations = listOf(
      "Dravakar Temple Entrance",
      "Crypt of the Pack-a-Punch",
      "Nexus Forge Courtyard",
      "Void Rift Causeway",
      "The Warden's Sanctum"
    )
    val mockAdvice = listOf(
      "พบสัญลักษณ์ Exfil ตรากะโหลกบนเสาหินด้านขวา - แนะนำเล็งยิงยืนยัน",
      "มีฝูง Blight Spiders เข้ามาทางซ้าย - ให้ถอยไปคุมระยะที่บันได",
      "กล่อง Aether Cube แสดงลูกศรสีน้ำเงินชี้บน - หมุนแกน X ตามเข็ม",
      "วิหาร Nyxara มีสายฟ้าสถิตแล้ว - สามารถเดินหน้าไปยังวิหาร Caltheris",
      "บอส Warden กำลังชาร์จ Void Burst - ให้หลบหลังแนวเสาหินทันที"
    )

    val randomIndex = (System.currentTimeMillis() % mockLocations.size).toInt()
    val threat = when (randomIndex % 3) {
      0 -> ThreatLevel.LOW
      1 -> ThreatLevel.CAUTION
      else -> ThreatLevel.CRITICAL
    }

    val note = if (errorReason != null) " [โหมดออฟไลน์/ทดสอบ: $errorReason]" else " [จำลองการวิเคราะห์สด]"

    return GameplayAnalysisResult(
      summary = "AI สแกนจอ PS5: ${mockLocations[randomIndex]}$note",
      threatLevel = threat,
      detectedLocation = mockLocations[randomIndex],
      detectedPuzzles = listOf("เสาหิน Exfil: สัญลักษณ์ตรวจพบ #${(randomIndex % 4) + 1}"),
      tacticalAdvice = mockAdvice[randomIndex],
      rawResponse = "Simulated Live Intel Feed for Rex Infernus",
      latencyMs = latency,
      isSimulated = true,
      activeModel = activeModel,
      wasFallbackTriggered = false
    )
  }
}
