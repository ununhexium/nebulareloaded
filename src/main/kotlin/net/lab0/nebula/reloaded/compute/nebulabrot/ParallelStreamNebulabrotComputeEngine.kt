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
      lowIterationLimit: Long,
      colorBounds: List<Long>,
      interpolation: (Long, Long, Long, Long) -> Long
  ) {

    val rasterizationContext = RasterizationContext(
        context.viewport,
        context.width,
        context.height
    )

    val resultMatrices = Array(colorBounds.size) {
      Array(context.height) {
        LongArray(context.width) { 0 }
      }
    }

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
              resultMatrices[getResultMatrixIndex(colorBounds, it.second)]
          )
        }

    log.debug("Coloring nebula")
    resultMatrices.forEach {
      mapToPixelValue(it, context, interpolation)
    }

    val image = BufferedImage(
        context.width,
        context.height,
        BufferedImage.TYPE_INT_RGB
    )
    val raster = image.raster

    val color = IntArray(3)
    (0 until context.height).forEach { y ->
      (0 until context.width).forEach { x ->
        color[0] = resultMatrices[1][y][x].toInt()
        color[1] = resultMatrices[2][y][x].toInt()
        color[2] = resultMatrices[0][y][x].toInt()
        raster.setPixel(x, y, color)
      }
    }

    image.data = raster
    context.rendering = image
  }

  private fun mapToPixelValue(
      it: Array<LongArray>,
      context: RenderingContext,
      iterpolation: (Long, Long, Long, Long) -> Long
  ) {
    val min = it.minBy { it.min()!! }!!.min()!!
    val max = it.maxBy { it.max()!! }!!.max()!!
    val average = it.map { it.average() }.average().toLong()

    (0 until context.height).forEach { y ->
      (0 until context.width).forEach { x ->
        val pixel = it[y][x]
        it[y][x] = iterpolation(min, average, max, pixel)
      }
    }
  }

  private fun getResultMatrixIndex(
      colorBounds: List<Long>,
      iterationLimit: Long
  ) = colorBounds.indexOfFirst { it >= iterationLimit }

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
