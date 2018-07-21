package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.ui.RenderingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.measureNanoTime


class MandelbrotRenderer(
    val context: RenderingContext
) {
  companion object {
    private val log: Logger by lazy {
      LoggerFactory
          .getLogger(this::class.java.name)
    }

    val BLACK = IntArray(3) { 0 }
    val GRAY = IntArray(3) { 128 }
  }

  fun render() {
    with(context) {
      log.debug("Computing with $computeEngine")

      val start = System.nanoTime()

      val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      val context = RasterizationContext(context.viewport, width, height)
      val raster = image.data as WritableRaster

      val reals = DoubleArray(raster.height * raster.width)
      val imgs = DoubleArray(raster.height * raster.width)

      val prepareTime = measureNanoTime {
        prepare(raster, context, reals, imgs)
      }

      val iterationsRef = AtomicReference<LongArray>()
      val computeTime = measureNanoTime {
        iterationsRef
            .set(computeEngine.iterationsAt(reals, imgs, iterationLimit))
      }

      val finishTime = measureNanoTime {
        val iterations = iterationsRef.get()
        iterations.mapIndexed { index, value ->
          val color = computeColor(value, iterationLimit)
          raster.setPixel(
              index % raster.width,
              index / raster.width,
              color
          )
        }
      }

      image.data = raster
      rendering = image

      val end = System.nanoTime()

      fun Long.toMillis() = this / 1_000_000

      log.debug(
          "Computation took ${(end - start).toMillis()}. " +
              "Prepare=${prepareTime.toMillis()}, " +
              "compute=${computeTime.toMillis()}, " +
              "finish=${finishTime.toMillis()}"
      )
    }
  }

  private fun prepare(
      raster: WritableRaster,
      context: RasterizationContext,
      reals: DoubleArray,
      imgs: DoubleArray
  ) {
    (0 until raster.height).forEach { y ->
      (0 until raster.width).forEach { x ->
        val plan = context.convert(ImageCoordinates(x, y))
        reals[y * raster.width + x] = plan.real
        imgs[y * raster.width + x] = plan.img
      }
    }
  }

  private inline fun computeColor(
      iterations: Long,
      iterationLimit: Long
  ): IntArray {
    val scaled = iterations * 255 / iterationLimit
    return if (scaled == 255L) BLACK else IntArray(3) { scaled.toInt() }
  }
}

