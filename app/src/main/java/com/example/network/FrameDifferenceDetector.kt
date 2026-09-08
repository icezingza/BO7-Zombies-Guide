package com.example.network

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs

/**
 * Lightweight Frame Difference Detector to avoid calling the Gemini API when the screen
 * is completely static, saving API tokens, battery, and preventing device overheating.
 */
object FrameDifferenceDetector {

  private const val SAMPLE_SIZE = 32

  /**
   * Compares two bitmaps by scaling them down to 32x32 and calculating the average
   * normalized Manhattan distance between RGB pixel values.
   * Returns a percentage value between 0.0% and 100.0%.
   */
  fun calculateDifferencePercent(prev: Bitmap?, current: Bitmap): Float {
    if (prev == null) return 100.0f // First frame always triggers

    val smallPrev = Bitmap.createScaledBitmap(prev, SAMPLE_SIZE, SAMPLE_SIZE, false)
    val smallCurrent = Bitmap.createScaledBitmap(current, SAMPLE_SIZE, SAMPLE_SIZE, false)

    var totalDiff = 0L
    val totalPixels = SAMPLE_SIZE * SAMPLE_SIZE

    val prevPixels = IntArray(totalPixels)
    val currPixels = IntArray(totalPixels)

    smallPrev.getPixels(prevPixels, 0, SAMPLE_SIZE, 0, 0, SAMPLE_SIZE, SAMPLE_SIZE)
    smallCurrent.getPixels(currPixels, 0, SAMPLE_SIZE, 0, 0, SAMPLE_SIZE, SAMPLE_SIZE)

    for (i in 0 until totalPixels) {
      val pColor = prevPixels[i]
      val cColor = currPixels[i]

      val rDiff = abs(Color.red(pColor) - Color.red(cColor))
      val gDiff = abs(Color.green(pColor) - Color.green(cColor))
      val bDiff = abs(Color.blue(pColor) - Color.blue(cColor))

      totalDiff += (rDiff + gDiff + bDiff)
    }

    // Maximum possible difference across 3 channels is 255 * 3 * totalPixels
    val maxPossibleDiff = 255.0 * 3.0 * totalPixels
    val percentage = (totalDiff / maxPossibleDiff * 100.0).toFloat()

    return percentage
  }
}
