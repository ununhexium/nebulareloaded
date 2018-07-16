package net.lab0.nebula.reloaded.image

class RasterizationContext(
    val viewport: PlanViewport,
    val width: Int,
    val height: Int
) {
  val ratio
    get() = width.toDouble() / height.toDouble()

  val pixelSide
    get() = if (ratio > 1) {
      viewport.width / width * ratio
    }
    else {
      viewport.width / width
    }

  val xCenter = width / 2

  val yCenter = height / 2

  fun convert(coordinates: PlanCoordinates) =
      with(coordinates) {
        ImageCoordinates(
            (((real - viewport.midX) / pixelSide) + xCenter).toInt(),
            (yCenter - ((img - viewport.midY) / pixelSide)).toInt()
        )
      }

  fun convert(coordinates: ImageCoordinates) =
      with(coordinates) {
        PlanCoordinates(
            pixelSide * (x - xCenter) + viewport.midX,
            -pixelSide * (y - yCenter) + viewport.midY
        )
      }
}
