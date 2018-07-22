package net.lab0.nebula.reloaded.image.color

import net.lab0.nebula.reloaded.ui.RenderingContext
import java.awt.Color
import java.util.LongSummaryStatistics

class MandelbrotGrayScale(insideSetColor: Color = Color.BLACK) : ColorScheme {

  private val insideSetColorArray = insideSetColor.toColorArray()

  override fun computeColor(
      color: IntArray,
      iterations: Long,
      context: RenderingContext,
      stats: LongSummaryStatistics
  ) {
    val scaled = iterations * 255 / context.iterationLimit
    if (scaled == 255L) {
      color.forEachIndexed { index, _ ->
        color[index] = insideSetColorArray[index]
      }
    }
    else {
      color.forEachIndexed { index, _ ->
        color[index] = scaled.toInt()
      }
    }
  }
}
