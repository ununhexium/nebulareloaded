package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.image.color.ColorScheme
import net.lab0.nebula.reloaded.ui.RenderingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.measureNanoTime


class MandelbrotRenderer(
    val context: RenderingContext,
    val colorScheme: ColorScheme
) {
  companion object {
    private val log: Logger by lazy {
      LoggerFactory
          .getLogger(this::class.java.name)
    }
  }

  fun render() {
    with(context) {
      log.debug("Computing with $computeEngine")

      val start = System.nanoTime()

      val rasterizationContext = RasterizationContext(context.viewport, width, height)

      val reals = with(rasterizationContext) { DoubleArray(height * width) }
      val imgs = with(rasterizationContext) { DoubleArray(height * width) }

      val prepareTime = measureNanoTime {
        prepare(rasterizationContext, reals, imgs)
      }

      val iterationsRef = AtomicReference<LongArray>()
      val computeTime = measureNanoTime {
        iterationsRef.set(
            computeEngine.iterationsAt(reals, imgs, iterationLimit)
        )
      }

      val finishTime = measureNanoTime {
        val iterations = iterationsRef.get()
        colorScheme.colorize(iterations, context)
      }


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
      context: RasterizationContext,
      reals: DoubleArray,
      imgs: DoubleArray
  ) {
    with(context) {
      (0 until height).forEach { y ->
        (0 until width).forEach { x ->
          val plan = context.convert(ImageCoordinates(x, y))
          reals[y * width + x] = plan.real
          imgs[y * width + x] = plan.img
        }
      }
    }
  }
}

