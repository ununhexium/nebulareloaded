package net.lab0.nebula.reloaded.image.color

import java.awt.Color

object GrayScale : ColorScheme by MandelbrotGrayScale() {
  override fun toString() = javaClass.simpleName
}

object IntenseHue : ColorScheme by MandelbrotHue(Color.BLUE) {
  override fun toString() = javaClass.simpleName
}


object SoftHue : ColorScheme by MandelbrotHue(Color(80, 80, 200)) {
  override fun toString() = javaClass.simpleName
}

object PastelHue : ColorScheme by MandelbrotHue(Color(80, 80, 120)) {
  override fun toString() = javaClass.simpleName
}

object ColorSchemes : List<ColorScheme> by listOf(
    GrayScale, IntenseHue, SoftHue, PastelHue
)
