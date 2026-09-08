package com.example.network

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

/**
 * AudioCoPilot: Provides real-time hands-free speech alerts for tactical instructions
 * and threat warnings so players don't have to look away from their PS5 screen.
 */
class AudioCoPilot(context: Context) {

  private var tts: TextToSpeech? = null
  private var isInitialized = false
  private var lastSpokenText: String = ""
  private var lastSpokenTime: Long = 0L

  companion object {
    private const val TAG = "AudioCoPilot"
    private const val MIN_REPEAT_INTERVAL_MS = 6000L // Don't repeat identical alert within 6 seconds
  }

  init {
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        val thaiLocale = Locale.forLanguageTag("th-TH")
        val result = tts?.setLanguage(thaiLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Log.w(TAG, "Thai language is not supported for TTS, falling back to default locale.")
          tts?.setLanguage(Locale.getDefault())
        }
        tts?.setSpeechRate(1.05f) // Slightly faster for urgent combat callouts
        tts?.setPitch(1.0f)
        isInitialized = true
        Log.d(TAG, "AudioCoPilot TTS initialized successfully.")
      } else {
        Log.e(TAG, "Failed to initialize TextToSpeech: status $status")
      }
    }
  }

  /**
   * Speaks out tactical advice or alerts if conditions are met and not redundant.
   */
  fun speakTacticalAdvice(text: String, isCritical: Boolean = false, force: Boolean = false) {
    if (!isInitialized || text.isBlank()) return

    val cleanText = text.replace(Regex("[*#_`\\[\\]]"), "").trim()
    val now = System.currentTimeMillis()

    // Skip if it's the exact same phrase spoken recently unless it's critical
    if (!force && cleanText == lastSpokenText && (now - lastSpokenTime) < MIN_REPEAT_INTERVAL_MS) {
      return
    }

    lastSpokenText = cleanText
    lastSpokenTime = now

    val queueMode = if (isCritical) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
    val utteranceId = "tactical_${System.currentTimeMillis()}"

    tts?.speak(cleanText, queueMode, null, utteranceId)
  }

  fun stop() {
    tts?.stop()
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
    isInitialized = false
  }
}
