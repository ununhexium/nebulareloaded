package net.lab0.nebula.reloaded.compute.nebulabrot

import net.lab0.nebula.reloaded.image.RasterizationContext
import net.lab0.nebula.reloaded.tree.PointWithIterationLimit
import net.lab0.nebula.reloaded.ui.RenderingContext

class DefaultNebulabrotComputeEngine : NebulabrotComputeEngine {

  private var stop = false

  override fun compute(
      points: Iterable<PointWithIterationLimit>,
      context: RenderingContext
  ) {

    val rasterizationContext = RasterizationContext(
        context.viewport,
        context.width,
        context.height
    )

    val resultMatrix = Array(context.height) { LongArray(context.width) { 0 } }

    points.forEach {
      compute(it.x, it.y, it.iterationLimit, rasterizationContext, resultMatrix)
      if (stop) return@forEach
    }
  }

  fun compute(
      real: Double,
      img: Double,
      iterationLimit: Long,
      context: RasterizationContext,
      resultMatrix: Array<LongArray>
  ): Long {
    var real1 = real
    var img1 = img
    var real2: Double
    var img2: Double

    var iter: Long = 0
    while (iter < iterationLimit) {
      real2 = real1 * real1 - img1 * img1 + real
      img2 = 2.0 * real1 * img1 + img

      val x = context.convertPlanToImageX(real2)
      val y = context.convertPlanToImageX(img2)
      if (inBounds(x, y, resultMatrix)) {
        resultMatrix[y][x]++
      }

      real1 = real2
      img1 = img2

      iter++
    }

    return iter
  }

  private fun inBounds(
      x: Int,
      y: Int,
      context: Array<LongArray>
  ) = x >= 0 &&
      x < context[0].size &&
      y >= 0 &&
      y < context.size

  override fun stop() {
    stop = true
  }
}
