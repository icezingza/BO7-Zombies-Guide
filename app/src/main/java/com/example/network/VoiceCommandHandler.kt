package com.example.network

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

sealed class VoiceAction {
  object TriggerScan : VoiceAction()
  object PauseLoop : VoiceAction()
  object ResumeLoop : VoiceAction()
  object NextStep : VoiceAction()
  object SaveSymbol : VoiceAction()
  object RequestBossTips : VoiceAction()
  data class Unrecognized(val text: String) : VoiceAction()
}

/**
 * VoiceCommandHandler: Listens for gamer voice commands while playing on PS5
 * so the player doesn't have to take their hands off the controller.
 */
class VoiceCommandHandler(
  private val context: Context,
  private val onActionDetected: (VoiceAction) -> Unit
) {
  private var speechRecognizer: SpeechRecognizer? = null
  private var isListening = false

  companion object {
    private const val TAG = "VoiceCommandHandler"
  }

  fun startListening() {
    if (isListening) return

    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
      Log.w(TAG, "Speech recognition not available on this device.")
      return
    }

    try {
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
          override fun onReadyForSpeech(params: Bundle?) {}
          override fun onBeginningOfSpeech() {}
          override fun onRmsChanged(rmsdB: Float) {}
          override fun onBufferReceived(buffer: ByteArray?) {}
          override fun onEndOfSpeech() {}

          override fun onError(error: Int) {
            Log.d(TAG, "SpeechRecognizer error: $error")
            // Automatically restart listening if still enabled
            if (isListening) {
              restartListening()
            }
          }

          override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = matches?.firstOrNull()?.lowercase() ?: ""
            Log.d(TAG, "Recognized speech: $recognizedText")

            parseCommand(recognizedText)

            if (isListening) {
              restartListening()
            }
          }

          override fun onPartialResults(partialResults: Bundle?) {}
          override fun onEvent(eventType: Int, params: Bundle?) {}
        })
      }

      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
      }

      speechRecognizer?.startListening(intent)
      isListening = true
    } catch (e: Exception) {
      Log.e(TAG, "Error starting voice listener", e)
    }
  }

  private fun parseCommand(text: String) {
    val clean = text.trim()
    when {
      clean.contains("สแกน") || clean.contains("scan") || clean.contains("อ่าน") || clean.contains("ดูจอ") -> {
        onActionDetected(VoiceAction.TriggerScan)
      }
      clean.contains("หยุด") || clean.contains("pause") || clean.contains("พอ") -> {
        onActionDetected(VoiceAction.PauseLoop)
      }
      clean.contains("เริ่ม") || clean.contains("เล่น") || clean.contains("ต่อ") || clean.contains("resume") -> {
        onActionDetected(VoiceAction.ResumeLoop)
      }
      clean.contains("ถัดไป") || clean.contains("ต่อไป") || clean.contains("next") -> {
        onActionDetected(VoiceAction.NextStep)
      }
      clean.contains("จด") || clean.contains("บันทึก") || clean.contains("เซฟ") || clean.contains("save") -> {
        onActionDetected(VoiceAction.SaveSymbol)
      }
      clean.contains("บอส") || clean.contains("boss") || clean.contains("สูตรบอส") || clean.contains("ขอสูตรบอส") -> {
        onActionDetected(VoiceAction.RequestBossTips)
      }
      clean.isNotEmpty() -> {
        onActionDetected(VoiceAction.Unrecognized(clean))
      }
    }
  }

  private fun restartListening() {
    try {
      speechRecognizer?.cancel()
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH")
      }
      speechRecognizer?.startListening(intent)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to restart speech listener", e)
    }
  }

  fun stopListening() {
    isListening = false
    try {
      speechRecognizer?.stopListening()
      speechRecognizer?.cancel()
      speechRecognizer?.destroy()
      speechRecognizer = null
    } catch (e: Exception) {
      Log.e(TAG, "Error stopping speech listener", e)
    }
  }
}
