package net.lab0.nebula.reloaded.image.color

import net.lab0.nebula.reloaded.ui.RenderingContext
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.LongSummaryStatistics

interface ColorScheme {
  fun colorize(pixels: LongArray, context: RenderingContext) {
    with(context) {
      val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      val raster = image.raster

      val stats = LongSummaryStatistics()
      pixels.forEach {
        stats.accept(it)
      }

      val color = IntArray(3)
      pixels.mapIndexed { index, value ->
        computeColor(color, value, context, stats)
        raster.setPixel(
            index % width,
            index / width,
            color
        )
      }

      image.data = raster
      context.rendering = image
    }
  }

  /**
   * @param color: The array in which the computed color will be stored
   */
  fun computeColor(
      color: IntArray,
      iterations: Long,
      context: RenderingContext,
      stats: LongSummaryStatistics
  )

  fun Color.toColorArray(): IntArray {
    return IntArray(3) {
      when (it) {
        0 -> this.red
        1 -> this.green
        2 -> this.blue
        else -> throw IllegalStateException("Nope. Something is wrong here.")
      }
    }
  }
}