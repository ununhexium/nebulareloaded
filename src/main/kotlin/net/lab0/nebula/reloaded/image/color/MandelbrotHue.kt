package net.lab0.nebula.reloaded.image.color

import net.lab0.nebula.reloaded.ui.RenderingContext
import java.awt.Color
import java.util.LongSummaryStatistics

class MandelbrotHue(baseColor: Color) : ColorScheme {

  val BASE_COLOR = FloatArray(3)

  init {
    Color.RGBtoHSB(baseColor.red, baseColor.green, baseColor.blue, BASE_COLOR)
  }

  override fun computeColor(
      color: IntArray,
      iterations: Long,
      context: RenderingContext,
      stats: LongSummaryStatistics
  ) {
    val colorArray = Color(
        Color.HSBtoRGB(
            BASE_COLOR[0] + iterations / 180.0f,
            BASE_COLOR[1],
            BASE_COLOR[2]
        )
    ).toColorArray()

    colorArray.forEachIndexed { index, _ ->
      color[index] = colorArray[index]
    }
  }
}
