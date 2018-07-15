package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.mandelbrot.Compute
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster


class MandelbrotRenderer(
    val viewport: PlanViewport
) {
    fun render(
        width: Int,
        height: Int,
        iterationLimit: Long,
        compute: Compute
    ): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        val context = RasterizationContext(viewport, width, height)

        val raster = image.data as WritableRaster

        (0 until raster.height).forEach { y ->
            (0 until raster.width).forEach { x ->
                val plan = context.convert(ImageCoordinates(x, y))
                val iterations = compute
                    .iterationsAt(plan.real, plan.img, iterationLimit)
                val color = computeColor(iterations, iterationLimit)
                raster.setPixel(x, y, color)
            }
        }

        image.data = raster
        return image
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
