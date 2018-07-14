package net.lab0.nebula.reloaded.image

data class ImageCoordinates(
    val x: Int,
    val y: Int,
    val context: CoordinatesContext
) {
  fun toPlan(): PlanCoordinates {
    with(context) {
      return PlanCoordinates(
          pixelSide * (x - imageCenterX) + planCenterX,
          -pixelSide * (y - imageCenterY) + planCenterY
      )
    }
  }
}
