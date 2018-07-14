package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.tree.PayloadStatus.EDGE
import net.lab0.nebula.reloaded.tree.PayloadStatus.INSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.OUTSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.UNDEFINED
import net.lab0.nebula.reloaded.tree.TreeNode
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster

class NodeImageWrapper(
    val node: TreeNode,
    val width: Int,
    val height: Int,
    /**
     * Percentage of the width / height
     */
    margin: Double = 0.1,
    val threadshold: Int = 4
) {

  val ratio = width / height.toDouble()
  val xCenter = width / 2
  val yCenter = height / 2

  // assuming square pixels
  val pixelWidth = if (ratio > 1) {
    node.position.rangeX / width * ratio
  }
  else {
    node.position.rangeX / width
  } / (1 - margin)

  val pixelHeight = if (ratio > 1) {
    node.position.rangeX / height
  }
  else {
    node.position.rangeX / height / ratio
  } / (1 - margin)


  val context = CoordinatesContext(
      pixelHeight,
      node.position.midX,
      node.position.midY,
      xCenter,
      yCenter
  )

  fun toImage(): BufferedImage {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    renderMandelbrot(image)
    renderAreas(image)
    return image
  }

  private fun renderMandelbrot(image: BufferedImage) {
    if (Math.abs((pixelHeight / pixelWidth) - 1) > 0.001) {
      throw IllegalStateException("$pixelHeight and $pixelWidth should be very close to each other. Bug?")
    }

    val raster = image.data as WritableRaster

    (0 until raster.height).forEach { y ->
      (0 until raster.width).forEach { x ->
        val (real, img) = ImageCoordinates(x, y, context).toPlan()
        val iterations = node.metadata.iterationsAt(real, img)
        val color = computeColor(iterations)
        raster.setPixel(x, y, color)
      }
    }

    image.data = raster
  }

  private fun computeColor(iterations: Long): IntArray {
    return when (iterations) {
      node.metadata.iterationLimit -> IntArray(3) { 255 }
      else -> IntArray(3) {
        (iterations * 255 / 2 / node.metadata.iterationLimit).toInt()
      }
    }
  }

  private fun renderAreas(image: BufferedImage) {
    val nodes = node.getNodesBreadthFirst { !it.hasChildren() }
    val g = image.graphics as Graphics2D
    nodes.forEach { node ->
      val color = when (node.payload.status) {
        UNDEFINED -> Color(255, 255, 255)
        OUTSIDE -> Color(0, 128, 0)
        INSIDE -> Color(200, 100, 0)
        EDGE -> Color(0, 100, 200)
      }

      val topLeft = toRaster(node.position.minX, node.position.maxY)
      val bottomRight = toRaster(node.position.maxX, node.position.minY)
      val x = topLeft.first
      val y = topLeft.second
      val width = bottomRight.first - topLeft.first
      val height = bottomRight.second - topLeft.second

      if (width > threadshold) {
        paintAsRegular(g, color, x, y, width, height)
      }
      else {
        paintAsUnavailable(g, x, y, width, height)
      }
    }
  }

  private fun paintAsRegular(
      g: Graphics2D,
      color: Color,
      x: Int,
      y: Int,
      width: Int,
      height: Int
  ) {
    g.paint = color.withAlpha(0.25)
    g.fillRect(x + 1, y + 1, width - 2, height - 2)
    g.paint = color.withAlpha(0.5)
    g.drawRect(x, y, width - 1, height - 1)
  }

  private fun paintAsUnavailable(
      g: Graphics2D,
      x: Int,
      y: Int,
      width: Int,
      height: Int
  ) {
    val color = Color(128, 128, 128)
    g.paint = color.withAlpha(0.5)
    g.fillRect(x, y, width, height)
  }

  /**
   * pixelWidth * (x - xCenter) + node.position.midX
   * pw * (x-xc) + nmx = real
   * pw * (x-xc) = real - nmx
   * (x-xc) = (real - nmx) / pw
   * x = ((real - nmx) / pw) + xc
   */
  private fun toRaster(real: Double, img: Double): Pair<Int, Int> {
    return Pair(
        (((real - node.position.midX) / pixelWidth) + xCenter).toInt(),
        (yCenter - ((img - node.position.midY) / pixelHeight)).toInt()
    )
  }
}
