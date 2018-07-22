package net.lab0.nebula.reloaded.compute.nebulabrot

import net.lab0.nebula.reloaded.compute.mandelbrot.DefaultMandelbrotComputeEngine
import net.lab0.nebula.reloaded.image.RasterizationContext
import net.lab0.nebula.reloaded.tree.ComplexPoint
import net.lab0.nebula.reloaded.ui.RenderingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import kotlin.streams.asStream

class ParallelStreamNebulabrotComputeEngine : NebulabrotComputeEngine {

  companion object {
    private val log: Logger by lazy {
      LoggerFactory
          .getLogger(this::class.java.name)
    }
  }

  private var stop = false

  private val mandelbrotComputeEngine = DefaultMandelbrotComputeEngine()

  override fun compute(
      points: Sequence<ComplexPoint>,
      context: RenderingContext,
      lowIterationLimit: Long
  ) {

    val rasterizationContext = RasterizationContext(
        context.viewport,
        context.width,
        context.height
    )

    val resultMatrix = Array(context.height) { LongArray(context.width) { 0 } }

    points
        .asStream()
        .parallel()
        .filter {
          !stop
        }
        .map {
          it to mandelbrotComputeEngine.iterationsAt(
              it.real,
              it.img,
              context.iterationLimit
          )
        }
        .filter {
          it.second < context.iterationLimit &&
          it.second > lowIterationLimit
        }
        .forEach {
          compute(
              it.first.real,
              it.first.img,
              it.second,
              rasterizationContext,
              resultMatrix
          )
        }

    log.debug("Coloring nebula")
    val min = resultMatrix.minBy { it.min()!! }!!.min()!!
    val max = resultMatrix.maxBy { it.max()!! }!!.max()!!
    val average = resultMatrix.map { it.average() }.average().toLong()
    val rangeCandidate = max - min
    val range = if (rangeCandidate == 0L) 1.0
    else Math.log(rangeCandidate.toDouble())

    val image = BufferedImage(
        context.width,
        context.height,
        BufferedImage.TYPE_INT_RGB
    )
    val raster = image.raster

    val color = DoubleArray(3)
    resultMatrix.forEachIndexed { lineIndex, line ->
      line.forEachIndexed { pixelIndex, pixel ->
        val colorValue = 255f * Math.log((pixel - min).toDouble()) / range
        color[0] = colorValue
        color[1] = colorValue
        color[2] = colorValue
        raster.setPixel(pixelIndex, lineIndex, color)
      }
    }

    image.data = raster
    context.rendering = image
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

      val x = context.convertPlanRealToImageX(real2)
      val y = context.convertPlanImgToImageY(img2)
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
