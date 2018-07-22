package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeContext
import net.lab0.nebula.reloaded.compute.nebulabrot.DefaultNebulabrotComputeEngine
import net.lab0.nebula.reloaded.compute.nebulabrot.NebulabrotComputeEngine
import net.lab0.nebula.reloaded.image.RasterizationContext
import net.lab0.nebula.reloaded.tree.PointWithIterationLimit
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.concurrent.atomic.AtomicReference

class NebulabrotPanel(computeContextRef: AtomicReference<MandelbrotComputeContext>) :
    FractalPanel(computeContextRef) {

  val computeEngine: NebulabrotComputeEngine = DefaultNebulabrotComputeEngine()

  /**
   * Complex plan points per pixel
   */
  val step = 0.01

  var negativeNebula = false

  override fun doRendering() {
    val computeContext = computeContextRef.get()
    val actualViewport = getActualViewport()
    val rasterizationContext = RasterizationContext(
        actualViewport,
        width,
        height
    )

    val xPointCount = (width / step).toInt()
    val yPointCount = (height / step).toInt()

    val reals = DoubleArray(xPointCount) { 0.0 }
    val imgs = DoubleArray(yPointCount) { 0.0 }

    val iterationLimit = computeContext.tree.metadata.iterationLimit
    val iterations = computeContext.computeEngine
        .iterationsAt(reals, imgs, iterationLimit)

    val candidatePoints = iterations.mapIndexed { index, iteration ->
      PointWithIterationLimit(reals[index], imgs[index], iteration)
    }

    val points = if (negativeNebula) {
      candidatePoints.filter {
        it.iterationLimit >= iterationLimit
      }
    }
    else {
      candidatePoints.filter {
        it.iterationLimit < iterationLimit
      }
    }

    val renderingContext = RenderingContext(
        actualViewport, width, height, -1, computeContext.computeEngine
    )

    computeEngine.compute(points, renderingContext)
  }

  val iterations = Array(0) { LongArray(0) }

  override fun paintComponent(graphics: Graphics) {
    val g2d = graphics as Graphics2D
    g2d.paint = Color.BLACK
    g2d.fillRect(0, 0, width, height)
  }


}