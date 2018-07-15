package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.mandelbrot.ComputeEngine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.measureNanoTime


class MandelbrotRenderer(
    val viewport: PlanViewport
) {
    companion object {
        private val log: Logger by lazy {
            LoggerFactory
                .getLogger(this::class.java.name)
        }
    }

    fun render(
        width: Int,
        height: Int,
        iterationLimit: Long,
        computeEngine: ComputeEngine
    ): BufferedImage {
        log.debug("Computing with $computeEngine")

        val start = System.nanoTime()

        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val context = RasterizationContext(viewport, width, height)
        val raster = image.data as WritableRaster

        val reals = DoubleArray(raster.height * raster.width)
        val imgs = DoubleArray(raster.height * raster.width)

        val prepareTime = measureNanoTime {
            prepare(raster, context, reals, imgs)
        }

        val iterations = AtomicReference<LongArray>()
        val computeTime = measureNanoTime {
            iterations
                .set(computeEngine.iterationsAt(reals, imgs, iterationLimit))
        }

        val finishTime = measureNanoTime {
            iterations.get().mapIndexed { index, value ->
                val color = computeColor(value, iterationLimit)
                raster
                    .setPixel(index % raster.width, index / raster.width, color)
            }
        }

        image.data = raster

        val end = System.nanoTime()

        fun Long.toMillis() = this / 1_000_000

        log.debug(
            "Computation took ${(end - start).toMillis()}. " +
                "Prepare=${prepareTime.toMillis()}, " +
                "compute=${computeTime.toMillis()}, " +
                "finish=${finishTime.toMillis()}"
        )
        return image
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

    private fun computeColor(iterations: Long, iterationLimit: Long): IntArray {
        return when (iterations) {
            iterationLimit -> IntArray(3) { 255 }
            else -> IntArray(3) {
                (iterations * 255 / 2 / iterationLimit).toInt()
            }
        }
    }
}

