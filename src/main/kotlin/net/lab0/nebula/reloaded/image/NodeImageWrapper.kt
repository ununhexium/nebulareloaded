package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.tree.PayloadStatus.EDGE
import net.lab0.nebula.reloaded.tree.PayloadStatus.INSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.OUTSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.UNDEFINED
import net.lab0.nebula.reloaded.tree.TreeNode
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class NodeImageWrapper(
    val node: TreeNode,
    val width: Int,
    val height: Int,
    /**
     * Percentage of the width / height
     */
    zoomFactor: Double = 0.9,
    val threadshold: Int = 4
) {
  val viewport = PlanViewport(node.position).zoom(zoomFactor)
  val rasterizationContext = RasterizationContext(viewport, width, height)

  fun toImage(): BufferedImage {
    val image = renderMandelbrot()
    renderAreas(image)
    return image
  }

  private fun renderMandelbrot(): BufferedImage {
    val renderer = MandelbrotRenderer(viewport)
    return renderer
        .render(
            width,
            height,
            node.metadata.iterationLimit,
            node.metadata.computeEngine
        )
  }

  private fun renderAreas(image: BufferedImage) {
    val nodes = node.getNodesBreadthFirst { !it.hasChildren() }
    val g = image.graphics as Graphics2D
    nodes.forEach { node ->
      val color = when (node.payload.status) {
        UNDEFINED -> Color(128, 128, 128)
        OUTSIDE -> Color(0, 128, 0)
        INSIDE -> Color(200, 100, 0)
        EDGE -> Color(0, 100, 200)
      }

      val topLeft = rasterizationContext.convert(node.position.topLeft)
      val bottomRight = rasterizationContext.convert(node.position.bottomRight)
      val width = bottomRight.x - topLeft.x
      val height = bottomRight.y - topLeft.y

      if (width > threadshold) {
        paintAsRegular(g, color, topLeft.x, topLeft.y, width, height)
      }
      else {
        paintAsUnavailable(g, topLeft.x, topLeft.y, width, height)
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
}
