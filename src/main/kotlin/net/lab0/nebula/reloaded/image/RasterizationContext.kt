package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.tree.Rectangle

class RasterizationContext(
    val viewport: Rectangle,
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
        convertPlanToImage(real, img)
      }

  fun convertPlanToImage(real: Double, img: Double) =
      ImageCoordinates(
          (((real - viewport.midX) / pixelSide) + xCenter).toInt(),
          (yCenter - ((img - viewport.midY) / pixelSide)).toInt()
      )

  fun convertPlanRealToImageX(real: Double) =
      (((real - viewport.midX) / pixelSide) + xCenter).toInt()

  fun convertPlanImgToImageY(img: Double) =
      (yCenter - ((img - viewport.midY) / pixelSide)).toInt()


  fun convert(coordinates: ImageCoordinates) =
      with(coordinates) { convertImageToPlan(x, y) }

  fun convertImageToPlan(x: Int, y: Int) =
      PlanCoordinates(
          convertImageXToPlanReal(x),
          convertImageYToPlanImg(y)
      )

  fun convertImageXToPlanReal(x: Int) =
      pixelSide * (x - xCenter) + viewport.midX

  fun convertImageYToPlanImg(y: Int) =
      -pixelSide * (y - yCenter) + viewport.midY
}
